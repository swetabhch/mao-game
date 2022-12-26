import GeneralView from "../GeneralView";

// component for wrapping the general board view with player props

function PlayerView(props) {
    return (
        <GeneralView
            isHost={false}
            location={props.location}
        />
    )
}

export default PlayerView;