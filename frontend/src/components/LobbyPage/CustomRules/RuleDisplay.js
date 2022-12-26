// component for formatting/displaying each rule in a way that is user-friendly

function RuleDisplay(props) {
    let rule = props.rule;

    const toLowerCase = String.prototype.toLowerCase.call.bind(String.prototype.toLowerCase);
    const suitsDisplay = rule.suits.map(toLowerCase).join(', ');
    const ranksDisplay = rule.ranks.map(toLowerCase).join(', ');

    let condition;
    if ((rule.suits.length === 4) && rule.ranks.length === 13) {
        condition = 'When any card is played, ';
    }  else if (rule.suits.length === 4) {
        condition = `When any card is played with rank from [${ranksDisplay}], `
    } else if (rule.ranks.length === 13) {
        condition = `When any card is played with suit from [${suitsDisplay}], `
    } else {
        condition = `When a card is played with suit from [${suitsDisplay}] and rank from [${ranksDisplay}], `;
    }

    let result;
    if (rule.type === 'VERBAL_PENALTY') {
        result = `the player must say "${rule.verbal}".`;
    // } else if (rule.type === 'SILENCE') {
    //     result = 'the player must be silent.';
    } else if (rule.type === 'REVERSE_GAMEPLAY') {
        result = 'gameplay is reversed.';
    } else if (rule.type === 'SKIP_NEXT_PLAYER') {
        result = 'the next player is skipped.';
    }

    return (
        <p className={props.type} id={props.id} onClick={props.onClick}>{condition}{result}</p>
    );
}

export default RuleDisplay;