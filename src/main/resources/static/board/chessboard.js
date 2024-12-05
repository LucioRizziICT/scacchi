const canvas = document.getElementById('chessboardCanvas');
let mouseX = 0;
let mouseY = 0;

canvas.addEventListener('mousemove', function(event) {
    mouseX = event.offsetX;
    mouseY = event.offsetY;
});

const PIECES_NAME = {
    'p': 'pawn',
    'r': 'rook',
    'n': 'knight',
    'b': 'bishop',
    'q': 'queen',
    'k': 'king'
};

const boardSize = 8;
let canvasSize = Math.min(window.innerWidth, window.innerHeight) - 20;
canvasSize = canvasSize - (canvasSize % boardSize);
canvas.height = canvasSize;
canvas.width = canvasSize;
const ctx = canvas.getContext('2d');
let cellSize = canvasSize / boardSize;
ctx.lineWidth = cellSize / 6;
ctx.strokeStyle = 'rgba(255,95,38,0.8)';

const whiteCellColor = '#f0d9b5';
const blackCellColor = '#b58863';

const playerColor = retrievedPlayerColor === "WHITE" ? 'w' : 'b';

let arrowStart = {};
let heldPiece = null;
let selectedPiece = null;

let kingPosition = { row: 0, col: 0 };
let checked = false;
function drawArrow(fromy, fromx, toy, tox){
    //variables to be used when creating the arrow
    var headlen = 10;
    var angle = Math.atan2(toy-fromy,tox-fromx);

    ctx.save();
    ctx.strokeStyle = 'rgba(255,95,38,0.8)';

    //starting path of the arrow from the start square to the end square
    //and drawing the stroke
    ctx.beginPath();
    ctx.moveTo(fromx, fromy);
    ctx.lineTo(tox, toy);
    ctx.lineWidth = cellSize / 6;
    ctx.stroke();

    //starting a new path from the head of the arrow to one of the sides of
    //the point
    ctx.beginPath();
    ctx.moveTo(tox, toy);
    ctx.lineTo(tox-headlen*Math.cos(angle-Math.PI/7),
        toy-headlen*Math.sin(angle-Math.PI/7));

    //path from the side point of the arrow, to the other side point
    ctx.lineTo(tox-headlen*Math.cos(angle+Math.PI/7),
        toy-headlen*Math.sin(angle+Math.PI/7));

    //path from the side point back to the tip of the arrow, and then
    //again to the opposite side point
    ctx.lineTo(tox, toy);
    ctx.lineTo(tox-headlen*Math.cos(angle-Math.PI/7),
        toy-headlen*Math.sin(angle-Math.PI/7));

    //draws the paths created above
    ctx.stroke();
    ctx.restore();
}

canvas.addEventListener('mousedown', function(event) {
    if (event.button === 0) {

        board.drawPieces();

        const x = event.offsetX;
        const y = event.offsetY;

        const row = Math.floor(y / cellSize);
        const col = Math.floor(x / cellSize);

        if (board.board[row][col] != null && board.board[row][col].color === playerColor) {
            heldPiece = board.board[row][col];
            heldPiece.held = true;
            selectedPiece = heldPiece;

            getMovableSpots(row, col);
        }
    }
    if (event.button === 2) {
        const x = event.offsetX;
        const y = event.offsetY;

        const row = Math.floor(y / cellSize);
        const col = Math.floor(x / cellSize);

        const centerRow = row * cellSize + cellSize / 2;
        const centerCol = col * cellSize + cellSize / 2;
        arrowStart.row = centerRow;
        arrowStart.col = centerCol;
    }
});

canvas.addEventListener('mouseup', function(event) {
    if (event.button === 0) {
        const x = event.offsetX;
        const y = event.offsetY;

        const row = Math.floor(y / cellSize);
        const col = Math.floor(x / cellSize);

        if (heldPiece != null) {
            heldPiece.held = false;
            heldPiece = null;

            if (selectedPiece.row === row && selectedPiece.col === col) {

            } else {
                if (board.movableSpots[`${row}_${col}`]) {
                    movePiece(selectedPiece, row, col);
                    selectedPiece = null;
                    board.movableSpots = {};
                } else {
                    selectedPiece = null;
                    board.movableSpots = {};
                }
            }
        } else {
            if (selectedPiece != null) {
                if (board.movableSpots[`${row}_${col}`]) {
                    movePiece(selectedPiece, row, col);
                    selectedPiece = null;
                    board.movableSpots = {};
                } else {
                    selectedPiece = null;
                    board.movableSpots = {};
                }
            }
        }

        board.drawPieces();
    }
    if (event.button === 2) {
        const x = event.offsetX;
        const y = event.offsetY;

        const row = Math.floor(y / cellSize);
        const col = Math.floor(x / cellSize);

        const centerRow = row * cellSize + cellSize / 2;
        const centerCol = col * cellSize + cellSize / 2;
        if (arrowStart.row === centerRow && arrowStart.col === centerCol) {
            return;
        }
        drawArrow(arrowStart.row, arrowStart.col, centerRow, centerCol);
    }
});

function movePiece(piece, row, col) {

    const from = getCorrectedPosition(piece.row, piece.col);
    const to = getCorrectedPosition(row, col);

    sendSocketMove(from.row, from.col, to.row, to.col);
    board.movableSpots = {};
    board.drawPieces();
}

function applyMove(fromRow, fromCol, toRow, toCol, isCheck) {
    const from = getCorrectedPosition(fromRow, fromCol);
    const to = getCorrectedPosition(toRow, toCol);

    board.board[to.row][to.col] = board.board[from.row][from.col];
    board.board[from.row][from.col] = null;
    board.board[to.row][to.col].row = to.row;
    board.board[to.row][to.col].col = to.col;
    checked = isCheck;
    if (board.board[to.row][to.col].color === playerColor) {
        checked = false;
        if (board.board[to.row][to.col].type === 'k') {
            kingPosition = {row: to.row, col: to.col};
        }
    }

    //TODO: AGGIUNGERE ALTRE MOSSE
    board.drawPieces();
}

canvas.addEventListener('contextmenu', function(event) {
    event.preventDefault();
});

function getCorrectedPosition(row, col) {
    if (playerColor === 'w') {
        return { row: boardSize - 1 - row, col: col };
    } else {
        return { row: row, col: boardSize - 1 - col };
    }
}

function getMovableSpots(row, col) {

    const correctedPosition = getCorrectedPosition(row, col);

    const url = `${window.location.pathname}/possibleMoves?row=${correctedPosition.row}&col=${correctedPosition.col}`;
    fetch(url, {
        method: 'GET',
        credentials: 'same-origin',
    }).then(response => {
        if (response.ok) {
            response.json().then(data => {
                console.log(data);
                board.movableSpots = {};
                data.moves.forEach(move => {
                    const correctedSpot = getCorrectedPosition(move.destination.row, move.destination.column);
                    board.movableSpots[`${correctedSpot.row}_${correctedSpot.col}`] = move.moveType;
                });
                board.drawPieces();
            });
        }
    });
}

function Piece(row, col, color, type) {
    this.row = row;
    this.col = col;
    this.color = color;
    this.type = type;
    this.held = false;
    this.img = new Image();
    this.img.src = `/scacchi/board/sprites/${this.color}_${PIECES_NAME[this.type]}.png`;
    this.img.onload = () => {
        this.draw();
    }

    this.draw = function() {
        if (this.held) {
            ctx.save();
            ctx.globalAlpha = 0.5;
            ctx.drawImage(this.img, this.col * cellSize, this.row * cellSize, cellSize, cellSize);
            ctx.restore();
        }
        else {
            ctx.drawImage(this.img, this.col * cellSize, this.row * cellSize, cellSize, cellSize);
        }
    }
}

function Chessboard () {
    this.board = new Array(boardSize);
    for (let i = 0; i < boardSize; i++) {
        this.board[i] = new Array(boardSize);
    }

    this.movableSpots = {};

    this.drawPieces = function () {
        drawBackground();
        for (let i = 0; i < boardSize; i++) {
            for (let j = boardSize - 1; j >= 0; j--) {
                if (this.board[i][j] != null) {
                    this.board[i][j].draw();
                }
            }
        }

        for (let spot in this.movableSpots) {
            const [row, col] = spot.split('_');
            ctx.beginPath();
            ctx.arc(col * cellSize + cellSize / 2, row * cellSize + cellSize / 2, cellSize / 6, 0, 2 * Math.PI);
            ctx.fillStyle = 'rgb(135,135,135)';
            ctx.fill();
        }
    }

    this.initialize = function () {
        const initialSetup = JSON.parse(retrievedPosition);

        for (let i = 0; i < boardSize; i++) {
            for (let j = 0; j < boardSize; j++) {
                const char = initialSetup[i][j];
                if (char === ' ') {
                    const position = getCorrectedPosition(i, j);
                    this.board[position.row][position.col] = null;
                } else {
                    const color = char === char.toUpperCase() ? 'w' : 'b';
                    const type = char.toLowerCase();
                    const position = getCorrectedPosition(i, j);
                    if (type === 'k' && color === playerColor) {
                        kingPosition = { row: position.row, col: position.col };
                    }
                    this.board[position.row][position.col] = new Piece(position.row, position.col, color, type);
                }
            }
        }
    }
}

function drawBackground() {
    for (let i = 0; i < boardSize; i++) {
        for (let j = 0; j < boardSize; j++) {
            ctx.fillStyle = (i + j) % 2 === 0 ? whiteCellColor : blackCellColor;
            ctx.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
        }
    }
    if (checked) {
        ctx.beginPath();
        ctx.arc(kingPosition.col * cellSize + cellSize / 2, kingPosition.row * cellSize + cellSize / 2, cellSize / 2, 0, 2 * Math.PI);
        ctx.fillStyle = 'rgba(221,1,1,0.73)';
        ctx.fill();
    }
}

const board = new Chessboard();

board.initialize();
board.drawPieces();

function animate() {
    requestAnimationFrame(animate);
    if(heldPiece == null) {
        return;
    }
    board.drawPieces();
    ctx.drawImage(heldPiece.img, mouseX - cellSize / 2, mouseY - cellSize / 2, cellSize, cellSize);
}
animate();