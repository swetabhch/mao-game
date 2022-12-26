import '../Lobby.css'
import Dropdown from 'react-dropdown'
import 'react-dropdown/style.css'

// component for holding the rules in the format [name of rule] [number]
//  host gets dynamic version

function NumberRule(props) {
    const view = props.view ? "visible" : "hidden";

    return (
        <div className={'number-rules'} style={{visibility: view}}>
            <p className={'rule-text'}>{props.ruleText}</p>
            {props.dynamic ?
                <Dropdown
                    className={'number-dropdown'}
                    value={{value: props.ruleAmt, label: props.ruleAmt.toString()}}
                    onChange={(val) => props.onChange(val.value)}
                    options={props.options}
                />

                : <div className={'rule-num'}>
                    <p>{props.ruleAmt}</p>
                </div>
            }
        </div>
    );
}

export default NumberRule;