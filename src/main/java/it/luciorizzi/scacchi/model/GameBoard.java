package it.luciorizzi.scacchi.model;

public class GameBoard {
    private final static int ROWS = 8;
    private final static int COLUMNS = 8;
    private final Piece[][] board;

    public GameBoard() {
        board = new Piece[ROWS][COLUMNS];
    }

    public void initialize() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = new EmptyPiece(new Position(i, j));
            }
        }
    }

    public Piece getPiece(Position position) {
        if (position.row() < 0 || position.row() >= ROWS || position.column() < 0 || position.column() >= COLUMNS) {
            return null;
        }
        return board[position.row()][position.column()];
    }

    public boolean isEmpty(Position position) {
        return getPiece(position) instanceof EmptyPiece;
    }

    public boolean isEnemy(Position position, PieceColor color) {
        if (isEmpty(position)) {
            return false;
        }
        return getPiece(position).getColor() != color;
    }
}
