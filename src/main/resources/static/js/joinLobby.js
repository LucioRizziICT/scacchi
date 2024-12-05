
document.getElementById('joinLobbyButton').addEventListener('click', joinLobby);

function joinLobby() {
    const playerName = document.getElementById('playerName').value;
    const password = document.getElementById('password').value;

    const url = `${window.location.pathname}/join?playerName=${playerName}&password=${password}`;
    fetch(url, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            response.text().then(playerToken => {
                setCookie('playerToken', playerToken, 30);
                window.location.reload();
            });
        } else {
            alert('Errore durante il join della partita');
        }
    });
}

function setCookie(cname, cvalue, exdays) {
    const d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    let expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}