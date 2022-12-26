import {Switch, Route} from 'react-router-dom';
import LobbyHost from './components/LobbyPage/LobbyHost';
import LandingPage from './components/LandingPage/LandingPage';
import HostView from './components/Board/HostView/HostView';
import PlayerView from './components/Board/PlayerView/PlayerView';
import LobbyPlayer from "./components/LobbyPage/LobbyPlayer";
import EndPage from "./components/EndPage/EndPage";

function Main() {
    return (
        <Switch>
            <Route exact path='/' component={LandingPage}/>
            <Route exact path='/lobbyHost' component={LobbyHost}/>
            <Route exact path='/lobbyPlayer' component={LobbyPlayer}/>
            <Route exact path='/player' component={PlayerView}/>
            <Route exact path='/host' component={HostView}/>
            <Route exact path='/end' component={EndPage}/>
        </Switch>
    )
};

export default Main;
