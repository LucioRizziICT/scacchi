
document.getElementById('joinLobbyButton').addEventListener('click', joinLobby);

function joinLobby() {
    const playerName = document.getElementById('playerName').value;
    const password = document.getElementById('password').value;

    const body = {
        playerName: playerName,
        password: password
    }

    const url = `${window.location.pathname}/join`;
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    }).then(response => {
        if (response.ok) {
            response.json().then(lobby => {
                setCookie('playerToken', lobby.playerTwo.token, 30);
                window.location.reload();
            });
        } else {
            alert('Errore durante il join della partita');
        }
    });
}