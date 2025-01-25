package it.luciorizzi.scacchi.model;

import it.luciorizzi.scacchi.model.type.PieceColor;

public class DrawState {
    private boolean whiteRequest;
    private boolean blackRequest;

    public DrawState() {
        this.whiteRequest = false;
        this.blackRequest = false;
    }

    public void setDraw(PieceColor color) {
        if (color == PieceColor.WHITE) {
            this.whiteRequest = true;
        }
        else if (color == PieceColor.BLACK) {
            this.blackRequest = true;
        }
    }

    public boolean isDrawAccepted() {
        return this.whiteRequest && this.blackRequest;
    }

    public void deny() {
        this.whiteRequest = false;
        this.blackRequest = false;
    }
}
