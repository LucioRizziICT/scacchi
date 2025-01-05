const SOUNDS = {
    move: new Audio('/scacchi/audio/move.mp3'),
    capture: new Audio('/scacchi/audio/capture.mp3'),
    check: new Audio('/scacchi/audio/check.mp3'),
    gameover: new Audio('/scacchi/audio/game_over.mp3')
}

function playSound(sound) {
    sound.play();
}

function playMoveSound() {
    playSound(SOUNDS.move);
}

function playCaptureSound() {
    playSound(SOUNDS.capture);
}

function playCheckSound() {
    playSound(SOUNDS.check);
}

function playGameOverSound() {
    playSound(SOUNDS.gameover);
}