package it.luciorizzi.scacchi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum PieceColor {
    WHITE(1), BLACK(-1);
    final int value;

    PieceColor(int value) {
        this.value = value;
    }

}
