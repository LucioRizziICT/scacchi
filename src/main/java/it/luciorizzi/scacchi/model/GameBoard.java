package it.luciorizzi.scacchi.model;

public class GameBoard {
    public final static int ROWS = 8;
    public final static int COLUMNS = 8;

    private final Piece[][] board;

    public GameBoard() {
        board = new Piece[ROWS][COLUMNS];
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = new EmptyPiece(new Position(i, j));
            }
        }
        for (int i = 0; i < COLUMNS; i++) {
            board[1][i] = new Pawn(PieceColor.WHITE, new Position(1, i));
            board[6][i] = new Pawn(PieceColor.BLACK, new Position(6, i));
        }
        board[0][0] = new Rook(PieceColor.WHITE, new Position(0, 0));
        board[0][7] = new Rook(PieceColor.WHITE, new Position(0, 7));
        board[7][0] = new Rook(PieceColor.BLACK, new Position(7, 0));
        board[7][7] = new Rook(PieceColor.BLACK, new Position(7, 7));
        board[0][1] = new Knight(PieceColor.WHITE, new Position(0, 1));
        board[0][6] = new Knight(PieceColor.WHITE, new Position(0, 6));
        board[7][1] = new Knight(PieceColor.BLACK, new Position(7, 1));
        board[7][6] = new Knight(PieceColor.BLACK, new Position(7, 6));
        board[0][2] = new Bishop(PieceColor.WHITE, new Position(0, 2));
        board[0][5] = new Bishop(PieceColor.WHITE, new Position(0, 5));
        board[7][2] = new Bishop(PieceColor.BLACK, new Position(7, 2));
        board[7][5] = new Bishop(PieceColor.BLACK, new Position(7, 5));
        board[0][3] = new Queen(PieceColor.WHITE, new Position(0, 3));
        board[7][3] = new Queen(PieceColor.BLACK, new Position(7, 3));
        board[0][4] = new King(PieceColor.WHITE, new Position(0, 4));
        board[7][4] = new King(PieceColor.BLACK, new Position(7, 4));
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
        Piece piece = getPiece(position);
        if (piece == null) {
            return false;
        }
        return piece.getColor() != color;
    }

    public boolean movePiece(Position origin, Position destination) {
        if (isEnemy(destination, getPiece(origin).getColor())) {
            return movePiece(new Move(origin, destination, true));
        }
        return movePiece(new Move(origin, destination, false));
    }

    public boolean movePiece(Move move) {
        if (move == null) {
            return false;
        }
        if (isEmpty(move.origin())) {
            return false;
        }
        if (getPiece(move.origin()).move(this, move)) {
            applyMove(move);
            return true;
        }
        return false;
    }

    private void applyMove(Move move) {
        board[move.destination().row()][move.destination().column()] = board[move.origin().row()][move.origin().column()];
        board[move.origin().row()][move.origin().column()] = new EmptyPiece(move.origin());
    }

    public boolean print() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                System.out.print(board[i][j].getSymbol() + " ");
            }
            System.out.println();
        }
        return true;
    }

    public void reset() {
        initialize();
    }
}
