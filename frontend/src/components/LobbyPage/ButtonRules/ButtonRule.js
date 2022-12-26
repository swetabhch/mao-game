import '../Lobby.css'

// component for a singular button rule

function ButtonRule(props) {

    //propagate state back upwards for clicks on this button
    const selectID = props.selected ? 'selected-button' : 'unselected-button';
    const subtext = props.selected ? "(on)" : "(off)";

    const changeSelection = () => {
        let selection = props.selected;
        props.onClick(!selection);
    }
    if (props.dynamic) {
        return (
            <button className={'button-rules'}
                    id={selectID}
                    onClick={changeSelection}> {props.ruleText} {subtext}</button>
        );
    } else {
        return (
            <p className={'button-rules'}
                    id={selectID}> {props.ruleText} {subtext}</p>
        );
    }
}

export default ButtonRule;