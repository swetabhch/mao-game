import './Lobby.css';

function Instructions(props) {
    return (
    <div className={props.class} style={{overflow:"auto"}}>
        <h1 className={'instructions-header'}>Instructions:</h1>
        <p className={'instructions-text'}>1) The point of the game is to get rid of all your cards</p>
        <p className={'instructions-text'}>2) The host knows the rest of the rules, but you will have to figure them out over the course of the game</p>
        <p className={'instructions-text'}>3) You will be penalized for breaking rules, but there will be hints to help you figure out what they are</p>
        <p className={'instructions-text'}>Good Luck!</p>
    </div>
    )
}

export default Instructions;