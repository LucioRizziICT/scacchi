package it.luciorizzi.scacchi.model;

import lombok.NonNull;

public class Pawn extends Piece {
    private boolean neverMoved = true;

    public Pawn(PieceColor color, Position position) {
        super(color, position, 'P');
    }

    @Override
    public MoveSet getPossibleMoves(@NonNull GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        if (gameBoard.isEmpty(new Position(position.row() + getColor().getValue(), position.column()))) {
            possibleMoves.addMovement(getPosition(), position.row() + getColor().getValue(), position.column());
            if (neverMoved && gameBoard.isEmpty(new Position(position.row() + getColor().getValue() * 2, position.column()))) {
                possibleMoves.addMovement(getPosition(), position.row() + getColor().getValue() * 2, position.column());
            }
        }

        Position side1 = new Position(position.row() + getColor().getValue(), position.column() - 1);
        if (gameBoard.isEnemy(side1, getColor())) {
            possibleMoves.addCapture(getPosition(), side1);
        }
        Position side2 = new Position(position.row() + getColor().getValue(), position.column() + 1);
        if (gameBoard.isEnemy(side2, getColor())) {
            possibleMoves.addCapture(getPosition(), side2);
        }
        return possibleMoves;
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
    public boolean move(GameBoard gameBoard, Move move) {
        if (super.move(gameBoard, move)) {
            neverMoved = false;
            return true;
        }
        return false;
    }
}
