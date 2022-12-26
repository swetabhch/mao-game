import GeneralView from "../GeneralView";

// component for wrapping the general board view with host props

function HostView(props) {
    return (
        <GeneralView
            isHost={true}
            location={props.location}
        ></GeneralView>
    )
}

export default HostView;