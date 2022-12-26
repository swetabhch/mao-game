// component to hold the room code

function RoomCode(props){
    return(
        <div className={'room-code'}>
            <h1 className={'code'}>Room Code: {props.code}</h1>
        </div>
    );
}

export default RoomCode;