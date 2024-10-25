package it.luciorizzi.scacchi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public abstract class Piece {
    private final PieceColor color;
    Position position;

    public Set<Move> getPossibleMoves(GameBoard gameBoard) {
    }

    public void move() {
    }
}
