import '../Board.css'

// component for the area for the regular players to take notes
// no backend functionality associated with this component

function Notes() {
    return (
        <div className={'left-sidebar'}>
            <h1 className={'left-sidebar-header'}>notes</h1>
            <textarea className={'notes-area'}
                      placeholder={'you can take notes on the game by typing here!'}
                      rows={80}
                      cols={32}>
            </textarea>
        </div>
    );
}

export default Notes;