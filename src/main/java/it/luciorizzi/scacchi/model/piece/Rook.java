package it.luciorizzi.scacchi.model.piece;

import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.model.movement.Position;
import lombok.Getter;

public class Rook extends Piece {
    public Rook(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'R');
    }

    private boolean neverMoved = true;

    public boolean hasNeverMoved() {
        return neverMoved;
    }

    @Override
    public boolean move(GameBoard gameBoard, Position to) {
        if (super.move(gameBoard, to)) {
            neverMoved = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean canMoveHorizontally() {
        return true;
    }

    @Override
    protected MoveSet getPossibleMovesInternal(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        super.addStraightMoves(possibleMoves, gameBoard);
        return possibleMoves;
    }
}
