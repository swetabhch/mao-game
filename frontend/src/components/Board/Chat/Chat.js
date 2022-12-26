import '../Board.css'
import {socket} from '../../../index.js'
import {CLIENT_MESSAGE} from '../../../utils/constants.js'

// component for holding the chat for all players

function Chat(props) {

    //function for sending the message to the websocket
    const enterMessage = (e) => {
        if (e.key !== 'Enter' || e.target.value === '') {
            return;
        }

        const toSend = {
            type: CLIENT_MESSAGE.CHAT_MESSAGE,
            payload: {
                playerId: props.playerId,
                chatMessage: e.target.value,
            }
        }
        socket.send(JSON.stringify(toSend));

        // reset the text field
        e.target.value = '';
    }

    return (
        <div className={'chat'}>
            <h1 className={'right-sidebar-header'}>chat</h1>
            <div className={'message-history'}>
                {props.children}
            </div>
            <input className={'chat-input'}
                   type={'text'}
                   placeholder={'type your message here...'}
                   onKeyPress={enterMessage}/>
        </div>
    );
}

export default Chat;