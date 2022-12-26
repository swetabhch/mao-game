
// div for creating the input boxes

function InputBox(props) {
    let view = props.view ? "unset" : "none";

    return (
        <input
    id={props.id}
    type={"text"}
    value={props.value}
    className={"user-input"}
    placeholder={props.placeholder}
    style={{display: view}}
    onChange={(e) => props.setValue(e.target.value)}
    />
    );
}

export default InputBox;
