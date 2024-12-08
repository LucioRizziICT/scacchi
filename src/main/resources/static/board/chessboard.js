// CONSTANTS
const PIECES_NAME = {
    'p': 'pawn',
    'r': 'rook',
    'n': 'knight',
    'b': 'bishop',
    'q': 'queen',
    'k': 'king'
};
const PROMOTION_PIECES = ['q', 'r', 'b', 'n'];
const BOARD_SIZE = 8;
const COLORS =  {
    WHITE_CELL: '#f0d9b5',
    BLACK_CELL: '#b58863',
    ARROW: 'rgba(255,95,38,0.8)',
    ARROW2: 'rgba(30,165,228,0.8)',
    CHECK: 'rgba(221,1,1,0.73)',
    MOVABLE_SPOT: 'rgba(217,217,217,0.82)',
    LAYER: 'rgba(33,33,33,0.56)',
    PROMOTION_MENU_BG: '#f1e6d4',
    BLACK: '#000000'
};

const canvas = document.getElementById('chessboardCanvas');

const divSpace = Math.min(canvas.parentElement.clientHeight, canvas.parentElement.clientWidth);
const canvasSize = divSpace - (divSpace % BOARD_SIZE);
canvas.height = canvasSize;
canvas.width = canvasSize;

const cellSize = canvasSize / BOARD_SIZE;
const playerColor = retrievedPlayerColor === "WHITE" ? 'w' : 'b';

let arrowStart = {};
let heldPiece = null;
let selectedPiece = null;
let choosingPromotionCol = false;
let kingPosition = { row: 0, col: 0 };
let checked = false;

let mouseX = 0;
let mouseY = 0;

canvas.addEventListener('mousemove', function(event) {
    mouseX = event.offsetX;
    mouseY = event.offsetY;
});

canvas.addEventListener('mousedown', function(event) {
    if (choosingPromotionCol !== false) {
        return;
    }
    if (event.button === 0) {

        board.draw();

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
    if (choosingPromotionCol !== false) {
        const x = event.offsetX;
        const y = event.offsetY;

        const row = Math.floor(y / cellSize);
        const col = Math.floor(x / cellSize);

        if(col === choosingPromotionCol) {
            if( row < PROMOTION_PIECES.length ) {
                movePiece(selectedPiece, 0, choosingPromotionCol, PROMOTION_PIECES[row]);
            }
        }

        choosingPromotionCol = false;
        board.draw();
        return;
    }
    if (event.button === 0) {
        const x = event.offsetX;
        const y = event.offsetY;

        const row = Math.floor(y / cellSize);
        const col = Math.floor(x / cellSize);

        if (heldPiece != null) {
            heldPiece.held = false;
            heldPiece = null;
        }

        if ( selectedPiece != null && (selectedPiece.row !== row || selectedPiece.col !== col) ) {
            if (board.movableSpots[`${row}_${col}`]) {
                if (selectedPiece.type === 'p' && row === 0) {
                    choosingPromotionCol = col;
                    drawPromotionMenu(col);
                    return;
                }
                console.log(selectedPiece);
                movePiece(selectedPiece, row, col);
                selectedPiece = null;
                board.movableSpots = {};
            } else {
                selectedPiece = null;
                board.movableSpots = {};
            }
        }

        board.draw();
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
        drawArrow(arrowStart.row, arrowStart.col, centerRow, centerCol, event.ctrlKey ? COLORS.ARROW2 : COLORS.ARROW);
    }
});

canvas.addEventListener('contextmenu', function(event) {
    event.preventDefault();
});


const ctx = canvas.getContext('2d');

function drawArrow(fromy, fromx, toy, tox, color){
    const headLength = cellSize / 3;
    const angle = Math.atan2(toy-fromy,tox-fromx);

    ctx.save();
    ctx.strokeStyle = color;
    ctx.lineWidth = cellSize / 6;

    //starting path of the arrow from the start square to the end square
    //and drawing the stroke
    ctx.beginPath();
    ctx.moveTo(fromx, fromy);
    ctx.lineTo(tox, toy);
    ctx.stroke();

    //starting a new path from the head of the arrow to one of the sides of
    //the point
    ctx.beginPath();
    ctx.moveTo(tox, toy);
    ctx.lineTo(tox-headLength*Math.cos(angle-Math.PI/7),
        toy-headLength*Math.sin(angle-Math.PI/7));

    //path from the side point of the arrow, to the other side point
    ctx.lineTo(tox-headLength*Math.cos(angle+Math.PI/7),
        toy-headLength*Math.sin(angle+Math.PI/7));

    //path from the side point back to the tip of the arrow, and then
    //again to the opposite side point
    ctx.lineTo(tox, toy);
    ctx.lineTo(tox-headLength*Math.cos(angle-Math.PI/7),
        toy-headLength*Math.sin(angle-Math.PI/7));

    //draws the paths created above
    ctx.stroke();
    ctx.restore();
}

function movePiece(piece, row, col, promotion = null) {

    const from = getCorrectedPosition(piece.row, piece.col);
    const to = getCorrectedPosition(row, col);

    sendSocketMove(from.row, from.col, to.row, to.col, promotion);
    board.movableSpots = {};
    board.draw();
}



function applyMove(fromRow, fromCol, toRow, toCol, promotion, isCheck) {
    const from = getCorrectedPosition(fromRow, fromCol);
    const to = getCorrectedPosition(toRow, toCol);

    const movedPiece = board.board[from.row][from.col];
    const destinationPiece = board.board[to.row][to.col];

    handleMovement();
    handleCheck();
    handleEnPassant();
    handleCastling();
    handlePromotion();

    board.draw();

    function handleMovement() {
        board.board[to.row][to.col] = board.board[from.row][from.col];
        board.board[from.row][from.col] = null;
        movedPiece.row = to.row;
        movedPiece.col = to.col;
    }

    function handleCheck() {
        checked = isCheck;
        if (movedPiece.color === playerColor) {
            checked = false;
            if (movedPiece.type === 'k') {
                kingPosition = {row: to.row, col: to.col};
            }
        }
    }

    function handleEnPassant() {
        if (movedPiece.type === 'p' && Math.abs(from.col - to.col) === 1 && destinationPiece == null) {
            board.board[to.row + (movedPiece.color === playerColor ? 1 : -1)][to.col] = null;
        }
    }

    function handleCastling() {
        if (movedPiece.type === 'k' && Math.abs(from.col - to.col) === 2) {
            if (movedPiece.col > 4) {
                board.board[to.row][to.col - 1] = board.board[to.row][7];
                board.board[to.row][7] = null;
                board.board[to.row][to.col - 1].col = to.col - 1;
            } else {
                board.board[to.row][to.col + 1] = board.board[to.row][0];
                board.board[to.row][0] = null;
                board.board[to.row][to.col + 1].col = to.col + 1;
            }
        }
    }

    function handlePromotion() {
        if (movedPiece.type === 'p' && (to.row === 0 || to.row === BOARD_SIZE - 1)) {
            movedPiece.changeType(promotion);
        }
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
                board.draw();
            });
        }
    });
}

function getCorrectedPosition(row, col) {
    if (playerColor === 'w') {
        return { row: BOARD_SIZE - 1 - row, col: col };
    } else {
        return { row: row, col: BOARD_SIZE - 1 - col };
    }
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

    this.changeType = function (newType) {
        this.type = newType;
        this.img = new Image();
        this.img.src = `/scacchi/board/sprites/${this.color}_${PIECES_NAME[this.type]}.png`;
        this.img.onload = () => {
            this.draw();
        }
    }
}

function Chessboard () {
    this.board = new Array(BOARD_SIZE);
    for (let i = 0; i < BOARD_SIZE; i++) {
        this.board[i] = new Array(BOARD_SIZE);
    }

    this.movableSpots = {};

    this.draw = function () {
        this.drawBackground();
        this.drawPieces();
        this.drawMovableSpots();
    }

    this.drawBackground = function () {
        for (let i = 0; i < BOARD_SIZE; i++) {
            for (let j = 0; j < BOARD_SIZE; j++) {
                ctx.fillStyle = (i + j) % 2 === 0 ? COLORS.WHITE_CELL : COLORS.BLACK_CELL;
                ctx.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
        if (checked) {
            ctx.beginPath();
            ctx.arc(kingPosition.col * cellSize + cellSize / 2, kingPosition.row * cellSize + cellSize / 2, cellSize / 2, 0, 2 * Math.PI);
            ctx.fillStyle = COLORS.CHECK;
            ctx.fill();
        }
    }

    this.drawPieces = function () {
        for (let i = 0; i < BOARD_SIZE; i++) {
            for (let j = BOARD_SIZE - 1; j >= 0; j--) {
                if (this.board[i][j] != null) {
                    this.board[i][j].draw();
                }
            }
        }
    }

    this.drawMovableSpots = function () {
        for (let spot in this.movableSpots) {
            const [row, col] = spot.split('_');
            ctx.beginPath();
            ctx.arc(col * cellSize + cellSize / 2, row * cellSize + cellSize / 2, cellSize / 6, 0, 2 * Math.PI);
            ctx.fillStyle = COLORS.MOVABLE_SPOT;
            ctx.fill();
            ctx.strokeStyle = COLORS.BLACK;
            ctx.lineWidth = 2;
            ctx.stroke();
        }
    }

    this.initialize = function () {
        const initialSetup = JSON.parse(retrievedPosition);

        for (let i = 0; i < BOARD_SIZE; i++) {
            for (let j = 0; j < BOARD_SIZE; j++) {
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

const board = new Chessboard();
board.initialize();
board.draw();


function drawPromotionMenu(col) {
    board.draw();
    ctx.fillStyle = COLORS.LAYER;
    ctx.fillRect(0, 0, canvasSize, canvasSize);


    ctx.fillStyle = COLORS.PROMOTION_MENU_BG;
    ctx.fillRect( col * cellSize, 0, cellSize, cellSize * (PROMOTION_PIECES.length + 1) );

    for (let i = 0; i < PROMOTION_PIECES.length; i++) {
        const piece = PROMOTION_PIECES[i];
        const img = new Image();
        img.src = `/scacchi/board/sprites/${playerColor}_${PIECES_NAME[piece]}.png`;
        img.onload = () => {
            ctx.drawImage(img, col * cellSize, i * cellSize, cellSize, cellSize);
        }
    }

    ctx.fillStyle = COLORS.BLACK;
    drawX(col * cellSize + cellSize / 4, (PROMOTION_PIECES.length) * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);

    function drawX(x, y, width, height) {
        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.lineTo(x + width, y + height);
        ctx.moveTo(x + width, y);
        ctx.lineTo(x, y + height);
        ctx.strokeStyle = COLORS.BLACK;
        ctx.lineWidth = cellSize / 10;
        ctx.stroke();
    }
}



function animate() {
    requestAnimationFrame(animate);
    if(heldPiece == null) {
        return;
    }
    board.draw();
    drawHeldPiece();

    function drawHeldPiece() {
        ctx.drawImage(heldPiece.img, mouseX - cellSize / 2, mouseY - cellSize / 2, cellSize, cellSize);
    }
}

animate();