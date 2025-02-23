package it.luciorizzi.scacchi.model.timer;

import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.util.EventPublisherHelper;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

public class ChessTimer {
    private static final long MAX_TIME = 10*60*60 - 1; // 9 hours 59 minutes 59 seconds
    private static final long MAX_INCREMENT = 30*60; // 30 minutes

    private final GameBoard parentBoard;
    @Getter
    private final String lobbyId;

    private final AtomicBoolean finished = new AtomicBoolean(false);

    private final PlayerTimer whiteTimer;
    private final PlayerTimer blackTimer;

    private PieceColor runningTimerColor = PieceColor.WHITE;

    public ChessTimer(long time, long increment, GameBoard parentBoard, String lobbyId) throws IllegalArgumentException {
        if (time <= 0 || time > MAX_TIME || increment < 0 || increment > MAX_INCREMENT) {
            throw new IllegalArgumentException("Illegal time or increment value");
        }
        whiteTimer = new PlayerTimer(time * 1_000, increment * 1_000, PieceColor.WHITE, this);
        blackTimer = new PlayerTimer(time * 1_000, increment * 1_000, PieceColor.BLACK, this);
        this.parentBoard = parentBoard;
        this.lobbyId = lobbyId;
    }

    public PlayerTimer getCurrentTimer() {
        return runningTimerColor == PieceColor.WHITE ? whiteTimer : blackTimer;
    }

    public void stop() {
        whiteTimer.stop();
        blackTimer.stop();
    }

    public void switchTurn() {
        if (finished.get()) {
            return;
        }
        getCurrentTimer().stop();
        runningTimerColor = runningTimerColor.opposite();
        getCurrentTimer().start();
    }

    public void notifyTimeOver(PieceColor color) {
        if (finished.getAndSet(true)) {
            return;
        }
        System.out.println("Time over for " + color);
        if ( parentBoard.handleTimeOver(color) ) {
            EventPublisherHelper.publishEvent(new TimedGameTimeoutEvent(this));
        }
    }

    public void reset() {
        whiteTimer.reset();
        blackTimer.reset();
    }

    public TimerInfo getTimerInfo() {
        return new TimerInfo(whiteTimer.getTimeMillis(), blackTimer.getTimeMillis());
    }
}
