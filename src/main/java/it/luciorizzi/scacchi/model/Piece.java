package it.luciorizzi.scacchi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public abstract class Piece {
    private final PieceColor color;
    Position position;

    public MoveSet getPossibleMoves(GameBoard gameBoard) {
        return new MoveSet();
    }

    public void move() {
    }
}
