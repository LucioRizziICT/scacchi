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