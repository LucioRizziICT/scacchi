const stompClient = new StompJs.Client({
    brokerURL: "ws://" + window.location.host + "/scacchi/scacchiapp"
});

stompClient.onConnect = function(frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/lobby/' + retrievedlobbyId, function(message) {
        console.log('Received: ' + message.body);
        const data = JSON.parse(message.body);
        applyMove(data.fromRow, data.fromCol, data.toRow, data.toCol, data.isCheck);
    });
};

stompClient.onWebSocketError = function(error) {
    console.log(error);
};

stompClient.onStompError = function(frame) {
    console.log('Error: ' + frame);
};

function sendSocketMove(fromRow, fromCol, toRow, toCol) {
    const messageWrapper = {
        playerToken: retrievedPlayerToken,
        message: {
            fromRow: fromRow,
            fromCol: fromCol,
            toRow: toRow,
            toCol: toCol
        }
    }
    stompClient.publish({
        destination: '/app/lobby/' + retrievedlobbyId + '/move',
        body: JSON.stringify(messageWrapper)
    });
    console.log("Message sent!");
}