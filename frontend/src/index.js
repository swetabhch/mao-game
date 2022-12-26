import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
// import reportWebVitals from './reportWebVitals';
import {MemoryRouter} from 'react-router-dom';


ReactDOM.render(
    <MemoryRouter>
        <App/>
    </MemoryRouter>,
    document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();

export let socket;
if (window.location.port === "3000") {
    socket = new WebSocket("ws://localhost:9000/message");
} else {
    // for deployment to Heroku
    socket = new WebSocket("wss://" + window.location.hostname + ":" + window.location.port + "/message");
}
