import { useHistory } from "react-router-dom";
import Board from "./Board";
import Chat from "./Chat/Chat";
import ModBar from "./HostView/ModBar";
import Notes from "./PlayerView/Notes";
import Message from "./Chat/Message";
import { useState, useEffect } from "react";
import { socket } from '../../index.js'
import {SERVER_MESSAGE, CLIENT_MESSAGE} from '../../utils/constants.js'

// component for holding the generalized full play screen (left sidebar/board/chat)

function GeneralView(props) {

    const SERVER_NAME = 'MaoMom';

    const [players, setPlayers] = useState(props.location.players);
    const [turnIndex, setTurnIndex] = useState(props.location.turnIndex);
    const [lastCardPlayed, setLastCardPlayed] = useState(props.location.startingCard);
    const [pastMessages, setPastMessages] = useState([]);

    const playerId = props.location.playerId;
    const formedRules = props.location.formedRules;
    const stringRules = props.location.stringRules;
    const usingTurnHighlights = props.location.usingTurnHighlights;

    const history = useHistory();

    // keep the Heroku server alive
    useEffect(() => {
        setInterval(() => {
            const toSend = {
                type: CLIENT_MESSAGE.PING,
                payload: {}
            }
            socket.send(JSON.stringify(toSend));
        }, 15000);
    }, [])

    //function for receiving and interpreting messages from the backend
    useEffect(() => {
        socket.onmessage = msg => {
            console.log('Message from server in GeneralView.js ', msg.data);
            const data = JSON.parse(msg.data);

            // add support for players leaving or joining
            if (data.type === SERVER_MESSAGE.CARD_PLAYED) {
                setTurnIndex(data.payload.turnIndex);
                setPlayers(data.payload.players);
                setLastCardPlayed(data.payload.card);

                // only add a chat message if verbalRuleInput is nonempty
                if (data.payload.message !== '') {
                    addMessage(data.payload.message, data.payload.username, 'verbal');
                }
                if (data.payload.hints.length !== 0) {
                    let hintString = '';
                    data.payload.hints.forEach((hint) => {
                        hintString += (' Hint: ' + hint + ' ');
                    });
                    addMessage(`${data.payload.username}, please accept this penalty card. ${hintString}`,
                        SERVER_NAME,
                        'penalty');
                }

                //functionality for someone leaving the lobby
            } else if (data.type === SERVER_MESSAGE.PLAYER_LEAVE_BOARD) {
                if (data.payload.player.isHost) {
                    history.push('/');
                    alert('The host left the lobby!');
                } else {
                    setTurnIndex(data.payload.turnIndex);
                    setPlayers(data.payload.players);
                    addMessage(`${data.payload.player.username} left the game.`, SERVER_NAME, 'server');
                }

                //functionality for incurring penalties
            } else if (data.type === SERVER_MESSAGE.PENALTY_INCURRED) {
                setPlayers(data.payload.players);
                addMessage(`${data.payload.username}, please accept this penalty card. Hint: ${data.payload.hint}`,
                    SERVER_NAME,
                    'penalty');

                //functionality for chat messages
            } else if (data.type === SERVER_MESSAGE.CHAT_MESSAGE) {
                addMessage(data.payload.message, data.payload.username, 'text');

                //functionality for point of order
            } else if (data.type === SERVER_MESSAGE.START_POINT_OF_ORDER) {
                addMessage('Point of order initiated.', SERVER_NAME, 'server');
            } else if (data.type === SERVER_MESSAGE.END_POINT_OF_ORDER) {
                addMessage('Point of order terminated.', SERVER_NAME, 'server');
                // functionality for ending the game
            } else if (data.type === SERVER_MESSAGE.END_GAME) {
                history.push({
                    pathname: '/end',
                    username: data.payload.username,
                });
                // functionality for just drawing a card
            } else if (data.type === SERVER_MESSAGE.CARD_DRAWN) {
                setTurnIndex(data.payload.turnIndex);
                setPlayers(data.payload.players);
            }
        };
    })

    //function for adding necessary messages to the chat
    const addMessage = (message, username, messageType) => {
        const msg = <Message key={pastMessages.length}
            messageText={message}
            username={username}
            messageType={messageType} />
        let allMessages = [...pastMessages];
        allMessages.unshift(msg);
        setPastMessages(allMessages);
    }

    return (
        <div className={'host-view'}>
            {props.isHost ?
                <ModBar formedRules={formedRules}
                    stringRules={stringRules}
                    players={players} />
                : <Notes />
            }
            <Board playerId={playerId}
                players={players}
                lastCardPlayed={lastCardPlayed}
                usingTurnHighlights={usingTurnHighlights}
                turnIndex={turnIndex} />
            <Chat playerId={playerId}>
                {pastMessages}
            </Chat>
        </div>
    );
}

export default GeneralView;