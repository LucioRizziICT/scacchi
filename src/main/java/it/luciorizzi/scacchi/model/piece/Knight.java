package it.luciorizzi.scacchi.model.piece;

import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.model.movement.Position;

public class Knight extends Piece {
    public Knight(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'N');
    }

    @Override
    public boolean canMoveInL() {
        return true;
    }

    @Override
    protected MoveSet getPossibleMovesInternal(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i == 0 || j == 0 || Math.abs(i) == Math.abs(j)) {
                    continue;
                }
                Position to = new Position(position.row() + i, position.column() + j);
                if (gameBoard.isEmpty(to)) {
                    possibleMoves.addMovement(getPosition(), to);
                }
                else if (gameBoard.isEnemy(to, getColor())) {
                    possibleMoves.addCapture(getPosition(), to);
                }
            }
        }
        return possibleMoves;
    }
}
