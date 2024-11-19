package it.luciorizzi.scacchi.model.piece;

import it.luciorizzi.scacchi.model.*;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.movement.Position;
import it.luciorizzi.scacchi.model.type.PieceColor;

public class Bishop extends Piece {
    public Bishop(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'B');
    }

    @Override
    public boolean canMoveDiagonally() {
        return true;
    }

    @Override
    protected MoveSet getPossibleMovesInternal(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        super.addDiagonalMoves(possibleMoves, gameBoard);
        return possibleMoves;
    }
}
