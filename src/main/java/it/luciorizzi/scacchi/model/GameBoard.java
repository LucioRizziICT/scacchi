package it.luciorizzi.scacchi.model;

import java.util.HashMap;
import java.util.Map;

public class GameBoard {
    public final static int ROWS = 8;
    public final static int COLUMNS = 8;

    private final Piece[][] board = new Piece[ROWS][COLUMNS];
    private PieceColor turn = PieceColor.WHITE;

    private Position whiteKingPosition;
    private Position blackKingPosition;
    private final Map<String, Integer> previousStates = new HashMap<>();

    public GameBoard() {
        initialize();
        saveCurrentState();
    }

    private void saveCurrentState() {
        String state = getPositionHash();
        if (previousStates.containsKey(state)) {
            previousStates.put(state, previousStates.get(state) + 1);
        } else {
            previousStates.put(state, 1);
        }
    }

    private String getPositionHash() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                sb.append(board[i][j].getSymbol());
            }
        }
        return sb.toString();
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
        whiteKingPosition = new Position(0, 4);
        blackKingPosition = new Position(7, 4);
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
        if (getPiece(move.origin()).getColor() != turn) {
            return false;
        }
        if (getPiece(move.origin()).move(this, move)) {
            applyMove(move);
            turn = turn == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
            return true;
        }
        return false;
    }

    private void applyMove(Move move) {
        Piece movedPiece = getPiece(move.origin());
        movedPiece.move(this, move);
        if (movedPiece instanceof King) {
            if (movedPiece.getColor() == PieceColor.WHITE) {
                whiteKingPosition = move.destination();
            } else {
                blackKingPosition = move.destination();
            }
        }
        board[move.destination().row()][move.destination().column()] = board[move.origin().row()][move.origin().column()];
        board[move.origin().row()][move.origin().column()] = new EmptyPiece(move.origin());
        saveCurrentState();
    }

    public void print() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                System.out.print(board[i][j].getSymbol() + " ");
            }
            System.out.println();
        }
    }

    public void reset() {
        turn = PieceColor.WHITE;
        initialize();
        previousStates.clear();
    }

    public GameStatus checkGameStatus() {
        if (isCheckmate()) {
            return turn == PieceColor.WHITE ? GameStatus.BLACK_WIN : GameStatus.WHITE_WIN;
        }
        if (hasSurrendered(PieceColor.WHITE)) {
            return GameStatus.BLACK_WIN;
        }
        if (hasSurrendered(PieceColor.BLACK)) {
            return GameStatus.WHITE_WIN;
        }
        if (gameRepeatedThreeTimes() || isMaterialInsufficient() || isStalemate() || isAgreedDraw()) { //TODO if time will be added add draw by time vs insufficient material
            return GameStatus.DRAW;
        }
        return GameStatus.ONGOING;
    }

    private boolean hasSurrendered(PieceColor pieceColor) {
        //Only cowards flee from the battlefield
        return false;
    }

    private boolean isAgreedDraw() {
        //Peace was never an option...
        return false;
    }

    private boolean isStalemate() {
        return false;
    }

    private boolean isMaterialInsufficient() {
        return false;
    }

    private boolean gameRepeatedThreeTimes() {
        return false;
    }

    public boolean isCurrentPlayer(PieceColor color) {
        return turn == color;
    }

    public Position getCurrentPlayerKingPosition() { //TODO: MAKE PRIVATE
        return turn == PieceColor.WHITE ? whiteKingPosition : blackKingPosition;
    }

    public boolean isCheck() {
        return isCheckInternal(getCurrentPlayerKingPosition(), turn);
    }

    private boolean isCheckInternal(Position kingPosition, PieceColor color) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (isEnemy(new Position(i, j), color)) {
                    if (getPiece(new Position(i, j)).couldReach(this, kingPosition)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isCheckmate() {
        return false;
    }
}
