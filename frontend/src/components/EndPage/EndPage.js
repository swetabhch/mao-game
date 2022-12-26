import './EndPage.css'
import {useHistory} from "react-router-dom";
import {Button} from "react-bootstrap";
import {socket} from '../../index.js'
import {useEffect} from "react";
import {CLIENT_MESSAGE} from '../../utils/constants.js'


// component for the final page

function EndPage(props) {

    const winner = props.location.username;

    const history = useHistory();

    // keep the Heroku server alive
    useEffect(() => {
        setInterval(() => {
            const toSend = {
                type: CLIENT_MESSAGE.PING,
                payload: {}
            }
            socket.send(JSON.stringify(toSend));
        }, 15000);
    }, [])

    const returnToLandingPage = () => {
        history.push('/');
    }

    return (<>
        <div className={'endText'}>{winner} has won the game of Mao</div>
        <Button id={'return-to-landing-page'}
                onClick={returnToLandingPage}>Return to Landing Page</Button>
        <div id={'credits'}>
            <>Created by Jay Sarva, Nishka Pant, Swetabh Changkakoti, and Sidharth Anand
            as a part of CSCI0320 at Brown University. </>
            <br></br>
            <>Special thanks to Christine Wang, Tim Nelson, and the CS32 staff :)</>
        </div>
    </>);
}

export default EndPage;