package it.luciorizzi.scacchi.model;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Piece {
    private boolean neverMoved = true;

    public Pawn(PieceColor color) {
        super(color);
    }

    @Override
    public Set<Move> getPossibleMoves(GameBoard gameBoard) {
        Set<Move> possibleMoves = new HashSet<>();

        return possibleMoves;
    }
}
