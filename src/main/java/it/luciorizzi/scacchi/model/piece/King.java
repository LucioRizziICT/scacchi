package it.luciorizzi.scacchi.model.piece;

import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.movement.Move;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.model.movement.Position;

public class King extends Piece {
    public King(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'K');
    }

    private boolean neverMoved = true;

    @Override
    public boolean move(GameBoard gameBoard, Position to) {
        if (super.move(gameBoard, to)) {
            neverMoved = false;
            return true;
        }
        return false;
    }

    @Override
    protected MoveSet getPossibleMovesInternal(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
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
        if (neverMoved && !gameBoard.isCheck()) {
            Position leftRook = new Position(position.row(), 0);
            if (gameBoard.getPiece(leftRook) instanceof Rook && ((Rook) gameBoard.getPiece(leftRook)).hasNeverMoved()) {
                boolean canCastle = true;
                for (int i = 1; i < position.column(); i++) {
                    if (!gameBoard.isEmpty(new Position(position.row(), i))) {
                        canCastle = false;
                        break;
                    }
                }
                if (canCastle && !gameBoard.isIllegalMove(Move.movement(getPosition(), new Position(position.row(), position.column() - 1)))) {
                    possibleMoves.addCastling(getPosition(), new Position(position.row(), position.column() - 2));
                }
            }
            Position rightRook = new Position(position.row(), 7);
            if (gameBoard.getPiece(rightRook) instanceof Rook && ((Rook) gameBoard.getPiece(rightRook)).hasNeverMoved()) {
                boolean canCastle = true;
                for (int i = position.column() + 1; i < 7; i++) {
                    if (!gameBoard.isEmpty(new Position(position.row(), i))) {
                        canCastle = false;
                        break;
                    }
                }
                if (canCastle && !gameBoard.isIllegalMove(Move.movement(getPosition(), new Position(position.row(), position.column() + 1)))) {
                    possibleMoves.addCastling(getPosition(), new Position(position.row(), position.column() + 2));
                }
            }
        }
        return possibleMoves;
    }
}
