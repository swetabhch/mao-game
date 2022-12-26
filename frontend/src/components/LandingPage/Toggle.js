import './Toggle.css';

// component for holding the toggle

function Toggle(props) {
    return (
        <div className="toggle-switch">
            <label className="switch">
                <input
                    type="checkbox"
                    id={"toggle"}
                    onChange={(e) => {
                        props.changeHost(!e.target.checked);
                    }}/>
                    <span className="slider round"/>
            </label>
        </div>
    );
}
export default Toggle; 