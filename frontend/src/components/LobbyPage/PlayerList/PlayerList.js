import './PlayerList.css';
import PlayerName from "./PlayerName";

// component for holding the left hand current list of players

function PlayerList(props) {
    let players = props.players;

    //map the player info into divs for the player list
    let playerDivs = players.map((player, index) => <PlayerName
            key={index}
            player={player}
            isMe={player.id === props.playerId}
            hostView={props.hostView}
        />
    )

    return (
        <div className={'player-list'}>
            <h1 id={'player-box-title'}>Players</h1>
            {playerDivs}
        </div>
    );
}

export default PlayerList