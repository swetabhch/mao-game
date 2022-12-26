import './Lobby.css'
import GeneralLobby from "./GeneralLobby";

// wrapper component to give player props to the lobby

function LobbyPlayer(props) {
    return (
        <GeneralLobby
    isHost={false}
    location={props.location}
    />
    )
}
export default LobbyPlayer;
