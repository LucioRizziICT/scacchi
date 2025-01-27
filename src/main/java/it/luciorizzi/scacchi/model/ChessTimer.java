package it.luciorizzi.scacchi.model;

import it.luciorizzi.scacchi.model.type.PieceColor;

public class ChessTimer {
    private GameBoard parentBoard;

    private PlayerTimer whiteTimer;
    private PlayerTimer blackTimer;

    private PieceColor runningTimerColor = PieceColor.BLACK;

    public ChessTimer(long time, long increment, GameBoard parentBoard) {
        whiteTimer = new PlayerTimer(time * 1_000, increment * 1_000, PieceColor.WHITE, this);
        blackTimer = new PlayerTimer(time * 1_000, increment * 1_000, PieceColor.BLACK, this);
        this.parentBoard = parentBoard;
    }

    public PlayerTimer getCurrentTimer() {
        return runningTimerColor == PieceColor.WHITE ? whiteTimer : blackTimer;
    }

    public void stop() {
        whiteTimer.stop();
        blackTimer.stop();
    }

    public void switchTurn() {
        getCurrentTimer().stop();
        runningTimerColor = runningTimerColor.opposite();
        getCurrentTimer().start();
    }

    public void notifyTimeOver(PieceColor color) {
        parentBoard.handleTimeOver(color);
        //TODO: Add gameover websocket message somewhere, maybe in Gameboard
    }

    public void reset() {
        //TODO implement
    }
}
