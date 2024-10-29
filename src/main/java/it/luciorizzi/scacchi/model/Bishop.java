package it.luciorizzi.scacchi.model;

public class Bishop extends Piece {
    public Bishop(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'B');
    }

    @Override
    public MoveSet getPossibleMoves(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        super.addMovesInDirection(possibleMoves, gameBoard, 1, 1);
        super.addMovesInDirection(possibleMoves, gameBoard, 1, -1);
        super.addMovesInDirection(possibleMoves, gameBoard, -1, 1);
        super.addMovesInDirection(possibleMoves, gameBoard, -1, -1);
        return possibleMoves;
    }
}
