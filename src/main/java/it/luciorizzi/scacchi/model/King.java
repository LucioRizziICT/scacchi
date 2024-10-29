package it.luciorizzi.scacchi.model;

public class King extends Piece {
    public King(PieceColor pieceColor, Position position) {
        super(pieceColor, position, 'K');
    }

    @Override
    public MoveSet getPossibleMoves(GameBoard gameBoard) {
        MoveSet possibleMoves = new MoveSet();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                Position to = new Position(position.row() + i, position.column() + j);
                if (gameBoard.isEmpty(to)) {
                    possibleMoves.addMovement(getPosition(), to);
                }
                else if (gameBoard.isEnemy(to, getColor())) {
                    possibleMoves.addCapture(getPosition(), to);
                }
            }
        }
        return possibleMoves;
    }
}
