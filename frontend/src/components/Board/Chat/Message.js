// component for each individual message in the chat

function Message(props) {

    // function to check if a message has any alphanumeric characters
    const hasAlphaNums = (word) => {
        if (word === '') {
            return false;
        } else {
            let firstChar = word.charCodeAt(1);
            if ((firstChar > 47 && firstChar < 58) || // numeric (0-9)
                (firstChar > 64 && firstChar < 91) || // upper alpha (A-Z)
                (firstChar > 96 && firstChar < 123)) { // lower alpha (a-z)
                return true;
            } else {
                return (hasAlphaNums(word.substring(1)))
            }
        }
    }

    let color;
    switch (props.messageType) {
        case ('verbal'):
            color = '#ceffd6';
            break;
        case ('penalty'):
            color = '#ff8080';
            break;
        case ('text'):
            color = '#b3d4fc';
            break;
        case ('server'):
            color = '#b3d4fc';
            break;
        default:
            color = '#b3d4fc';
    }

    const Filter = require('bad-words'),
        filter = new Filter();

    // added word to filter for demo purposes
    filter.addWords('NimTelson');

    // filter will break if there are no alphanumeric characters
    const filteredMsg = hasAlphaNums(props.messageText) ? filter.clean(props.messageText) : props.messageText;

    const formattedSender = (props.messageType === 'penalty' || props.messageType === 'server') ? <b>{props.username}:</b> : <>{props.username}:</>

    return (
        <div className={'chat-message'} style={{backgroundColor: color}}>
            {formattedSender} {filteredMsg}
        </div>
    );
}

export default Message;