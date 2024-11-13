package it.luciorizzi.scacchi.model;

public class Bishop extends Piece {
    public Bishop(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'B');
    }

    @Override
    public boolean canMoveDiagonally() {
        return true;
    }

    @Override
    public MoveSet getPossibleMovesInternal(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        super.addMovesInDirection(possibleMoves, gameBoard, 1, 1);
        super.addMovesInDirection(possibleMoves, gameBoard, 1, -1);
        super.addMovesInDirection(possibleMoves, gameBoard, -1, 1);
        super.addMovesInDirection(possibleMoves, gameBoard, -1, -1);
        return possibleMoves;
    }
}
