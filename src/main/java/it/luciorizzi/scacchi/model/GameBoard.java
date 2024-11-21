package it.luciorizzi.scacchi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.luciorizzi.scacchi.model.movement.*;
import it.luciorizzi.scacchi.model.piece.*;
import it.luciorizzi.scacchi.model.type.GameStatus;
import it.luciorizzi.scacchi.model.type.PieceColor;
import lombok.Getter;

import java.util.*;

public class GameBoard {
    public final static int ROWS = 8;
    public final static int COLUMNS = 8;

    private final Piece[][] board = new Piece[ROWS][COLUMNS];
    private PieceColor turn = PieceColor.WHITE;
    private Set<Piece> whitePieces = new HashSet<>();
    private Set<Piece> blackPieces = new HashSet<>();
    @Getter
    private GameStatus gameStatus = GameStatus.ONGOING;
    private Position whiteKingPosition;
    private Position blackKingPosition;
    private final Map<String, Integer> previousStates = new HashMap<>();
    private Pawn enPassantablePawn = null;

    @Getter
    private final MoveHistory movesHistory = new MoveHistory();

    public GameBoard() {
        initialize();
        saveCurrentState();
    }

    public void reset() {
        turn = PieceColor.WHITE;
        initialize();
        saveCurrentState();
        previousStates.clear();
        whitePieces.clear();
        blackPieces.clear();
        gameStatus = GameStatus.ONGOING;
        movesHistory.clear();
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
                sb.append(board[i][j].getColorSymbol());
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
            whitePieces.add(board[1][i]);
            board[6][i] = new Pawn(PieceColor.BLACK, new Position(6, i));
            blackPieces.add(board[6][i]);
        }
        board[0][0] = new Rook(PieceColor.WHITE, new Position(0, 0));
        board[0][7] = new Rook(PieceColor.WHITE, new Position(0, 7));
        whitePieces.add(board[0][0]);
        whitePieces.add(board[0][7]);
        board[7][0] = new Rook(PieceColor.BLACK, new Position(7, 0));
        board[7][7] = new Rook(PieceColor.BLACK, new Position(7, 7));
        blackPieces.add(board[7][0]);
        blackPieces.add(board[7][7]);
        board[0][1] = new Knight(PieceColor.WHITE, new Position(0, 1));
        board[0][6] = new Knight(PieceColor.WHITE, new Position(0, 6));
        whitePieces.add(board[0][1]);
        whitePieces.add(board[0][6]);
        board[7][1] = new Knight(PieceColor.BLACK, new Position(7, 1));
        board[7][6] = new Knight(PieceColor.BLACK, new Position(7, 6));
        blackPieces.add(board[7][1]);
        blackPieces.add(board[7][6]);
        board[0][2] = new Bishop(PieceColor.WHITE, new Position(0, 2));
        board[0][5] = new Bishop(PieceColor.WHITE, new Position(0, 5));
        whitePieces.add(board[0][2]);
        whitePieces.add(board[0][5]);
        board[7][2] = new Bishop(PieceColor.BLACK, new Position(7, 2));
        board[7][5] = new Bishop(PieceColor.BLACK, new Position(7, 5));
        blackPieces.add(board[7][2]);
        blackPieces.add(board[7][5]);
        board[0][3] = new Queen(PieceColor.WHITE, new Position(0, 3));
        whitePieces.add(board[0][3]);
        board[7][3] = new Queen(PieceColor.BLACK, new Position(7, 3));
        blackPieces.add(board[7][3]);
        board[0][4] = new King(PieceColor.WHITE, new Position(0, 4));
        whitePieces.add(board[0][4]);
        board[7][4] = new King(PieceColor.BLACK, new Position(7, 4));
        blackPieces.add(board[7][4]);
        whiteKingPosition = new Position(0, 4);
        blackKingPosition = new Position(7, 4);
    }

    public MoveSet getPossibleMoves(int row, int column) {
        return getPiece(new Position(row, column)).getPossibleMoves(this);
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

    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol, Character promotion) {
        return movePiece(new Position(fromRow, fromCol), new Position(toRow, toCol), promotion);
    }

    public boolean movePiece(Position origin, Position destination, Character promotion) {
        Piece movedPiece = getPiece(origin);
        if (movedPiece instanceof Pawn && (destination.row() == 0 || destination.row() == 7)) {
            if (promotion == null) {
                return false;
            }
            if (isEnemy(destination, movedPiece.getColor())) {
                return movePiece(Move.promotionCapture(origin, destination, promotion));
            }
            return movePiece(Move.promotionMovement(origin, destination, promotion));
        }
        if (isEnemy(destination, movedPiece.getColor())) {
            return movePiece(Move.capture(origin, destination));
        }
        if (movedPiece instanceof Pawn && Math.abs(destination.column() - origin.column()) == 1) {
            return movePiece(Move.enPassant(origin, destination));
        }
        if (movedPiece instanceof King && Math.abs(destination.column() - origin.column()) == 2) {
            return movePiece(Move.castling(origin, destination));
        }
        return movePiece(Move.movement(origin, destination));
    }

    public boolean movePiece(Move move) {
        if (move == null) {
            return false;
        }
        if (isEmpty(move.getOrigin())) {
            return false;
        }
        if (getPiece(move.getOrigin()).getColor() != turn) {
            return false;
        }
        if (getPiece(move.getOrigin()).move(this, move)) {
            if (move.isCastling()) {
                if (move.getDestination().column() == 2) {
                    getPiece(new Position(move.getDestination().row(), 0)).move(this, new Position(move.getDestination().row(), 3));
                } else {
                    getPiece(new Position(move.getDestination().row(), 7)).move(this, new Position(move.getDestination().row(), 5));
                }
            }

            applyMove(move);
            executePostMoveOperations(move);
            return true;
        }
        return false;
    }

    private void executePostMoveOperations(Move move) {
        handleEnPassantable(move);
        saveCurrentState();
        switchTurn();
        addMoveToHistory(move);
        gameStatus = checkGameStatus();
        if (gameStatus != GameStatus.ONGOING) {
            movesHistory.setOutcome(gameStatus);
        }
    }

    private void switchTurn() {
        turn = turn == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
    }

    private void addMoveToHistory(Move move) {
        Piece movedPiece = getPiece(move.getDestination());
        if (isCheck()) {
            if (isCheckmate()) {
                movesHistory.addCheckmate(move, movedPiece);
            } else {
                movesHistory.addCheck(move, movedPiece);
            }
        } else {
            movesHistory.add(move, movedPiece);
        }
    }

    private void handleEnPassantable(Move move) {
        if (enPassantablePawn != null) {
            enPassantablePawn.setEnPassantable(false);
            enPassantablePawn = null;
        }
        Piece movedPiece = getPiece(move.getDestination());
        if (movedPiece instanceof Pawn && ((Pawn) movedPiece).isEnPassantable()) {
            enPassantablePawn = (Pawn) movedPiece;
        }
    }

    private void applyMove(Move move) {
        Piece movedPiece = getPiece(move.getOrigin());
        updateKingPosition(move, movedPiece);
        if (move.isCapture())
            getPieces(getPiece(move.getDestination()).getColor()).remove(board[move.getDestination().row()][move.getDestination().column()]);
        if (move.isEnPassant()) {
            Position takenPiecePosition = new Position(move.getDestination().row() - turn.getValue(), move.getDestination().column());
            Piece takenPiece = getPiece(takenPiecePosition);
            board[takenPiecePosition.row()][takenPiecePosition.column()] = new EmptyPiece(takenPiecePosition);
            getPieces(turn.opposite()).remove(takenPiece);
        }
        if (move.isCastling()) {
            Position rookOrigin = new Position(move.getDestination().row(), move.getDestination().column() > 4 ? 7 : 0);
            Position rookDestination = new Position(move.getDestination().row(), move.getDestination().column() > 4 ? 5 : 3);
            board[rookDestination.row()][rookDestination.column()] = getPiece(rookOrigin);
            board[rookOrigin.row()][rookOrigin.column()] = new EmptyPiece(rookOrigin);
        }

        board[move.getDestination().row()][move.getDestination().column()] = getPiece(move.getOrigin());
        board[move.getOrigin().row()][move.getOrigin().column()] = new EmptyPiece(move.getOrigin());

        if (move.getPromotion() != null) {
            promotePiece(move, movedPiece);
        }
    }

    private void promotePiece(Move move, Piece movedPiece) {
        Piece promotedPiece = switch (move.getPromotion()) {
            case 'Q' -> new Queen(turn, move.getDestination());
            case 'R' -> new Rook(turn, move.getDestination());
            case 'N' -> new Knight(turn, move.getDestination());
            case 'B' -> new Bishop(turn, move.getDestination());
            default -> null;
        };
        getPieces(turn).remove(movedPiece);
        getPieces(turn).add(promotedPiece);
        board[move.getDestination().row()][move.getDestination().column()] = promotedPiece;
    }

    private void updateKingPosition(Move move, Piece movedPiece) {
        if (movedPiece instanceof King) {
            if (movedPiece.getColor() == PieceColor.WHITE) {
                whiteKingPosition = move.getDestination();
            } else {
                blackKingPosition = move.getDestination();
            }
        }
    }

    public void print() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                System.out.print(board[i][j].getColorSymbol() + " ");
            }
            System.out.println();
        }
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
        if (isCheck()) {
            return false;
        }
        for (Piece piece : getCurrentPlayerPieces()) {
            if (!piece.getPossibleMoves(this).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isMaterialInsufficient() {
        if(whitePieces.size() >= 3 || blackPieces.size() >= 3)
            return false;
        for (Piece piece : whitePieces) {
            if (!(piece instanceof Bishop || piece instanceof Knight || piece instanceof King)) {
                return false;
            }
        }
        for (Piece piece : blackPieces) {
            if (piece instanceof Bishop || piece instanceof Knight) {
                return true;
            }
        }
        return false;
    }

    private boolean gameRepeatedThreeTimes() {
        return previousStates.get(getPositionHash()) >= 3;
    }

    public boolean isCurrentPlayer(PieceColor color) {
        return turn == color;
    }

    public PieceColor getCurrentPlayer() {
        return turn;
    }

    public Position getCurrentPlayerKingPosition() { //TODO: MAKE PRIVATE
        return turn == PieceColor.WHITE ? whiteKingPosition : blackKingPosition;
    }

    private Set<Piece> getCurrentPlayerPieces() {
        return getPieces(turn);
    }

    private Set<Piece> getPieces(PieceColor color) {
        return color == PieceColor.WHITE ? whitePieces : blackPieces;
    }

    public boolean isCheck() {
        return isCheckInternal(getCurrentPlayerKingPosition(), turn);
    }

    private boolean isCheckInternal(Position kingPosition, PieceColor color) {
        for (Piece piece : getPieces(color.opposite())) {
            if (piece.couldReach(this, kingPosition)) {
                return true;
            }
        }

        return false;
    }

    public boolean isCheckmate() {
        if (!isCheck()) {
            return false;
        }
        for (Piece piece : getCurrentPlayerPieces()) {
            if (!piece.getPossibleMoves(this).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isIllegalMove(Move move) {
        if (move == null) {
            return true;
        }
        if (isEmpty(move.getOrigin())) {
            return true;
        }
        if (getPiece(move.getOrigin()).getColor() != turn) {
            return true;
        }
        return whouldAllowCheck(move);
    }

    private boolean whouldAllowCheck(Move move) {
        GameBoard copy = new GameBoard();
        copy.turn = turn;
        copy.whitePieces = new HashSet<>(whitePieces);
        copy.blackPieces = new HashSet<>(blackPieces);
        copy.gameStatus = gameStatus;
        copy.whiteKingPosition = whiteKingPosition;
        copy.blackKingPosition = blackKingPosition;
        for (int i = 0; i < ROWS; i++) {
            System.arraycopy(board[i], 0, copy.board[i], 0, COLUMNS);
        }
        copy.applyMove(move);
        return copy.isCheck();
    }
}
