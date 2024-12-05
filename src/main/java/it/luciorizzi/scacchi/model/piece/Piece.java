package it.luciorizzi.scacchi.model.piece;

import it.luciorizzi.scacchi.model.*;
import it.luciorizzi.scacchi.model.movement.Move;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.movement.Position;
import it.luciorizzi.scacchi.model.type.PieceColor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public abstract class Piece {
    private final PieceColor color;
    Position position;
    char symbol;

    public boolean canMoveDiagonally() {
        return false;
    }

    public boolean canMoveHorizontally() {
        return false;
    }

    public boolean canMoveInL() {
        return false;
    }

    public MoveSet getPossibleMoves(GameBoard gameBoard) {
        if (!gameBoard.isCurrentPlayer(getColor())) {
            return new MoveSet();
        }
        MoveSet possibleMoves = getPossibleMovesInternal(gameBoard);
        MoveSet result = new MoveSet();

        for (Move move : possibleMoves.getMoves()) {
            if (!gameBoard.isIllegalMove(move)) {
                result.addMove(move);
            }
        }
        return result;
    }

    protected MoveSet getPossibleMovesInternal(GameBoard gameBoard) {
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
        if (!move.getOrigin().equals(position)) {
            return false;
        }
        return move(gameBoard, move.getDestination());
    }

    protected void addStraightMoves(MoveSet possibleMoves, GameBoard gameBoard) {
        addMovesInDirection(possibleMoves, gameBoard, 1, 0);
        addMovesInDirection(possibleMoves, gameBoard, -1, 0);
        addMovesInDirection(possibleMoves, gameBoard, 0, 1);
        addMovesInDirection(possibleMoves, gameBoard, 0, -1);
    }

    protected void addDiagonalMoves(MoveSet possibleMoves, GameBoard gameBoard) {
        addMovesInDirection(possibleMoves, gameBoard, 1, 1);
        addMovesInDirection(possibleMoves, gameBoard, -1, -1);
        addMovesInDirection(possibleMoves, gameBoard, 1, -1);
        addMovesInDirection(possibleMoves, gameBoard, -1, 1);
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

    public boolean canReach(GameBoard gameBoard, Position position) {
        return getPossibleMoves(gameBoard).canReach(position);
    }

    public boolean couldReach(GameBoard gameBoard, Position position) {
        return getPossibleMovesInternal(gameBoard).canReach(position);
    }

    public char getColorSymbol() {
        return color == PieceColor.WHITE ? Character.toUpperCase(symbol) : Character.toLowerCase(symbol);
    }
}
