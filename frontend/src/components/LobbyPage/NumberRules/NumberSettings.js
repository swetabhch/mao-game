import NumberRule from "./NumberRule";

// component for holding all the number rules

function NumberSettings(props) {
    const numPlayersOptions = [1, 2, 3, 4, 5, 6].map(n => ({ value: n, label: n.toString() }));
    const numDecksOptions = [1, 2, 3, 4, 5].map(n => ({ value: n, label: n.toString() }));
    const aiDifficultyOptions = [1, 2, 3].map(n => ({ value: n, label: n.toString() }));

    return (
        <div className={'number-rule-container'}>
            <NumberRule
                ruleText={"Number of Players"}
                ruleAmt={props.states.numPlayers}
                onChange={props.states.setNumPlayers}
                options={numPlayersOptions}
                dynamic={props.dynamic}
                view={true}/>
            <NumberRule
                ruleText={"Number of Decks"}
                ruleAmt={props.states.numDecks}
                onChange={props.states.setNumDecks}
                options={numDecksOptions}
                dynamic={props.dynamic}
                view={true}/>
            <NumberRule
                ruleText={"Difficulty of AI Players"}
                ruleAmt={props.states.aiDifficulty}
                onChange={props.states.setAIDifficulty}
                options={aiDifficultyOptions}
                dynamic={props.dynamic}
                view={props.usingAIPlayers}/>
        </div>
    );
}

export default NumberSettings;