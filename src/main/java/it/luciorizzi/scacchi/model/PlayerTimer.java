package it.luciorizzi.scacchi.model;

import it.luciorizzi.scacchi.model.type.PieceColor;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class PlayerTimer {
    private long timeMillis;
    private final long incrementMillis;
    PieceColor color;

    private final ChessTimer parentTimer;

    public PlayerTimer(long timeMillis, long incrementMillis, PieceColor color, ChessTimer parentTimer) {
        this.timeMillis = timeMillis;
        this.incrementMillis = incrementMillis;
        this.color = color;
        this.parentTimer = parentTimer;
    }

    private final Timer timer = new Timer(10, this::updateTimer);

    private void updateTimer(ActionEvent actionEvent) {
        timeMillis -= 10;
        if (timeMillis <= 0) {
            timer.stop();
            parentTimer.notifyTimeOver(color);
        }
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
        timeMillis += incrementMillis;
    }
}
