function createGame() {
    let lobbyName = encodeURIComponent(document.getElementById('lobbyName').value);
    let playerName = encodeURIComponent(document.getElementById('playerName').value);
    let password = encodeURIComponent(document.getElementById('password').value);
    let color = document.getElementById('pieceColor').value;
    color = color === 'random' ? Math.random() > 0.5 ? 'WHITE' : 'BLACK' : color;
    let lobbyType = document.getElementById('lobbyType').value;
    let url = 'lobby/createNewGame?lobbyName=' + lobbyName + '&playerName=' + playerName + '&password=' + password + '&color=' + color + '&lobbyType=' + lobbyType;

    fetch(url , {
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

document.getElementById('createLobbyButton').addEventListener('click', createGame);

function refreshLobbies() {
    fetch('lobby/getLobbies', {
        method: 'GET'
    }).then(response => {
        if (response.ok) {
            response.json().then(data => {
                console.log(data);
                if (!data || data.length === 0) {
                    document.getElementById('lobbiesList').innerHTML = '<p>Nessuna lobby trovata</p>';
                    return;
                }
                const lobbiesTableBody = document.querySelector('#lobbiesTable tbody');
                lobbiesTableBody.innerHTML = ''; // Clear existing rows
                data.forEach(lobby => {
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
    if (mainDiv) {
        let prevMargin = mainDiv.style.marginTop.replace('px', '');
        let baseMargin = 30;
        let limit = window.innerHeight - baseMargin - mainDiv.clientHeight ;
        mainDiv.style.marginTop = `${Math.min( baseMargin ,Math.max(limit, parseInt(prevMargin ? prevMargin : 0 ) - event.deltaY/1.2))}px`;
    }
});

function getOpenLobby() {
    let playerToken = localStorage.getItem('playerToken');
    if (playerToken) {
        fetch('lobby/getOpenLobby?playerToken=' + playerToken, {
            method: 'GET'
        }).then(response => {
            if (response.ok) {
                response.json().then(data => {
                    document.getElementById('openLobbyName').innerText = data.lobbyName;
                    document.getElementById('opponentName').innerText = data.opponentName;
                    document.getElementById('openLobbyButton').addEventListener('click', () => {
                        window.location.href = 'lobby/' + data.lobbyId;
                    });
                    document.getElementById('openLobby').style.display = "block";
                });
            } else if (response.status === 404) {
                localStorage.removeItem('playerToken');
            } else {
                alert('Errore durante il recupero della lobby aperta');
            }
        });
    }
}

window.onload = function() {
    refreshLobbies();
    getOpenLobby();
}