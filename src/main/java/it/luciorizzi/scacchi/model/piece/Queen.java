package it.luciorizzi.scacchi.model.piece;

import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.model.movement.Position;

public class Queen extends Piece {
    public Queen(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'Q');
    }

    @Override
    public boolean canMoveDiagonally() {
        return true;
    }
    @Override
    public boolean canMoveHorizontally() {
        return true;
    }

    @Override
    protected MoveSet getPossibleMovesInternal(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        super.addDiagonalMoves(possibleMoves, gameBoard);
        super.addStraightMoves(possibleMoves, gameBoard);
        return possibleMoves;
    }
}
