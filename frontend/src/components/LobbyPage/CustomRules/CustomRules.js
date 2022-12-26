import './CustomRule.css'
import ExistingRules from "./ExistingRules";
import {useState} from "react";
import {RANKS, SUITS} from '../../../utils/constants.js'

// div for holding the custom rule construction/display

function CustomRules(props) {

    //div is only visible when custom rules is selected (button setting)
    let view = props.view ? "visible" : "hidden";

    const Filter = require('bad-words'),
        filter = new Filter();

    const [ruleType, setRuleType] = useState('VERBAL_PENALTY');
    const [ranks, setRanks] = useState([]);
    const [suits, setSuits] = useState([]);
    //verbal holds inputs for both verbal rules and string rules
    const [verbal, setVerbal] = useState('');
    const [hint, setHint] = useState('');
    const [ruleTypes, setRuleTypes] = useState([]);

    //function for adding a rule from the settings
    const addRule = () => {

        //no format rules are held separately
        if (ruleType === 'NO_FORMAT') {

            if (filter.isProfane(verbal) || filter.isProfane(hint)) {
                alert("Please don't use profanity in your rules")
                setVerbal('');
                setHint('');
                return;
            }

            let stringRules = [...props.stringRules];
            const newStringRule = {
                ruleString: verbal,
                hint: hint,
            }
            stringRules.unshift(newStringRule);
            props.setStringRules(stringRules);

            //holding history of rule types for undo-ing in the added order
            let updatedRuleTypes = [...ruleTypes];
            updatedRuleTypes.push(ruleType);
            setRuleTypes(updatedRuleTypes);
            return;
        }

        if (ranks.length === 0 || suits.length === 0) {
            alert('Please specify a suit/suits and a rank/ranks for the rule!');
            return;
        }

        if (filter.isProfane(verbal) || filter.isProfane(hint)) {
            alert("Please don't use profanity in your rules")
            setVerbal('');
            setHint('');
            return;
        }

        //if reaching this point, rule will have format with it
        let formedRules = [...props.formedRules];
        const newFormedRule = {
            type: ruleType,
            ranks: ranks,
            suits: suits,
            verbal: verbal,
            hint: hint,
        }
        formedRules.unshift(newFormedRule);
        props.setFormedRules(formedRules);

        //holding history of rule types for undo-ing in the added order
        let updatedRuleTypes = [...ruleTypes];
        updatedRuleTypes.push(ruleType);
        setRuleTypes(updatedRuleTypes);
    }

    //function for changing the type of rule
    const updateType = (e) => {
        let selectedType = Array.from(e.target.selectedOptions, option => option.value)[0];
        setRuleType(selectedType);
    }

    //function for changing the ranks the rule applies to
    const updateRanks = (e) => {
        let selectedRanks = Array.from(e.target.selectedOptions, option => option.value);
        if (selectedRanks.includes("select-all")) {
            setRanks(Object.keys(RANKS));
        } else {
            setRanks(selectedRanks);
        }
    }

    //function for changing the suits the rule applies to
    const updateSuits = (e) => {
        let selectedSuits = Array.from(e.target.selectedOptions, option => option.value);
        if (selectedSuits.includes("select-all")) {
            setSuits(Object.keys(SUITS));
        } else {
            setSuits(selectedSuits);
        }
    }

    //function for changing the state variable to hold either the string rule or the
    // necessary phrase for a verbal rule
    const changeVerbal = (e) => {
        setVerbal(e.target.value);
    }

    //function for changing the state variable to hold the hints
    const changeHint = (e) => {
        setHint(e.target.value);
    }

    //function for getting rid of all the created rules
    const clearRules = () => {
        props.setFormedRules([]);
        props.setStringRules([]);
    }

    //function for undoing the last rule added
    const undoLatestRule = () => {
        if (ruleTypes.length !== 0) {
            let updatedRuleTypes = [...ruleTypes];
            const lastRule = updatedRuleTypes.pop();
            if (lastRule === 'NO_FORMAT') {
                let stringRules = [...props.stringRules];
                stringRules.shift();
                props.setStringRules(stringRules);
            } else {
                let formedRules = [...props.formedRules];
                formedRules.shift();
                props.setFormedRules(formedRules);
            }
            setRuleTypes(updatedRuleTypes);
        }
    }

    const selectVisibility = (ruleType === 'NO_FORMAT') ? 'hidden' : "";
    const verbalVisibility = (ruleType !== 'NO_FORMAT' && ruleType !== 'VERBAL_PENALTY') ? 'hidden' : '';
    const phrasePlaceholderText = (ruleType === 'VERBAL_PENALTY') ? 'enter rule phrase' : 'enter rule';

    // identifier for holding the interactive rule options
    const ruleSettings =
        <>
            <select
                onChange={updateSuits}
                className={'card-selector'}
                id='suit-selector'
                multiple={'multiple'}
                style={{visibility: selectVisibility}}>
                <option value={'select-all'}>All Suits</option>
                {Object.keys(SUITS).map((suit, index) => (
                    <option key={index} value={suit}>{suit.toLowerCase()}</option>
                ))}
            </select>
            <select
                onChange={updateRanks}
                className={'card-selector'}
                id='rank-selector'
                multiple={'multiple'}
                style={{visibility: selectVisibility}}>
                <option value={'select-all'}>All Values</option>
                {Object.keys(RANKS).map((rank, index) => (
                    <option key={index} value={rank}>{rank.toLowerCase()}</option>
                ))}
            </select>
            <input type={'text'}
                   id={'rule-phrase-input'}
                   value={verbal}
                   style={{visibility: verbalVisibility}}
                   placeholder={phrasePlaceholderText}
                   onChange={changeVerbal}>
            </input>
            <input
                id={'rule-hint-input'}
                type={'text'}
                value={hint}
                placeholder={'hint for the player'}
                onChange={changeHint}>
            </input>
        </>

    return (
        <div className={'custom-rules-box'} style={{visibility: view}}>
            <div className={'rule-input-boxes'}>
                <select defaultValue={'VERBAL_PENALTY'} onChange={updateType} id='type-selector'>
                    <option value={'VERBAL_PENALTY'}>Verbal Penalty</option>
                    {/* <option value={'SILENCE'}>Silence</option> */}
                    <option value={'REVERSE_GAMEPLAY'}>Reverse Gameplay</option>
                    <option value={'SKIP_NEXT_PLAYER'}>Skip Next Player</option>
                    <option value={'NO_FORMAT'}>Custom Unenforced Rule</option>
                </select>
                {ruleSettings}
                <button className={'rules-button'}
                        id={'add-rule-button'}
                        onClick={addRule}
                > add rule
                </button>
                <button className={'rules-button'}
                        id={'undo-button'}
                        onClick={undoLatestRule}
                > undo
                </button>
            </div>
            <button className={'rules-button'}
                    id={'clear-rule-button'}
                    onClick={clearRules}
            > clear rules
            </button>
            <ExistingRules
                formedRules={props.formedRules}
                stringRules={props.stringRules}
                type={'setting-display-rule'}
                id={''}/>
        </div>
    );
}

export default CustomRules;