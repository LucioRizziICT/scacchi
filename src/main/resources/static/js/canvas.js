const canvas = document.getElementById('mainCanvas');
const mouse = { x: 0, y: 0 };

window.addEventListener('resize', resizeCanvas, false);

function resizeCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    ctx.fillStyle = 'rgba(255, 255, 255, 0.8)';
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

canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

const ctx = canvas.getContext('2d');
ctx.fillStyle = 'rgba(85,85,85,0.7)';

canvas.style.backgroundColor = 'rgb(235,235,235)';

const NUMBER_OF_CIRCLES = 150;
const MOUSE_RADIUS = 100;

const kingImg = document.getElementById('kingSprite');
const queenImg = document.getElementById('queenSprite');
const rookImg = document.getElementById('rookSprite');
const bishopImg = document.getElementById('bishopSprite');
const knightImg = document.getElementById('knightSprite');

function Sprite(x, y, xspeed, yspeed, radius, r, rspeed, drawing) {
    this.x = x;
    this.y = y;
    this.xspeed = xspeed;
    this.yspeed = yspeed;
    this.radius = radius;
    this.mass = 1;
    this.r = r;
    this.rspeed = rspeed;
    this.drawing = drawing;

    this.draw = function() {
        /*
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.radius, 0, Math.PI *2, false);
        ctx.fill();
         */
        ctx.setTransform(1, 0, 0, 1, this.x, this.y);
        ctx.rotate(this.r);
        ctx.drawImage(this.drawing, 0, 0, 240, 240, -30, -30, 60, 60);
    }

    this.update = function() {
        if(this.x + this.radius > innerWidth || this.x - this.radius < 0) {
            this.xspeed = -this.xspeed;
        }
        if(this.y + this.radius > innerHeight || this.y - this.radius < 0) {
            this.yspeed = -this.yspeed;
        }
        if (!mousedown) {
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
        } else {
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

        this.checkCollision();

        let maxSpeed = 1;
        let friction = 0.02;
        if (this.xspeed > maxSpeed) this.accelerateX(-friction);
        if (this.xspeed < -maxSpeed) this.accelerateX(friction);
        if (this.yspeed > maxSpeed) this.yspeed -= friction;
        if (this.yspeed < -maxSpeed) this.yspeed += friction;

        this.y += this.yspeed;
        this.x += this.xspeed;
        this.r += this.rspeed;
        this.draw();
    }
    this.checkCollision = function() {
        for (let i = 0; i < circles.length; i++) {
            if (this === circles[i]) continue;
            const dx = this.x - circles[i].x;
            const dy = this.y - circles[i].y;
            const distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < this.radius + circles[i].radius) {
                if (this.x - circles[i].x < 0) {
                    this.xspeed -= 0.01;
                } else {
                    this.xspeed += 0.01;
                }
                if (this.y - circles[i].y < 0) {
                    this.yspeed -= 0.01;
                } else {
                    this.yspeed += 0.01;
                }
            }
        }
    }

    this.accelerateX = function(n) {
        this.xspeed += n/this.mass;
    }

    this.accelerateY = function(n) {
        this.yspeed += n/this.mass;
    }
}



const circles = [];
for (let i = 0; i < NUMBER_OF_CIRCLES; i++) {
    let radius = 30;
    let x = Math.random() * (innerWidth - radius * 2) + radius;
    let xspeed = (Math.random() - 0.5) * (20 - radius)/6;
    let y = Math.random() * (innerHeight - radius * 2) + radius;
    let yspeed = (Math.random() - 0.5) * (20 - radius)/6;
    let rspeed = (Math.random() - 0.5) * 0.03;
    let drawing;
    switch (Math.floor(Math.random() * 5)) {
        case 0:
            drawing = kingImg;
            break;
        case 1:
            drawing = queenImg;
            break;
        case 2:
            drawing = rookImg;
            break;
        case 3:
            drawing = bishopImg;
            break;
        case 4:
            drawing = knightImg;
            break;
    }
    circles.push(new Sprite(x, y, xspeed, yspeed, radius,0, rspeed, drawing));
}
function animate() {
    requestAnimationFrame(animate);
    ctx.setTransform(1,0,0,1,0,0);
    ctx.clearRect(0, 0, innerWidth, innerHeight);
    drawGrid()


    for (let i = 0; i < circles.length; i++) {
        circles[i].update();
    }

}

function drawGrid() {
    const gridSize = 60; // Size of each square
    for (let x = 0; x < canvas.width; x += gridSize) {
        for (let y = 0; y < canvas.height; y += gridSize) {
            if (mouse.x >= x && mouse.x < x + gridSize && mouse.y >= y && mouse.y < y + gridSize) {
                if (mousedown) {
                    ctx.fillStyle = 'rgba(255,0,0,0.5)'; // Highlight color
                } else {
                    ctx.fillStyle = 'rgba(255,242,0,0.5)'; // Highlight color
                }
            } else {
                ctx.fillStyle = (x / gridSize + y / gridSize) % 2 === 0 ? '#f0d9b5' : '#b58863';
            }
            ctx.fillRect(x, y, gridSize, gridSize);

        }
    }
}
animate();