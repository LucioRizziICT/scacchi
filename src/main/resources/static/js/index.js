function createGame() {
    fetch('scacchi/createNewGame', {
        method: 'POST',
        headers: {
            'Player-Token': localStorage.getItem('playerToken')
        }
    }).then(response => {
        if (response.ok) {
            response.json().then(data => {
                localStorage.setItem('gameId', data.id);
                window.location.href = 'game.html';
            });
        } else {
            alert('Errore durante la creazione della partita');
        }
    }
}