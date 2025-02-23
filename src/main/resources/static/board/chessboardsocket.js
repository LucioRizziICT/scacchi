const stompClient = new StompJs.Client({
    brokerURL: "ws://" + window.location.host + "/scacchi/scacchiapp"
});

stompClient.onConnect = function(frame) {
    stompClient.subscribe('/topic/lobby/' + retrievedLobbyId + '/start', function (message) {
        console.log('Game started');
        if (gamestarted) {
            return;
        }
        gamestarted = true;
        const data = JSON.parse(message.body);
        document.getElementById("player2NameSpan").innerText = data.player2.name;
        renderChessboard();
    });

    stompClient.subscribe('/topic/lobby/' + retrievedLobbyId + '/move', function(message) {
        const data = JSON.parse(message.body);
        applyMove(data.fromRow, data.fromCol, data.toRow, data.toCol, data.promotion, data.isCheck, data.timerInfo);
        updateMovesHistory();
    });

    stompClient.subscribe('/topic/lobby/' + retrievedLobbyId + '/gameover', function(message) {
        const data = JSON.parse(message.body);
        applyGameOver(data.outcome);
    });

    stompClient.subscribe('/topic/lobby/' + retrievedLobbyId + '/chat', function(message) {
        const data = JSON.parse(message.body);
        appendChatMessage(data);
    });

    stompClient.subscribe('/topic/lobby/' + retrievedLobbyId + '/notification', function(message) {
        const data = JSON.parse(message.body);
        if (data.playerId === retrievedPlayerId) {
            return;
        }
        showNotification(data); //TODO dont show self sent notifications and only if it makes sense (ex. dont show accept draw if game is over)
    });

    stompClient.subscribe('/topic/lobby/' + retrievedLobbyId + '/rematch', function(message) {
        window.location.reload();
    });
};

stompClient.onWebSocketError = function(error) {
    console.log(error);
};

stompClient.onStompError = function(frame) {
    console.log('Error: ' + frame);
};

function sendSocketMove(fromRow, fromCol, toRow, toCol, promotion = null) {
    const messageWrapper = {
        playerToken: getCookie('playerToken'),
        message: {
            fromRow: fromRow,
            fromCol: fromCol,
            toRow: toRow,
            toCol: toCol,
            promotion: promotion || null
        }
    }
    stompClient.publish({
        destination: '/app/lobby/' + retrievedLobbyId + '/move',
        body: JSON.stringify(messageWrapper)
    });
}