package it.luciorizzi.scacchi.model.piece;

import it.luciorizzi.scacchi.model.*;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.movement.Position;
import it.luciorizzi.scacchi.model.type.PieceColor;
import lombok.Getter;
import lombok.Setter;

public class Pawn extends Piece {
    private boolean neverMoved = true;
    @Setter
    @Getter
    private boolean enPassantable = false;

    public Pawn(PieceColor color, Position position) {
        super(color, position, 'P');
    }

    @Override
    protected MoveSet getPossibleMovesInternal(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        if (gameBoard.isEmpty(new Position(position.row() + getColor().getValue(), position.column()))) {
            possibleMoves.addMovement(getPosition(), position.row() + getColor().getValue(), position.column());
            if (neverMoved && gameBoard.isEmpty(new Position(position.row() + getColor().getValue() * 2, position.column()))) {
                possibleMoves.addMovement(getPosition(), position.row() + getColor().getValue() * 2, position.column());
            }
        }

        Position diag1 = new Position(position.row() + getColor().getValue(), position.column() - 1);
        if (gameBoard.isEnemy(diag1, getColor())) {
            possibleMoves.addCapture(getPosition(), diag1);
        }
        Position diag2 = new Position(position.row() + getColor().getValue(), position.column() + 1);
        if (gameBoard.isEnemy(diag2, getColor())) {
            possibleMoves.addCapture(getPosition(), diag2);
        }
        Position side1 = new Position(position.row(), position.column() - 1);
        if (gameBoard.isEnemy(side1, getColor()) && gameBoard.getPieceAt(side1) instanceof Pawn && ((Pawn) gameBoard.getPieceAt(side1)).enPassantable) {
            possibleMoves.addEnPassant(getPosition(), diag1);
        }
        Position side2 = new Position(position.row(), position.column() + 1);
        if (gameBoard.isEnemy(side2, getColor()) && gameBoard.getPieceAt(side2) instanceof Pawn && ((Pawn) gameBoard.getPieceAt(side2)).enPassantable) {
            possibleMoves.addEnPassant(getPosition(), diag2);
        }
        return possibleMoves;
    }

    @Override
    public boolean move(GameBoard gameBoard, Position to) {
        if (super.move(gameBoard, to)) {
            if (Math.abs(to.row() - position.row()) == 2) {
                enPassantable = true;
            }
            neverMoved = false;
            return true;
        }
        return false;
    }
}
