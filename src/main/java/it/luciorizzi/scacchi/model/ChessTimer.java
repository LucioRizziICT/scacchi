package it.luciorizzi.scacchi.model;

import it.luciorizzi.scacchi.model.type.PieceColor;

public class ChessTimer {
    private final PlayerTimer whiteTimer;
    private final PlayerTimer blackTimer;
    private PieceColor runningColor = PieceColor.WHITE;

    private GameBoard parentBoard;

    public ChessTimer(long timeSeconds, long incrementSeconds) {
        whiteTimer = new PlayerTimer(timeSeconds*1000, incrementSeconds*1000, PieceColor.WHITE, this);
        blackTimer = new PlayerTimer(timeSeconds*1000, incrementSeconds*1000, PieceColor.BLACK, this);
    }

    public PlayerTimer getTimer(PieceColor color) {
        return color == PieceColor.WHITE ? whiteTimer : blackTimer;
    }

    public void stop() {
        whiteTimer.stop();
        blackTimer.stop();
    }

    public void switchTurn() {
        getTimer(runningColor).stop();
        runningColor = runningColor.opposite();
        getTimer(runningColor).start();
    }

    public void notifyTimeOver(PieceColor color) {
        parentBoard.handleTimeOver(color);
        //TODO: Add gameover websocket message
    }
}
