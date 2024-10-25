package it.luciorizzi.scacchi.model;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Piece {
    private boolean neverMoved = true;

    public Pawn(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public MoveSet getPossibleMoves(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        if(gameBoard.isEmpty(new Position(position.row() + getColor().getValue(), position.column()))) {
            possibleMoves.addMovement(getPosition(), position.row() + getColor().getValue(), position.column());
            if(neverMoved && gameBoard.isEmpty(new Position(position.row() + getColor().getValue() * 2, position.column()))) {
                possibleMoves.addMovement(getPosition(), position.row() + getColor().getValue() * 2, position.column());
            }
        }
        if(!gameBoard.isEmpty(new Position(position.row() + getColor().getValue(), position.column() + 1)) && gameBoard.isEnemy(new Position(position.row() + getColor().getValue(), position.column() + 1), getColor())) {
            possibleMoves.addCapture(getPosition(), position.row() + getColor().getValue(), position.column() + 1);
        }
        if(!gameBoard.isEmpty(new Position(position.row() + getColor().getValue(), position.column() - 1)) && gameBoard.isEnemy(new Position(position.row() + getColor().getValue(), position.column() - 1), getColor())) {
            possibleMoves.addCapture(getPosition(), position.row() + getColor().getValue(), position.column() - 1);
        }
        return possibleMoves;
    }
}
