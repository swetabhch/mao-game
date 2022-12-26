import ButtonRule from "./ButtonRule";

// component for holding all the button rules

function ButtonSettings(props) {
    return (
        <div className={'button-container'}>
            <ButtonRule ruleText={'Custom Rules'}
                        selected={props.states.usingCustomRules}
                        onClick={props.states.setUsingCustomRules}
                        dynamic={props.dynamic}/>
            <ButtonRule ruleText={'AI Players'}
                        selected={props.states.usingAIPlayers}
                        onClick={props.states.setUsingAIPlayers}
                        dynamic={props.dynamic}/>
            <ButtonRule ruleText={'Turn Highlights'}
                        selected={props.states.usingTurnHighlights}
                        onClick={props.states.setUsingTurnHighlights}
                        dynamic={props.dynamic}/>
        </div>
    );
}

export default ButtonSettings;