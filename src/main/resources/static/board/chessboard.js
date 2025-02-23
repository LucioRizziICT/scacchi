// CONSTANTS
const PIECES_NAMES = {
    'p': 'pawn',
    'r': 'rook',
    'n': 'knight',
    'b': 'bishop',
    'q': 'queen',
    'k': 'king'
};
const PROMOTION_PIECES = ['q', 'r', 'b', 'n'];
const BOARD_SIZE = 8;
const HIGHLIGHT_ALPHA = "C0";
const ARROW_ALPHA = "C0";

const PREFERENCES = JSON.parse(retrievedLobbyPreferences);

const COLORS =  {
    WHITE_CELL: '#f0d9b5',
    BLACK_CELL: '#b58863',
    HIGHLIGHTED_CELL1: PREFERENCES.highlightColors.color1,
    HIGHLIGHTED_CELL2: PREFERENCES.highlightColors.color2,
    HIGHLIGHTED_CELL3: PREFERENCES.highlightColors.color3,
    HIGHLIGHTED_CELL4: PREFERENCES.highlightColors.color4,
    ARROW1: PREFERENCES.arrowColors.color1,
    ARROW2: PREFERENCES.arrowColors.color2,
    ARROW3: PREFERENCES.arrowColors.color3,
    ARROW4: PREFERENCES.arrowColors.color4,
    CHECK: 'rgba(211,0,0,0.77)',
    MOVABLE_SPOT: 'rgba(217,217,217,0.82)',
    LAYER: 'rgba(33,33,33,0.56)',
    PROMOTION_MENU_BG: '#f1e6d4',
    BLACK: '#000000',
    LAST_MOVE: 'rgba(255, 255, 0, 0.5)'
};

const canvas = document.getElementById('chessboardCanvas');

const canvasLayers = {
    background: document.createElement('canvas'),
    highlights: document.createElement('canvas'),
    pieces: document.createElement('canvas'),
    arrows: document.createElement('canvas'),
    movableSpots: document.createElement('canvas'),
    promotionMenu: document.createElement('canvas'),
    overall: document.createElement('canvas')
}

const ctxLayers = {
    background: canvasLayers.background.getContext('2d'),
    highlights: canvasLayers.highlights.getContext('2d'),
    pieces: canvasLayers.pieces.getContext('2d'),
    arrows: canvasLayers.arrows.getContext('2d'),
    movableSpots: canvasLayers.movableSpots.getContext('2d'),
    promotionMenu: canvasLayers.promotionMenu.getContext('2d'),
    overall: canvasLayers.overall.getContext('2d')
}

let cellSize;
setCanvasSize();

function setCanvasSize() {
    const divSpace = Math.min(canvas.parentElement.clientHeight - 12, canvas.parentElement.clientWidth - 12); //TODO: 20 is the sum of the margins, make it dynamic
    const canvasSize = divSpace - (divSpace % BOARD_SIZE);
    canvas.height = canvasSize;
    canvas.width = canvasSize;

    for (const layer in canvasLayers) {
        canvasLayers[layer].height = canvasSize;
        canvasLayers[layer].width = canvasSize;
    }

    cellSize = canvasSize / BOARD_SIZE;
}

const chessboardCtx = canvas.getContext('2d');

const playerColor = retrievedPlayerColor === "WHITE" ? 'w' : 'b';
let movableSpots = {};
let highlightedCells = {};
let arrowStart = {};
let heldPiece = null;
let selectedPiece = null;
let choosingPromotionCol = false;
let kingPositions = {
    'w': { row: 0, col: 4 },
    'b': { row: 7, col: 4 }
};
let isChecked = {
    'w': false,
    'b': false
}
let gameover = retrievedGameover;
let gamestarted = retrievedGameStarted;
let lastMove = null;

let mouseX = 0;
let mouseY = 0;

canvas.addEventListener('mousemove', function(event) {
    mouseX = event.offsetX;
    mouseY = event.offsetY;
});

canvas.addEventListener('mousedown', function(event) {
    if (gameover || !gamestarted)
        return;

    if (choosingPromotionCol !== false) {
        return;
    }
    if (event.button === 0) {

        clearCtx(ctxLayers.arrows);
        clearCtx(ctxLayers.promotionMenu);
        clearCtx(ctxLayers.movableSpots);
        clearCtx(ctxLayers.highlights);

        const x = event.offsetX;
        const y = event.offsetY;

        const row = Math.floor(y / cellSize);
        const col = Math.floor(x / cellSize);

        if (board.board[row][col] != null && board.board[row][col].color === playerColor) {
            if (heldPiece != null) {
                heldPiece.held = false;
            }
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
    if (gameover || !gamestarted)
        return;

    const x = event.offsetX;
    const y = event.offsetY;

    const row = Math.floor(y / cellSize);
    const col = Math.floor(x / cellSize);

    if (choosingPromotionCol !== false) {

        if(col === choosingPromotionCol) {
            if( !(PREFERENCES.setWhiteAlwaysBottom && playerColor === 'b') && row < PROMOTION_PIECES.length ) {
                movePiece(selectedPiece, 0, choosingPromotionCol, PROMOTION_PIECES[row]);
            } else if( (PREFERENCES.setWhiteAlwaysBottom && playerColor === 'b') && row > BOARD_SIZE - 1 - PROMOTION_PIECES.length ) {
                movePiece(selectedPiece, BOARD_SIZE -1, choosingPromotionCol, PROMOTION_PIECES[BOARD_SIZE - 1 - row]);
            }
        }

        choosingPromotionCol = false;
        clearCtx(ctxLayers.promotionMenu);
        renderChessboard();
        return;
    }

    if (event.button === 0) {
        if (heldPiece != null) {
            heldPiece.held = false;
            heldPiece = null;
        }

        if ( selectedPiece != null && (selectedPiece.row !== row || selectedPiece.col !== col) ) {
            if (movableSpots[`${row}_${col}`]) {
                if (selectedPiece.type === 'p' && (row === 0 || row === BOARD_SIZE - 1) ) {
                    choosingPromotionCol = col;
                    drawPromotionMenu(ctxLayers.promotionMenu, choosingPromotionCol, row);
                    renderChessboard();
                    return;
                }
                movePiece(selectedPiece, row, col);
                selectedPiece = null;
                movableSpots = {};
            } else {
                selectedPiece = null;
                movableSpots = {};
            }
        }

        renderChessboard();
    } else if (event.button === 2) {
        const centerRow = row * cellSize + cellSize / 2;
        const centerCol = col * cellSize + cellSize / 2;

        if (arrowStart.row === centerRow && arrowStart.col === centerCol) {
            highlightCell(ctxLayers.highlights, row, col, getHighlightColor());
        } else {
            drawArrow(ctxLayers.arrows, arrowStart.row, arrowStart.col, centerRow, centerCol, getArrowColor());
        }
        renderChessboard();

        function getHighlightColor() {
            if (event.ctrlKey) {
                if (event.shiftKey) {
                    return COLORS.HIGHLIGHTED_CELL4;
                }
                return COLORS.HIGHLIGHTED_CELL2;
            }
            if (event.shiftKey) {
                return COLORS.HIGHLIGHTED_CELL3;
            }
            return COLORS.HIGHLIGHTED_CELL1;
        }

        function getArrowColor() {
            if (event.ctrlKey) {
                if (event.shiftKey) {
                    return COLORS.ARROW4;
                }
                return COLORS.ARROW2;
            }
            if (event.shiftKey) {
                return COLORS.ARROW3;
            }
            return COLORS.ARROW1;
        }
    }
});

canvas.addEventListener('contextmenu', function(event) {
    event.preventDefault();
});

window.addEventListener('resize', resizeCanvas, false);

function resizeCanvas() {
    setCanvasSize();
    drawGameNotStarted(ctxLayers.overall);
    renderChessboard();
}

//from codepen
function drawArrow(ctx, fromy, fromx, toy, tox, color){
    color += ARROW_ALPHA;

    const headLength = cellSize / 3;
    const angle = Math.atan2(toy-fromy,tox-fromx);

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
}

function highlightCell(ctx, row, col, color) {
    color += HIGHLIGHT_ALPHA;
    const key = `${row}_${col}`;
    if (highlightedCells[key] === color) {
        delete highlightedCells[key];
    } else {
        highlightedCells[key] = color;
    }
    drawSingleCell();

    function drawSingleCell() {
        ctx.clearRect(col * cellSize, row * cellSize, cellSize, cellSize);
        if (highlightedCells[key]) {
            ctx.fillStyle = color;
            ctx.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
        }
    }
}

function movePiece(piece, row, col, promotion = null) {

    const from = getCorrectedPosition(piece.row, piece.col);
    const to = getCorrectedPosition(row, col);

    sendSocketMove(from.row, from.col, to.row, to.col, promotion);
    movableSpots = {};
    clearCtx(ctxLayers.promotionMenu);
    renderChessboard();
}

function applyGameOver(outcome) { //TODO: Cambiare con testo custom, non valore dell'enum (ex. "WHITE_WIN")
    if (gameover) {
        return;
    }
    gameover = true;

    document.getElementById("gameEndModalStatus").innerText = outcome.status;
    document.getElementById("gameEndModalCause").innerText = outcome.cause;

    openGameEndModal();
    playGameOverSound();
    showPostGameDiv();
}

function applyMove(fromRow, fromCol, toRow, toCol, promotion, isCheck) {
    const from = getCorrectedPosition(fromRow, fromCol);
    const to = getCorrectedPosition(toRow, toCol);

    lastMove = { from: from, to: to };

    const movedPiece = board.board[from.row][from.col];
    const destinationPiece = board.board[to.row][to.col];

    handleSound();
    handleMovement();
    handleCheck();
    handleEnPassant();
    handleCastling();
    handlePromotion();

    renderChessboard();

    function handleSound() {
        if (isCheck) {
            playCheckSound();
        }
        else if (destinationPiece) {
            playCaptureSound();
        } else {
            playMoveSound();
        }
    }

    function handleMovement() {
        board.board[to.row][to.col] = board.board[from.row][from.col];
        board.board[from.row][from.col] = null;
        movedPiece.row = to.row;
        movedPiece.col = to.col;
    }

    function handleCheck() {
        const movedColor = movedPiece.color;
        const otherColor = movedColor === 'w' ? 'b' : 'w';

        isChecked[movedColor] = false;
        isChecked[otherColor] = isCheck;

        if (movedPiece.type === 'k') {
            kingPositions[movedColor] = {row: to.row, col: to.col};
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
                movableSpots = {};
                data.forEach(move => {
                    const correctedSpot = getCorrectedPosition(move.destination.row, move.destination.col);
                    movableSpots[`${correctedSpot.row}_${correctedSpot.col}`] = move.moveType;
                });
                renderChessboard();
            });
        }
    });
}

function getCorrectedPosition(row, col) {
    if (PREFERENCES.setWhiteAlwaysBottom || playerColor === 'w') {
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
    this.img.src = `/scacchi/board/sprites/${this.color}_${PIECES_NAMES[this.type]}.png`;
    this.img.onload = () => {
        this.draw(ctxLayers.pieces);
    }

    this.draw = function(ctx) {
        if (this.held) {
            ctx.globalAlpha = 0.5;
            ctx.drawImage(this.img, this.col * cellSize, this.row * cellSize, cellSize, cellSize);
        }
        else {
            ctx.globalAlpha = 1;
            ctx.drawImage(this.img, this.col * cellSize, this.row * cellSize, cellSize, cellSize);
        }
    }

    this.changeType = function (newType) {
        this.type = newType;
        this.img = new Image();
        this.img.src = `/scacchi/board/sprites/${this.color}_${PIECES_NAMES[this.type]}.png`;
        this.img.onload = () => {
            this.draw(ctxLayers.pieces);
        }
    }
}

function Chessboard () {
    this.board = new Array(BOARD_SIZE);
    for (let i = 0; i < BOARD_SIZE; i++) {
        this.board[i] = new Array(BOARD_SIZE);
    }

    this.draw = function () {
        this.drawPieces(ctxLayers.background);
    }

    this.drawPieces = function (ctx) {
        clearCtx(ctx)
        for (let i = 0; i < BOARD_SIZE; i++) {
            for (let j = BOARD_SIZE - 1; j >= 0; j--) {
                if (this.board[i][j] != null) {
                    this.board[i][j].draw(ctx);
                }
            }
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
                    if (type === 'k') {
                        kingPositions[color] = { row: position.row, col: position.col };
                    }
                    this.board[position.row][position.col] = new Piece(position.row, position.col, color, type);
                }
            }
        }
    }
}

const board = new Chessboard();
board.initialize();
drawGameNotStarted(ctxLayers.overall);
renderChessboard();


function drawPromotionMenu(ctx, col, row) {
    clearCtx(ctx);
    if (choosingPromotionCol === false)
        return;

    const canvasSize = canvas.width;
    ctx.fillStyle = COLORS.LAYER;
    ctx.fillRect(0, 0, canvasSize, canvasSize);

    if (row === 0) {
        drawTopPromotionMenu();
    }
    else if (row === BOARD_SIZE - 1) {
        drawBottomPromotionMenu();
    }

    function drawTopPromotionMenu() {
        ctx.fillStyle = COLORS.PROMOTION_MENU_BG;
        ctx.fillRect( col * cellSize, 0, cellSize, cellSize * (PROMOTION_PIECES.length + 1) );

        for (let i = 0; i < PROMOTION_PIECES.length; i++) {
            const piece = PROMOTION_PIECES[i];
            const img = new Image();
            img.src = `/scacchi/board/sprites/${playerColor}_${PIECES_NAMES[piece]}.png`;
            img.onload = () => {
                ctx.drawImage(img, col * cellSize, i * cellSize, cellSize, cellSize);
            }
        }

        ctx.fillStyle = COLORS.BLACK;
        drawX(col * cellSize + cellSize / 4, (PROMOTION_PIECES.length) * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);
    }

    function drawBottomPromotionMenu() {
        ctx.fillStyle = COLORS.PROMOTION_MENU_BG;
        ctx.fillRect( col * cellSize, (BOARD_SIZE - (PROMOTION_PIECES.length + 1)) * cellSize, cellSize, cellSize * (PROMOTION_PIECES.length + 1) );

        for (let i = 0; i < PROMOTION_PIECES.length; i++) {
            const piece = PROMOTION_PIECES[i];
            const img = new Image();
            img.src = `/scacchi/board/sprites/${playerColor}_${PIECES_NAMES[piece]}.png`;
            img.onload = () => {
                ctx.drawImage(img, col * cellSize, (BOARD_SIZE - 1 - i) * cellSize, cellSize, cellSize);
            }
        }

        ctx.fillStyle = COLORS.BLACK;
        drawX(col * cellSize + cellSize / 4, (BOARD_SIZE - 1 - PROMOTION_PIECES.length) * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);
    }

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

function renderChessboard() {
    drawBackground(ctxLayers.background);
    board.drawPieces(ctxLayers.pieces);
    drawMovableSpots(ctxLayers.movableSpots);
    drawHeldPiece(ctxLayers.overall);
    for (const layer in canvasLayers) {
        chessboardCtx.drawImage(canvasLayers[layer], 0, 0);
    }
}

function drawBackground(ctx) {
    for (let i = 0; i < BOARD_SIZE; i++) {
        for (let j = 0; j < BOARD_SIZE; j++) {
            ctx.fillStyle = (i + j) % 2 === 0 ? COLORS.WHITE_CELL : COLORS.BLACK_CELL;
            ctx.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
        }
    }
    drawCheck(ctx);
    drawLastMove(ctx);
}

function drawCheck(ctx) {
    if (!PREFERENCES.showCheck)
        return;

    for(const color of ['w', 'b']) {
        if (isChecked[color]) {
            ctx.beginPath();
            ctx.arc(kingPositions[color].col * cellSize + cellSize / 2, kingPositions[color].row * cellSize + cellSize / 2, cellSize / 2, 0, 2 * Math.PI);
            ctx.fillStyle = COLORS.CHECK;
            ctx.fill();
        }
    }
}

function drawLastMove(ctx) {
    if (PREFERENCES.showLastMove && lastMove !== null) {
        const from = lastMove.from;
        const to = lastMove.to;


        ctx.fillStyle = COLORS.LAST_MOVE;
        ctx.fillRect(from.col * cellSize, from.row * cellSize, cellSize, cellSize);
        ctx.fillRect(to.col * cellSize, to.row * cellSize, cellSize, cellSize);
    }
}

function drawMovableSpots(ctx) {
    clearCtx(ctx);
    if (!PREFERENCES.showPossibleMoves)
        return;

    for (let spot in movableSpots) {
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

function drawGameNotStarted(ctx) {
    const canvasSize = canvas.width;
    if (!gamestarted) {
        ctx.fillStyle = COLORS.LAYER;
        ctx.fillRect(0, 0, canvasSize, canvasSize);
        ctx.font = "30px Arial";
        ctx.fillStyle = COLORS.BLACK;
        ctx.textAlign = "center";
        ctx.fillText("Waiting for an opponent...", canvasSize / 2, canvasSize / 2);
    }
}

function drawHeldPiece(ctx) {
    if (!gamestarted)
        return;
    clearCtx(ctx);
    if (heldPiece == null)
        return;
    ctx.drawImage(heldPiece.img, mouseX - cellSize / 2, mouseY - cellSize / 2, cellSize, cellSize);
}


function clearCtx(ctx) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
}


function animate() {
    requestAnimationFrame(animate);
    renderChessboard();
}

animate();