package it.luciorizzi.scacchi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Piece {
    private final PieceColor color;
    Position position;
    char symbol;

    public MoveSet getPossibleMoves(GameBoard gameBoard) {
        return new MoveSet();
    }

    public boolean move(GameBoard gameBoard, Position to) {
        if (to == null) {
            return false;
        }
        if (getPossibleMoves(gameBoard).canReach(to)) {
            position = to;
            return true;
        }
        return false;
    }

    public boolean move(GameBoard gameBoard, Move move) {
        if (move == null) {
            return false;
        }
        if (!move.origin().equals(position)) {
            return false;
        }
        return move(gameBoard, move.destination());
    }

    protected void addMovesInDirection(MoveSet possibleMoves, GameBoard gameBoard, int rowIncrement, int colIncrement) {
        for (int i = 1; i < GameBoard.ROWS; i++) {
            Position newPos = new Position(position.row() + i * rowIncrement, position.column() + i * colIncrement);
            if (gameBoard.isEmpty(newPos)) {
                possibleMoves.addMovement(position, newPos);
            } else if (gameBoard.isEnemy(newPos, getColor())) {
                possibleMoves.addCapture(position, newPos);
                break;
            } else {
                break;
            }
        }
    }
}
