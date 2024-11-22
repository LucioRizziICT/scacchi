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

function refreshLobbies() {
    fetch('scacchi/lobby/getLobbies', {
        method: 'GET'
    }).then(response => {
        if (response.ok) {
            response.json().then(data => {
                if (!data.lobbies || data.lobbies.length === 0) {
                    document.getElementById('lobbiesList').innerHTML = '<p>Nessuna lobby trovata</p>';
                    return;
                }
                const lobbiesTableBody = document.querySelector('#lobbiesTable tbody');
                lobbiesTableBody.innerHTML = ''; // Clear existing rows
                data.lobbies.forEach(lobby => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${lobby.name}</td>
                        <td>${lobby.playerOne}</td>
                        <td>${lobby.playerTwo || 'Waiting...'}</td>
                        <td><button onclick="joinLobby('${lobby.id}')">${lobby.playerTwo ? "Guarda" : "Entra"}</button></td>
                    `;
                    lobbiesTableBody.appendChild(row);
                });
            });
        } else {
            alert('Errore durante il recupero delle lobby');
        }
    });
}

function joinLobby(lobbyId) {
    // Implement the logic to join a lobby
}

document.getElementById('refreshLobbiesButton').addEventListener('click', refreshLobbies);

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