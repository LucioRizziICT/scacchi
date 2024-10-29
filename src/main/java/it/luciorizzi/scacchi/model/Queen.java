package it.luciorizzi.scacchi.model;

public class Queen extends Piece {
    public Queen(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'Q');
    }

    @Override
    public MoveSet getPossibleMoves(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        super.addMovesInDirection(possibleMoves, gameBoard, 1, 0);
        super.addMovesInDirection(possibleMoves, gameBoard, -1, 0);
        super.addMovesInDirection(possibleMoves, gameBoard, 0, 1);
        super.addMovesInDirection(possibleMoves, gameBoard, 0, -1);
        super.addMovesInDirection(possibleMoves, gameBoard, 1, 1);
        super.addMovesInDirection(possibleMoves, gameBoard, 1, -1);
        super.addMovesInDirection(possibleMoves, gameBoard, -1, 1);
        super.addMovesInDirection(possibleMoves, gameBoard, -1, -1);
        return possibleMoves;
    }
}
