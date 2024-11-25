const canvas = document.getElementById('chessboardCanvas');
let mouseX = 0;
let mouseY = 0;

canvas.addEventListener('mousemove', function(event) {
    mouseX = event.offsetX;
    mouseY = event.offsetY;
});

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

const playerColor = 'w';

let arrowStart = {};
let heldPiece = null;
let selectedPiece = null;

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
                } else {
                    selectedPiece = null;
                    board.movableSpots = {};
                }
            }
        } else {
            if (selectedPiece != null) {

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
    const url = `${window.location.pathname}/move?fromRow=${piece.row}&fromCol=${piece.col}&toRow=${row}&toCol=${col}`;

    fetch(url, {
        method: 'POST',
        headers: {
            'Player-Token': localStorage.getItem('playerToken')
        }
    }).then(response => {
        if (response.ok) {
            response.json().then(data => {
                board.board[row][col] = piece;
                board.board[piece.row][piece.col] = null;
                piece.row = row;
                piece.col = col;
                //TODO: AGGIUNGERE ALTRE MOSSE
            });
        }
    });
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
        headers: {
            'Player-Token': localStorage.getItem('playerToken')
        }
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
    this.img.src = `/scacchi/board/sprites/${this.color}_${this.type}.png`;
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
            for (let j = 0; j < boardSize; j++) {
                if (this.board[i][j] != null) {
                    this.board[i][j].draw();
                }
            }
        }

        for (let spot in this.movableSpots) {
            const [row, col] = spot.split('_');
            ctx.beginPath();
            ctx.arc(col * cellSize + cellSize / 2, row * cellSize + cellSize / 2, cellSize / 6, 0, 2 * Math.PI);
            ctx.fillStyle = 'rgba(68,68,68,0.5)';
            ctx.fill();
        }
    }

    this.initialize = function () {
        const initialSetup = [
            ['rook', 'knight', 'bishop', 'queen', 'king', 'bishop', 'knight', 'rook'],
            ['pawn', 'pawn', 'pawn', 'pawn', 'pawn', 'pawn', 'pawn', 'pawn'],
            [null, null, null, null, null, null, null, null],
            [null, null, null, null, null, null, null, null],
            [null, null, null, null, null, null, null, null],
            [null, null, null, null, null, null, null, null],
            ['pawn', 'pawn', 'pawn', 'pawn', 'pawn', 'pawn', 'pawn', 'pawn'],
            ['rook', 'knight', 'bishop', 'queen', 'king', 'bishop', 'knight', 'rook']
        ];

        for (let i = 0; i < boardSize; i++) {
            for (let j = 0; j < boardSize; j++) {
                if (initialSetup[i][j] != null) {
                    this.board[i][j] = new Piece(i, j, i < 2 ? 'b' : 'w', initialSetup[i][j]);
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