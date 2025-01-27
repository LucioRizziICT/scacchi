package it.luciorizzi.scacchi.model;

import it.luciorizzi.scacchi.model.type.PieceColor;

public class Agreement {
    private boolean whiteAgree;
    private boolean blackAgree;

    public Agreement() {
        this.whiteAgree = false;
        this.blackAgree = false;
    }

    public void agree(PieceColor color) {
        if (color == PieceColor.WHITE) {
            this.whiteAgree = true;
        }
        else if (color == PieceColor.BLACK) {
            this.blackAgree = true;
        }
    }

    public boolean isAccepted() {
        return this.whiteAgree && this.blackAgree;
    }

    public void deny() {
        this.whiteAgree = false;
        this.blackAgree = false;
    }
}
