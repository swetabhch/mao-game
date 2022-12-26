import "./LandingPage.css"
import InputBox from './InputBox'
import Toggle from './Toggle.js'
import {useHistory} from "react-router-dom";
import React, {useState, useEffect} from "react";
import {socket} from '../../index.js'
import {SERVER_MESSAGE, CLIENT_MESSAGE} from '../../utils/constants.js'

// component for the start page

function LandingPage() {

    //state for holding names and room code and state of host/player toggle
    const [isHost, setIsHost] = useState(true);
    const [usernameInput, setUsernameInput] = useState('');
    const [lobbyCodeInput, setLobbyCodeInput] = useState('');
    const history = useHistory();

    //filter for profane usernames
    const Filter = require('bad-words'),
        filter = new Filter();

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
            // console.log('Message from server in LandingPage.js ', msg.data);
            const data = JSON.parse(msg.data);

            if (data.type === SERVER_MESSAGE.INVALID_LOBBY_CODE) {
                alert('Invalid invite code!');
            } else if (data.type === SERVER_MESSAGE.LOBBY_LOCKED) {
                alert('Lobby is full or game has started!');
            } else if (data.type === SERVER_MESSAGE.LOBBY_STATE) {
                history.push({
                    pathname: isHost ? '/lobbyHost' : '/lobbyPlayer',
                    playerId: data.payload.playerId,
                    lobbyCode: data.payload.lobbyCode,
                    players: data.payload.players,
                    settings: data.payload.settings,
                });
            }
        };
    }, [isHost, history])

    //function for creating a new lobby (creates a host)
    const createLobby = () => {
        if (usernameInput === '') {
            return;
        } else if (filter.isProfane(usernameInput)) {
            alert("Please don't use profanity in your username!")
            setUsernameInput('');
            return;
        }

        const toSend = {
            type: CLIENT_MESSAGE.CREATE_LOBBY,
            payload: {
                username: usernameInput
            }
        }
        socket.send(JSON.stringify(toSend));
    }

    //function for joining an existing lobby (creates a player)
    const joinLobby = () => {
        if (usernameInput === '' || lobbyCodeInput === '') {
            return;
        }
        const toSend = {
            type: CLIENT_MESSAGE.JOIN_LOBBY,
            payload: {
                username: usernameInput,
                lobbyCode: lobbyCodeInput
            }
        }
        socket.send(JSON.stringify(toSend));
    }

    return (
        <div className={'landing-page'}>
            <h1 className={'header'}>MAO</h1>
            <div className={'row'}>
                <h2 className={'toggle-label'}>Host</h2>
                <Toggle
                    changeHost={setIsHost}
                />
                <h2 className={'toggle-label'}>Join</h2>
            </div>
            <InputBox
                id={"username"}
                placeholder={"Username"}
                value={usernameInput}
                setValue={setUsernameInput}
                adaptive={true}
                view={true}
            />
            <br/>
            <InputBox
                id={"code"}
                placeholder={"Lobby Code"}
                value={lobbyCodeInput}
                setValue={setLobbyCodeInput}
                adaptive={true}
                view={!isHost}
            />
            <br/>
            <button
                id={"submit"}
                type="button"
                onClick={isHost ? createLobby : joinLobby}>
                {isHost ? 'Create Lobby' : 'Join Lobby'}
            </button>
        </div>
    );
}

export default LandingPage;