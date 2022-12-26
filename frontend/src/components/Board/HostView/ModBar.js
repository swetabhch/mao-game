import '../Board.css'
import './ModBar.css'
import RuleDisplay from "../../LobbyPage/CustomRules/RuleDisplay";
import {useState} from 'react';
import {socket} from '../../../index.js'
import {CLIENT_MESSAGE} from '../../../utils/constants.js'

// component for moderation bar used to see rules (host perspective) and give out manual penalties

function ModBar(props) {

    const [selectedRuleIdx, setSelectedRuleIdx] = useState(null);
    const [selectedRule, setSelectedRule] = useState(null);
    const [activeSelecting, setActiveSelecting] = useState(false);
    const [selectedPlayerId, setSelectedPlayerId] = useState(null);

    const players = props.players;

    //function to activate the manual penalty selection
    const activatePenalty = () => {
        if (activeSelecting) {
            setSelectedRuleIdx('');
        }
        setActiveSelecting(!activeSelecting);
    }

    //function to select the rule associated with manual penalty
    const selectPenalty = (idx, rule) => {
        if (activeSelecting) {
            setSelectedRuleIdx(idx);
            setSelectedRule(rule);
        }
    }

    //function to actually submit the penalty
    const submitPenalty = () => {
        if (selectedRuleIdx === null || selectedPlayerId === null) {
            return;
        }

        const toSend = {
            type: CLIENT_MESSAGE.MANUAL_PENALTY,
            payload: {
                playerId: selectedPlayerId,
                hint: selectedRule.hint,
            }
        }
        socket.send(JSON.stringify(toSend));

        //reset modbar to default state after submit
        setSelectedRuleIdx(null);
        setSelectedRule(null);
        setSelectedPlayerId(null);
        setActiveSelecting(false);
    }

    //divs for displaying the individual formatted rules
    let formedRuleDivs = props.formedRules.map((rule, index) => {
        return (<RuleDisplay
            type={'left-sidebar-rule'}
            id={(index === selectedRuleIdx) ? 'selected-rule' : ''}
            key={index}
            rule={rule}
            onClick={() => selectPenalty(index, rule)}
        />);
    });

    //divs for displaying the unformatted rules
    let stringRuleDivs = props.stringRules.map((rule, index) => {
        return (
            <p id={((formedRuleDivs.length + index) === selectedRuleIdx) ? 'selected-rule' : ''}
               className={'left-sidebar-rule'}
               key={index}
               onClick={() => selectPenalty(formedRuleDivs.length + index, rule)}>
                {rule.ruleString}
            </p>
        );
    });

    return (
        <div className={'left-sidebar'}>
            <h1 className={'left-sidebar-header'}>current rules</h1>
            <p id={'penalty-instruction'}
               style={{visibility: !activeSelecting ? 'hidden' : ''}}>click rule and submit to administer penalty</p>
            <div className={'left-sidebar-text'}>
                {formedRuleDivs}
                {stringRuleDivs}
            </div>
            {activeSelecting ?
                <div
                    className={'chose-player'}
                    onChange={(e) => setSelectedPlayerId(e.target.value)}>
                    {players.map((player, index) => (
                        <label key={index} className={'player-option'}>
                            <input type="radio" value={player.id} name="playerIdPenalized"/>
                            {player.username}
                        </label>
                    ))}
                </div>
                : <></>}
            <button className={'penalty-button'}
                    onClick={activatePenalty}>Manual Penalty
            </button>
            <button className={'submit-penalty'}
                    style={{visibility: !activeSelecting ? 'hidden' : ''}}
                    onClick={submitPenalty}>submit
            </button>
        </div>
    );
}

export default ModBar;