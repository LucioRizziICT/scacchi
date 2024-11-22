function createGame() {
    let lobbyName = encodeURIComponent(document.getElementById('lobbyName').value);
    let playerName = encodeURIComponent(document.getElementById('playerName').value);

    fetch(`scacchi/lobby/createNewGame?lobbyName=${lobbyName}&playerName=${playerName}`, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            response.json().then(data => {
                localStorage.setItem('playerToken', data.playerToken);
                window.location.href = 'lobby/'+data.lobbyId;
            });
        } else {
            alert('Errore durante la creazione della partita');
        }
    });
}

document.addEventListener('wheel', function(event) {
    if (event.ctrlKey) return;
    const mainDiv = document.getElementById('main');
    console.log(mainDiv);
    if (mainDiv) {
        console.log('scrolling');
        console.log(mainDiv.style.marginTop);
        let prevMargin = mainDiv.style.marginTop.replace('px', '');
        console.log(prevMargin);
        let baseMargin = 30;
        let limit = window.innerHeight - baseMargin - mainDiv.clientHeight ;
        mainDiv.style.marginTop = `${Math.min( baseMargin ,Math.max(limit, parseInt(prevMargin ? prevMargin : 0 ) - event.deltaY/1.2))}px`;
    }
});