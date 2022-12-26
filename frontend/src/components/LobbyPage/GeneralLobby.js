import './Lobby.css'
import {Link, useHistory} from "react-router-dom";
import PlayerList from "./PlayerList/PlayerList";
import RoomCode from "./RoomCode";
import NumberSettings from "./NumberRules/NumberSettings";
import ButtonSettings from "./ButtonRules/ButtonSettings";
import CustomRules from "./CustomRules/CustomRules";
import {useState, useEffect} from "react";
import {socket} from '../../index.js'
import {SERVER_MESSAGE, CLIENT_MESSAGE} from '../../utils/constants.js'
import Instructions from "./Instructions";

// component for holding the general lobby view

function GeneralLobby(props) {

    const playerId = props.location.playerId;
    const [players, setPlayers] = useState(props.location.players);
    const [stringRules, setStringRules] = useState([]);

    const loadedSettings = props.location.settings;

    //states to hold all rule options for the host
    const [numPlayers, setNumPlayers] = useState(loadedSettings.numPlayers);
    const [numDecks, setNumDecks] = useState(loadedSettings.numDecks);
    const [usingAIPlayers, setUsingAIPlayers] = useState(loadedSettings.usingAIPlayers);
    const [aiDifficulty, setAIDifficulty] = useState(loadedSettings.aiDifficulty);
    const [usingCustomRules, setUsingCustomRules] = useState(loadedSettings.usingCustomRules);
    const [customRules, setCustomRules] = useState(loadedSettings.customRules);
    const [usingTurnHighlights, setUsingTurnHighlights] = useState(loadedSettings.usingTurnHighlights);

    //states specifically for the number settings
    const numStates = {
        numPlayers: numPlayers, setNumPlayers: setNumPlayers,
        numDecks: numDecks, setNumDecks: setNumDecks,
        aiDifficulty: aiDifficulty, setAIDifficulty: setAIDifficulty,
    }

    //states specifically for the button rules
    const buttonStates = {
        usingCustomRules: usingCustomRules, setUsingCustomRules: setUsingCustomRules,
        usingAIPlayers: usingAIPlayers, setUsingAIPlayers: setUsingAIPlayers,
        usingTurnHighlights: usingTurnHighlights, setUsingTurnHighlights: setUsingTurnHighlights,
    };

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

    //function to update settings display for non-hosts
    useEffect(() => {
        socket.onmessage = msg => {
            // console.log('Message from server in GeneralLobby.js ', msg.data);
            const data = JSON.parse(msg.data);

            if (data.type === SERVER_MESSAGE.PLAYER_JOIN_LOBBY) {
                setPlayers(players => [...players, data.payload.player]);
            } else if (data.type === SERVER_MESSAGE.PLAYER_LEAVE_LOBBY) {
                if (data.payload.player.id === playerId) {
                    history.push('/');
                    alert('You were kicked from the lobby!');
                } else if (data.payload.player.isHost) {
                    history.push('/');
                    alert('The host left the lobby!');
                } else {
                    setPlayers(players => players.filter((p) => p.id !== data.payload.player.id));
                }
            } else if (data.type === SERVER_MESSAGE.UPDATE_SETTINGS) {
                // this message should only be sent to non-hosts to prevent infinite useEffect loops
                const updatedSettings = data.payload.settings;
                setNumPlayers(updatedSettings.numPlayers);
                setNumDecks(updatedSettings.numDecks);
                setUsingAIPlayers(updatedSettings.usingAIPlayers);
                setAIDifficulty(updatedSettings.aiDifficulty);
                setUsingCustomRules(updatedSettings.usingCustomRules);
                setCustomRules(updatedSettings.customRules);
                setUsingTurnHighlights(updatedSettings.usingTurnHighlights);
            } else if (data.type === SERVER_MESSAGE.START_GAME) {
                history.push({
                    pathname: props.isHost ? '/host' : '/player',
                    playerId: playerId,
                    players: data.payload.players,
                    turnIndex: data.payload.turnIndex,
                    startingCard: data.payload.startingCard,
                    formedRules: data.payload.formedRules,
                    stringRules: stringRules,
                    usingTurnHighlights: usingTurnHighlights,
                });
            }
        };
    })

    //function to update settings in the backend whenever they are changed by the host
    useEffect(() => {
        if (props.isHost) {
            const toSend = {
                type: CLIENT_MESSAGE.UPDATE_SETTINGS,
                payload: {
                    settings: {
                        numPlayers: numPlayers,
                        numDecks: numDecks,
                        usingAIPlayers: usingAIPlayers,
                        aiDifficulty: aiDifficulty,
                        usingCustomRules: usingCustomRules,
                        customRules: customRules,
                        usingTurnHighlights: usingTurnHighlights,
                    }
                }
            }
            socket.send(JSON.stringify(toSend));
        }
    }, [numPlayers, numDecks, usingAIPlayers, aiDifficulty,
        usingCustomRules, customRules, usingTurnHighlights, props.isHost]);

    //function to leave the lobby and start the game
    const startGame = () => {
        const toSend = {
            type: CLIENT_MESSAGE.START_GAME,
            payload: {}
        }
        socket.send(JSON.stringify(toSend));
    }

    //function for anyone to leave the lobby
    const leaveLobby = () => {
        const toSend = {
            type: CLIENT_MESSAGE.LEAVE_LOBBY,
            payload: {}
        }
        socket.send(JSON.stringify(toSend));
    }

    const lowerSpaceContent = props.isHost ?
        <CustomRules
            view={usingCustomRules && props.isHost}
            formedRules={customRules}
            setFormedRules={setCustomRules}
            stringRules={stringRules}
            setStringRules={setStringRules}
        /> :

        <Instructions class={'custom-rules-box'}>
        </Instructions>
    return (
        <div className={"lobby"}>
            <h1 className={'lobby-header'}>Settings</h1>
            <PlayerList
                playerId={playerId}
                players={players}
                hostView={props.isHost}/>
            <div className={'rules'}>
                <NumberSettings dynamic={props.isHost} states={numStates} usingAIPlayers={usingAIPlayers}/>
                <ButtonSettings dynamic={props.isHost} states={buttonStates}/>
            </div>
            {lowerSpaceContent}
            <RoomCode code={props.location.lobbyCode}/>

            {props.isHost ?
                <button
                    className={'button'}
                    id={'start-button'}
                    type="button"
                    onClick={startGame}>Start Game</button>
                : <></>
            }
            <Link to="/">
                <button
                    className={'button'}
                    id={'back-button'}
                    type="button"
                    onClick={leaveLobby}>Leave Lobby
                </button>
            </Link>
        </div>
    );
}

export default GeneralLobby;
