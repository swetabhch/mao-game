import './Board.css'
import OpponentPlayer from "./OpponentPlayer";
import './OpponentPlayer.css';
import React, {useState} from 'react';
import {RANKS, SUITS} from '../../utils/constants.js'
import {socket} from '../../index.js'
import {CLIENT_MESSAGE} from '../../utils/constants.js'

// component for holding the actual board

function Board(props) {

    //for collecting information to be submitted when play is pressed
    const [verbalRuleInput, setVerbalRuleInput] = useState('');
    const [selectedCardIdx, setSelectedCardIdx] = useState(null);
    const [selectedCard, setSelectedCard] = useState(null);

    const players = props.players;
    const lastCardPlayed = props.lastCardPlayed;
    const usingTurnHighlights = props.usingTurnHighlights;
    const turnIndex = props.turnIndex;

    //for information about other players
    const myPlayer = players.filter((p) => p.id === props.playerId)[0];
    const otherPlayers = players.slice(players.indexOf(myPlayer) + 1)
        .concat(players.slice(0, players.indexOf(myPlayer)));
    const numOtherPlayers = otherPlayers.length;

    //set maximum number of visible card backs for aesthetic purposes
    //the number will still display total number of cards
    const maxOpponentCards = (numOtherPlayers < 4) ? 9 : 4;

    //for all the opponent player hands
    const otherPlayerDivs = otherPlayers.map((player, index) => {
        //isTurn used for the turn indicators on the player div
        const isTurn = (player.id === players[turnIndex].id);

        return (<OpponentPlayer
            key={index}
            username={player.username}
            numCards={player.cards.length}
            maxCards={maxOpponentCards}
            usingTurnHighlights={usingTurnHighlights}
            isTurn={isTurn}
            //id of player used to position the hand
            id={'p' + (index + 1).toString() + 'of' + numOtherPlayers.toString()}/>);
    })

    //changing the card rank/suit to the convention used in cardsjs
    const cardToImageName = (card) => {
        return RANKS[card.rank] + SUITS[card.suit];
    }

    //for the visible player card hand
    const cardDivs = myPlayer.cards.map((card, index) => {
        return (<img
                key={index}
                className='card'
                id={(index === selectedCardIdx) ? 'selected-player-card' : ''}
                src={`cards/${cardToImageName(card)}.svg`}
                onClick={() => {
                    setSelectedCardIdx(index);
                    setSelectedCard(card);
                }} alt={cardToImageName(card)}/>
        );
    });

    const drawCard = () => {
        if (myPlayer.id === players[turnIndex].id) {
            //get position of the draw pile
            let hiddenDeck = document.getElementById('hidden-deck')
            let deckRect = hiddenDeck.getBoundingClientRect();

            //get position of player hand
            let playerHand = document.getElementById('player-hand')
            let phRect = playerHand.getBoundingClientRect();

            hiddenDeck.style.transition = '0.5s'
            hiddenDeck.style.transform = `translate(${phRect.left - deckRect.left}px,${(phRect.top - 50) - deckRect.top}px)`;

            //time delay to give the animation enough time to finish before the card gets deleted from the hand
            new Promise(r => setTimeout(r, 500))
                .then(() => {

                    //remove the style from the selected card
                    hiddenDeck.removeAttribute('style')

                    const toSend = {
                        type: CLIENT_MESSAGE.DRAW_CARD,
                        payload: {
                            playerId: props.playerId,
                        }
                    }
                    socket.send(JSON.stringify(toSend));
                })
        }
    }

    //for playing the selected card
    const play = () => {
        if (selectedCard === null) {
            alert('Please select a card to play!');
            return;
        }

        //get position of the discard pile
        let discardPile = document.getElementById('discard-pile')
        let discardRect = discardPile.getBoundingClientRect();

        //get position of the selected card
        let sc = document.getElementById(cardDivs[selectedCardIdx].props.id)
        let scRect = sc.getBoundingClientRect();

        //move the selected card
        sc.style.transition = '0.5s'
        sc.style.transform = `translate(${discardRect.left - scRect.left}px,${discardRect.top - scRect.top - 10}px)`;

        //time delay to give the animation enough time to finish before the card gets deleted from the hand
        new Promise(r => setTimeout(r, 500))
            .then(() => {

                    //remove the style from the selected card
                    sc.removeAttribute('style')

                    const toSend = {
                        type: CLIENT_MESSAGE.PLAY_CARD_WITH_MESSAGE,
                        payload: {
                            playerId: props.playerId,
                            card: selectedCard,
                            message: verbalRuleInput,
                        }
                    }
                    socket.send(JSON.stringify(toSend));
                }
            )

        // clear the input box
        setVerbalRuleInput('');
        setSelectedCardIdx(null);
        setSelectedCard(null);
    }

    //if turn highlight should be shown for the current player
    const showHighlight = usingTurnHighlights && (myPlayer.id === players[turnIndex].id);

    return (
        <div className={'board'}>
            <div className="hand center-cards">
                <img className='card'
                     src={`cards/Blue_Back.svg`}
                     id={'deck'}
                     alt={'deck'}
                     onClick={drawCard}/>
                <img className='card'
                     src={`cards/${cardToImageName(lastCardPlayed)}.svg`}
                     id={'discard-pile'}
                     alt={cardToImageName(lastCardPlayed)}/>
            </div>
            <div className="hand hidden-deck">
                <img className='card'
                     src={`cards/Blue_Back.svg`}
                     id={'hidden-deck'}
                     alt={'deck'}
                     onClick={drawCard}
                     style={{zIndex: 1, display:'hidden'}}/>
            </div>

            <div id={'player-hand'}>
                <div className="hand hhand-compact active-hand" style={{marginTop: '1vh'}}>
                    {cardDivs}
                </div>
                <div style={{display: 'flex', flexDirection: 'row', justifyContent: 'center'}}>
                    <span className="dot" style={{visibility: showHighlight ? '' : 'hidden'}}/>
                    <p id={'current-player-name'}>{myPlayer.username}</p>
                    <span className="dot" style={{visibility: 'hidden'}}/>
                </div>
            </div>
            {otherPlayerDivs}
            <input id={'verbal-rule-input'}
                   type={'text'}
                   placeholder={'anything to say...? (separate phrases with ;)'}
                   onChange={(e) => setVerbalRuleInput(e.target.value)}
                   value={verbalRuleInput}
            />
            <button id={'play-button'}
                    onClick={play}>play
            </button>
        </div>
    );
}

export default Board;