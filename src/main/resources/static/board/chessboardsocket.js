const stompClient = new StompJs.Client({
    brokerURL: "ws://" + window.location.host + "/scacchi/scacchiapp"
});

stompClient.onConnect = function(frame) {
    stompClient.subscribe('/topic/lobby/' + retrievedlobbyId + '/move', function(message) {
        const data = JSON.parse(message.body);
        applyMove(data.fromRow, data.fromCol, data.toRow, data.toCol, data.promotion, data.isCheck);
    });
    stompClient.subscribe('/topic/lobby/' + retrievedlobbyId + '/gameover', function(message) {
        const data = JSON.parse(message.body);
        applyGameOver(data);
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
        playerToken: retrievedPlayerToken,
        message: {
            fromRow: fromRow,
            fromCol: fromCol,
            toRow: toRow,
            toCol: toCol,
            promotion: promotion || null
        }
    }
    stompClient.publish({
        destination: '/app/lobby/' + retrievedlobbyId + '/move',
        body: JSON.stringify(messageWrapper)
    });
}