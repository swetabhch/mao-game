import './PlayerList.css'
import {socket} from '../../../index.js'
import {CLIENT_MESSAGE} from '../../../utils/constants.js'

// components for displaying each player name
//  host view has the option to kick players from the lobby

function PlayerName(props) {

    const kickPlayer = (playerId) => {
        const toSend = {
            type: CLIENT_MESSAGE.KICK_PLAYER,
            payload: {
                playerId: playerId,
            }
        }
        socket.send(JSON.stringify(toSend));
    }

    return (
        <div className={'player-name'} style={props.isMe ? {color: 'green'} : {}}>
            {(props.hostView && !props.isMe) ?
                <p className={'X'} onClick={() => kickPlayer(props.player.id)}>X</p> : <></>}
            {props.player.username} {props.player.isHost ? "(Host)" : ''}
        </div>
    );
}

export default PlayerName