package it.luciorizzi.scacchi.model.piece;

import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.model.movement.Position;

public class Rook extends Piece {
    public Rook(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'R');
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
