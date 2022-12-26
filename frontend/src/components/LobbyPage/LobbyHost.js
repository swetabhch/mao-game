import './Lobby.css'
import GeneralLobby from "./GeneralLobby";

// wrapper component to give host props to the lobby

function LobbyHost(props) {
    return (
        <GeneralLobby
    isHost={true}
    location={props.location}
    />
    )
}

export default LobbyHost;
