import RuleDisplay from "./RuleDisplay";

// component for containing the list of already created rules

function ExistingRules(props) {
    return (
        <div className={'display-rules'}>
            <h1 id={'existing-rule-header'}>Current Rules: </h1>
            <div className={'rules-list'}>
                <p className={'instr-text'}>The following rules are auto-enforced:</p>
                {props.formedRules.map((rule, index) => (
                    <RuleDisplay
                        key={index}
                        rule={rule}
                        type={props.type}
                        id={props.id}
                        onClick={null}
                    />
                ))}
                <p className={'instr-text'}>The following rules must be manually enforced:</p>
                {props.stringRules.map((rule, index) => (
                    <p key={index} type={props.type}>{rule.ruleString}</p>
                ))}
            </div>
        </div>
    );
}

export default ExistingRules;