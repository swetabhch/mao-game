import './OpponentPlayer.css'
import React from "react";

// component for holding the visualization for opponent players

function OpponentPlayer(props) {

    let cards = [];
    //aesthetic display of number of cards opponent is holding
    for (let i = 0; i < Math.min(props.numCards, props.maxCards); i++) {
        cards.push(<img className={'card'} src='cards/Blue_Back.svg' alt='Blue_Back' key={i}/>)
    }

    //determines whether or not to show turn indicator
    const showHighlight = props.usingTurnHighlights && props.isTurn;

    return (
        <div className={'opponent-player'} id={props.id}>
            <div style={{display: 'flex', flexDirection: 'row', justifyContent: 'center'}}>
                <span className="dot" style={{visibility: 'hidden'}}/>
                <p className={'opponent-player-name'}>{props.username}</p>
                <span className="dot" style={{visibility: showHighlight ? '' : 'hidden'}}/>
            </div>
            <div className="hand hhand-compact">
                {cards}
            </div>
            <p className={'opponent-num-cards'}>{props.numCards}</p>
        </div>
    );
}

export default OpponentPlayer;