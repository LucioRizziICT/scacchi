const Colors = {
    LIGHT_SQUARE: '#f0d9b5',
    DARK_SQUARE: '#b58863',
    MOUSE_UP_SQUARE: 'rgba(255, 255, 0, 0.5)',
    MOUSE_DOWN_SQUARE: 'rgba(255,4,4,0.5)'
}

const canvas = document.getElementById('mainCanvas');
resizeCanvas();
const ctx = canvas.getContext('2d');

const NUMBER_OF_PIECES = 100;
const MOUSE_RADIUS = 100;
const PIECE_RADIUS = 30;
const SPRITE_SIZE = 240;
const DRAWING_SIZE = 60;
const PIECE_MAX_SPEED = 1;
const PIECE_FRICTION = 0.02;
const ADJUSTMENT_FACTOR = 120;
const mouse = { x: 0, y: 0 };

window.addEventListener('resize', resizeCanvas, false);

function resizeCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
}

window.addEventListener('mousemove', function(event) {
    mouse.x = event.clientX;
    mouse.y = event.clientY;
});

canvas.addEventListener('contextmenu', function(event) {
    event.preventDefault();
});

let mousedown = false;
window.addEventListener('mousedown', function(event) {
    mousedown = true;
});

window.addEventListener('mouseup', function(event) {
    mousedown = false;
});


const kingImg = document.getElementById('kingSprite');
const queenImg = document.getElementById('queenSprite');
const rookImg = document.getElementById('rookSprite');
const bishopImg = document.getElementById('bishopSprite');
const knightImg = document.getElementById('knightSprite');

const Sprites = [kingImg, queenImg, rookImg, bishopImg, knightImg];

function Piece(x, y, xspeed, yspeed, radius, r, rspeed, drawing) {
    this.x = x;
    this.y = y;
    this.xspeed = xspeed;
    this.yspeed = yspeed;
    this.radius = radius;
    this.r = r;
    this.rspeed = rspeed;
    this.drawing = drawing;

    this.draw = function() {
        ctx.setTransform(1, 0, 0, 1, this.x, this.y);
        ctx.rotate(this.r);
        ctx.drawImage(this.drawing, 0, 0, SPRITE_SIZE, SPRITE_SIZE, -DRAWING_SIZE/2, -DRAWING_SIZE/2, DRAWING_SIZE, DRAWING_SIZE);
    }

    this.update = function(deltaTime) {
        this.checkBorderCollision();
        this.checkMouseInteraction();
        this.checkCollisionWithPieces();
        this.addFriction();

        this.move(deltaTime);
        this.draw();
    }

    this.checkBorderCollision = function() {
        if(this.x + this.radius > innerWidth || this.x - this.radius < 0) {
            this.xspeed = -this.xspeed;
        }
        if(this.y + this.radius > innerHeight || this.y - this.radius < 0) {
            this.yspeed = -this.yspeed;
        }
    }

    this.checkMouseInteraction = function() {
        if (!mousedown)
            this.repelFromMouse();
        else
            this.attractToMouse();
    }

    this.repelFromMouse = function() {
        if (Math.sqrt(Math.pow(this.x - mouse.x, 2) + Math.pow(this.y - mouse.y, 2)) < radius + MOUSE_RADIUS) {
            let rate = 0.05;
            if (this.x - mouse.x < 0) {
                this.accelerateX(-rate);
            } else {
                this.accelerateX(rate);
            }
            if (this.y - mouse.y < 0) {
                this.accelerateY(-rate);
            } else {
                this.accelerateY(rate);
            }
        }
    }

    this.attractToMouse = function() {
        if (Math.sqrt(Math.pow(this.x - mouse.x, 2) + Math.pow(this.y - mouse.y, 2)) < radius + MOUSE_RADIUS*2) {
            let rate = 0.03;
            if (this.x - mouse.x < 0) {
                this.accelerateX(rate);
            } else {
                this.accelerateX(-rate);
            }
            if (this.y - mouse.y < 0) {
                this.accelerateY(rate);
            } else {
                this.accelerateY(-rate);
            }
        }
    }

    this.checkCollisionWithPieces = function() {
        for (let i = 0; i < pieces.length; i++) {
            if (this === pieces[i])
                continue;

            const dx = this.x - pieces[i].x;
            const dy = this.y - pieces[i].y;
            const distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < this.radius + pieces[i].radius) {
                if (this.x - pieces[i].x < 0) {
                    this.accelerateX(-0.01);
                } else {
                    this.accelerateX(0.01);
                }
                if (this.y - pieces[i].y < 0) {
                    this.accelerateY(-0.01);
                } else {
                    this.accelerateY(0.01);
                }
            }
        }
    }

    this.addFriction = function() {
        if (this.xspeed > PIECE_MAX_SPEED) this.accelerateX(-PIECE_FRICTION);
        if (this.xspeed < -PIECE_MAX_SPEED) this.accelerateX(PIECE_FRICTION);
        if (this.yspeed > PIECE_MAX_SPEED) this.accelerateY(-PIECE_FRICTION);
        if (this.yspeed < -PIECE_MAX_SPEED) this.accelerateY(PIECE_FRICTION);
    }

    this.move = function(deltaTime) {
        this.y += this.yspeed * deltaTime * ADJUSTMENT_FACTOR;
        this.x += this.xspeed * deltaTime * ADJUSTMENT_FACTOR;
        this.r += this.rspeed * deltaTime * ADJUSTMENT_FACTOR;
    }

    this.accelerateX = function(n) {
        this.xspeed += n;
    }

    this.accelerateY = function(n) {
        this.yspeed += n;
    }
}

const pieces = [];

for (let i = 0; i < NUMBER_OF_PIECES; i++) {
    pieces.push(createNewPiece());
}

function createNewPiece() {
    let radius = PIECE_RADIUS;
    let x = Math.random() * (innerWidth - radius * 2) + radius;
    let xspeed = (Math.random() - 0.5) * (20 - radius)/6;
    let y = Math.random() * (innerHeight - radius * 2) + radius;
    let yspeed = (Math.random() - 0.5) * (20 - radius)/6;
    let rspeed = (Math.random() - 0.5) * 0.03;
    let drawing = Sprites[Math.floor(Math.random() * Sprites.length)];
    return new Piece(x, y, xspeed, yspeed, radius,0, rspeed, drawing)
}

let lastTime = Date.now();

function animate() {
    requestAnimationFrame(animate);
    const time = Date.now();
    const deltaTime = (time - lastTime) / 1000; // Convert to seconds
    lastTime = time;

    ctx.setTransform(1, 0, 0, 1, 0, 0);
    drawBackground();
    for (let i = 0; i < pieces.length; i++) {
        pieces[i].update(deltaTime);
    }
}

function drawBackground() {
    const gridSize = 60; // Size of each square
    for (let x = 0; x < canvas.width; x += gridSize) {
        for (let y = 0; y < canvas.height; y += gridSize) {
            ctx.fillStyle = (x / gridSize + y / gridSize) % 2 === 0 ? Colors.LIGHT_SQUARE : Colors.DARK_SQUARE;
            if (mouse.x >= x && mouse.x < x + gridSize && mouse.y >= y && mouse.y < y + gridSize) {
                if (mousedown) {
                    ctx.fillStyle = Colors.MOUSE_DOWN_SQUARE;
                } else {
                    ctx.fillStyle = Colors.MOUSE_UP_SQUARE;
                }
            }
            ctx.fillRect(x, y, gridSize, gridSize);
        }
    }
}


animate();