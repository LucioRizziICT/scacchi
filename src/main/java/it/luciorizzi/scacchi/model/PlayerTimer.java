package it.luciorizzi.scacchi.model;

import it.luciorizzi.scacchi.model.type.PieceColor;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class PlayerTimer {
    private long timeMillis;
    private final long incrementMillis;
    PieceColor color;

    private transient long lastNanoTime;

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
        lastNanoTime = currentNanoTime;
        timeMillis -= elapsedMillis;
        checkTimerFinished();
    }

    private void checkTimerFinished() {
        if (timeMillis <= 0) {
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
        long currentNanoTime = System.nanoTime();
        long elapsedMillis = (currentNanoTime - lastNanoTime) / 1_000_000;
        lastNanoTime = currentNanoTime;
        timeMillis -= elapsedMillis;
        timeMillis += incrementMillis;
    }
}
