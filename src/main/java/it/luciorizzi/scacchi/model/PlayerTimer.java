package it.luciorizzi.scacchi.model;

import it.luciorizzi.scacchi.model.type.PieceColor;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class PlayerTimer {
    private static final long NEVER_STARTED = -1;

    private long timeMillis;
    private final long incrementMillis;
    PieceColor color;

    private transient long lastNanoTime = NEVER_STARTED;

    private final ChessTimer parentTimer;

    public PlayerTimer(long timeMillis, long incrementMillis, PieceColor color, ChessTimer parentTimer) {
        this.timeMillis = timeMillis;
        this.incrementMillis = incrementMillis;
        this.color = color;
        this.parentTimer = parentTimer;
    }

    private final Timer timer = new Timer(100, this::updateTimer);

    private void updateTimer(ActionEvent actionEvent) {
        long currentNanoTime = System.nanoTime();
        long elapsedMillis = (currentNanoTime - lastNanoTime) / 1_000_000;
        checkTimerFinished(elapsedMillis);
    }

    private void checkTimerFinished(long elapsedMillis) {
        if (timeMillis <= elapsedMillis) {
            timeMillis = 0;
            timer.stop();
            parentTimer.notifyTimeOver(color);
        }
    }

    public void start() {
        timer.start();
        lastNanoTime = System.nanoTime();
    }

    public void stop() {
        timer.stop();
        if (lastNanoTime == NEVER_STARTED) {
            return;
        }
        long currentNanoTime = System.nanoTime();
        long elapsedMillis = (currentNanoTime - lastNanoTime) / 1_000_000;
        timeMillis -= elapsedMillis;
        System.out.println("timeMillis = " + timeMillis);
        timeMillis += incrementMillis;
    }

    public void reset() {
        //TODO: Implement reset
    }

    public long getTimeMillis() {
        if (timer.isRunning()) {
            long currentNanoTime = System.nanoTime();
            long elapsedMillis = (currentNanoTime - lastNanoTime) / 1_000_000;
            return timeMillis - elapsedMillis;
        } else {
            return timeMillis;
        }
    }
}
