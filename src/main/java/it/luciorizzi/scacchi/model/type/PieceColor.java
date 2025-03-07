package it.luciorizzi.scacchi.model.type;

import lombok.Getter;

@Getter
public enum PieceColor {
    WHITE(1), BLACK(-1);
    final int value;

    PieceColor(int value) {
        this.value = value;
    }

    public PieceColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }

}
