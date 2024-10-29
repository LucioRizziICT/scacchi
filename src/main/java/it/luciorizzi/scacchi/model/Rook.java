package it.luciorizzi.scacchi.model;

public class Rook extends Piece {
    public Rook(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'R');
    }

    @Override
    public MoveSet getPossibleMoves(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        super.addMovesInDirection(possibleMoves, gameBoard, 1, 0);
        super.addMovesInDirection(possibleMoves, gameBoard, -1, 0);
        super.addMovesInDirection(possibleMoves, gameBoard, 0, 1);
        super.addMovesInDirection(possibleMoves, gameBoard, 0, -1);
        return possibleMoves;
    }
}
