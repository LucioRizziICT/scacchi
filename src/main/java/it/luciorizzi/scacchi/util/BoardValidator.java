package it.luciorizzi.scacchi.util;

import it.luciorizzi.scacchi.model.BoardValidationException;
import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.movement.Move;
import it.luciorizzi.scacchi.model.movement.Position;

public class BoardValidator {
    public static boolean isValidMove(Move move) {
        return move != null
                && !move.getOrigin().equals(move.getDestination())
                && isValidPosition(move.getOrigin())
                && isValidPosition(move.getDestination());
    }

    public static boolean isValidPosition(Position position) {
        return position != null && position.row() >= 0 && position.column() >= 0 && position.row() < GameBoard.ROWS && position.column() < GameBoard.COLUMNS;
    }

    public static void validatePosition(Position position) throws BoardValidationException {
        if (!isValidPosition(position)) {
            throw new BoardValidationException("Invalid position: " + position.toString());
        }
    }

    public static void validateMove(Move move) {
        if (!isValidMove(move)) {
            throw new BoardValidationException("Invalid move: " + move.toString());
        }
    }
}

