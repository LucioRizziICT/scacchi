package it.luciorizzi.scacchi.model;

import it.luciorizzi.scacchi.model.movement.*;
import it.luciorizzi.scacchi.model.piece.*;
import it.luciorizzi.scacchi.model.type.GameOutcome;
import it.luciorizzi.scacchi.model.type.GameoverCause;
import it.luciorizzi.scacchi.model.type.PieceColor;
import lombok.Getter;

import java.util.*;

public class GameBoard { //TODO: add thread safety if needed
    public final static int ROWS = 8;
    public final static int COLUMNS = 8;

    private final Piece[][] board = new Piece[ROWS][COLUMNS];
    private PieceColor turn = PieceColor.WHITE;
    private Set<Piece> whitePieces = new HashSet<>();
    private Set<Piece> blackPieces = new HashSet<>();
    @Getter
    private boolean isOngoing = true;
    private Position whiteKingPosition;
    private Position blackKingPosition;
    private final Map<String, Integer> previousStates = new HashMap<>();
    private Pawn enPassantablePawn = null;
    private int fiftyMovesCounter = 0;
    private Agreement drawAgreement = new Agreement();
    private ChessTimer timer = null;
    @Getter
    private final MoveHistory movesHistory = new MoveHistory();

    private transient Boolean cachedCheck = null;

    public GameBoard() {
        initialize();
        saveCurrentState();
    }

    public GameBoard(int timeSeconds, int incrementSeconds) {
        initialize();
        saveCurrentState();
        timer = new ChessTimer(timeSeconds, incrementSeconds, this);
    }

    public char[][] getBoard() {
        char[][] board = new char[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = this.board[i][j].getColorSymbol();
            }
        }
        return board;
    }

    public void reset() {
        turn = PieceColor.WHITE;
        previousStates.clear();
        whitePieces.clear();
        blackPieces.clear();
        movesHistory.clear();
        drawAgreement = new Agreement();
        isOngoing = true;
        fiftyMovesCounter = 0;
        if (timer != null)
            timer.reset();
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

    public MoveSet getPossibleMoves(Position position) {
        if (!isOngoing) {
            return new MoveSet();
        }
        return getPiece(position).getPossibleMoves(this);
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
        if (!isOngoing) {
            return false;
        }
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

    private void executePostMoveOperations(Move move) {
        cachedCheck = null;
        fiftyMovesCounter++;
        handleEnPassantable(move);
        saveCurrentState();
        switchTurn();
        addMoveToHistory(move);
        checkGameStatus();
        if (isOngoing && timer != null) {
            timer.switchTurn();
        }
        if (!isOngoing && timer != null) {
            timer.stop();
        }
    }

    private void switchTurn() {
        turn = turn.opposite();
    }

    private void addMoveToHistory(Move move) {


        Piece movedPiece = getPiece(move.getDestination());
        Class<? extends Piece> movedPieceOriginalClass = move.getPromotion() != null ? Pawn.class : movedPiece.getClass();

        boolean disambiguationColumn = false;
        boolean disambiguationRow = false;

        if ( !(movedPiece instanceof King) ) { //TODO extraxt or refactor
            //TODO make thread safe (is now probably super unsafe)
            if (move.getMoveType() == MoveType.CAPTURE) {
                board[move.getDestination().row()][move.getDestination().column()] = new Queen(turn, move.getDestination());
            } else {
                board[move.getDestination().row()][move.getDestination().column()] = new EmptyPiece(move.getDestination());
            }


            for (Piece piece : getPieces(turn.opposite())) {
                if (movedPieceOriginalClass.equals(piece.getClass())) {
                    if (piece.couldReach(this, move.getDestination())) {
                        if (move.getOrigin().column() != piece.getPosition().column()) {
                            disambiguationColumn = true;
                        } else {
                            disambiguationRow = true;
                        }
                    }
                }
            }

            board[move.getDestination().row()][move.getDestination().column()] = movedPiece;
        }



        if (isCheck()) {
            if (isCheckmate()) {
                movesHistory.addCheckmate(move, movedPiece, disambiguationColumn, disambiguationRow);
            } else {
                movesHistory.addCheck(move, movedPiece, disambiguationColumn, disambiguationRow);
            }
        } else {
            movesHistory.add(move, movedPiece, disambiguationColumn, disambiguationRow);
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

    private void promotePiece(Move move, Piece movedPiece) {
        Piece promotedPiece = switch (move.getPromotion()) {
            case 'q' -> new Queen(turn, move.getDestination());
            case 'r' -> new Rook(turn, move.getDestination());
            case 'n' -> new Knight(turn, move.getDestination());
            case 'b' -> new Bishop(turn, move.getDestination());
            default -> throw new IllegalArgumentException("Invalid promotion character");
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

    @Deprecated(forRemoval = true)
    public void print() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                System.out.print(board[i][j].getColorSymbol() + " ");
            }
            System.out.println();
        }
    }

    private void checkGameStatus() {
        //Wins
        if (isCheckmate()) {
            isOngoing = false;
            movesHistory.setOutcome( new GameOutcome().withWinner(turn.opposite()).withCause(GameoverCause.CHECKMATE) );
        }
        //Draws
        else {
            if(gameRepeatedThreeTimes()) {
                isOngoing = false;
                movesHistory.setOutcome( new GameOutcome().withDraw().withCause(GameoverCause.THREEFOLD_REPETITION) );
            }
            if(isFiftyMovesRuleBroken()) {
                isOngoing = false;
                movesHistory.setOutcome( new GameOutcome().withDraw().withCause(GameoverCause.FIFTY_MOVES_RULE) );
            }
            if(isStalemate()) {
                isOngoing = false;
                movesHistory.setOutcome( new GameOutcome().withDraw().withCause(GameoverCause.STALEMATE) );
            }
            if(isMaterialInsufficient()) {
                isOngoing = false;
                movesHistory.setOutcome( new GameOutcome().withDraw().withCause(GameoverCause.INSUFFICIENT_MATERIAL) );
            }
            if(isAgreedDraw()) {
                isOngoing = false;
                movesHistory.setOutcome( new GameOutcome().withDraw().withCause(GameoverCause.AGREED_DRAW) );
            }
            //TODO if time will be added add draw by time vs insufficient material
        }
    }

    private boolean isFiftyMovesRuleBroken() {
        return fiftyMovesCounter >= 100; //100 moves = 50 turns
    }

    @Deprecated(forRemoval = true)
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
        return isMaterialInsufficient(PieceColor.WHITE) && isMaterialInsufficient(PieceColor.BLACK);
    }

    private boolean isMaterialInsufficient(PieceColor color) {
        Set<Piece> pieces = getPieces(color);
        if(pieces.size() >= 3)
            return false;
        for (Piece piece : whitePieces) {
            if (!(piece instanceof Bishop || piece instanceof Knight || piece instanceof King)) {
                return false;
            }
        }
        return true;
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

    private Position getCurrentPlayerKingPosition() {
        return turn == PieceColor.WHITE ? whiteKingPosition : blackKingPosition;
    }

    private Set<Piece> getCurrentPlayerPieces() {
        return getPieces(turn);
    }

    private Set<Piece> getPieces(PieceColor color) {
        return color == PieceColor.WHITE ? whitePieces : blackPieces;
    }

    public boolean isCheck() {
        if (cachedCheck == null) {
            cachedCheck = isCheckInternal(getCurrentPlayerKingPosition(), turn);
        }
        return cachedCheck;
    }

    private boolean isCheckInternal(Position kingPosition, PieceColor color) {
        for (Piece piece : getPieces(color.opposite())) {
            if (piece instanceof King) { //using manual king to avoid infinite loop call to isCheck
                if (Math.abs(piece.getPosition().row() - kingPosition.row()) <= 1 && Math.abs(piece.getPosition().column() - kingPosition.column()) <= 1) {
                    return true;
                }
                continue;
            }
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
        return wouldAllowCheck(move);
    }

    private boolean wouldAllowCheck(Move move) {
        GameBoard copy = new GameBoard();
        copy.turn = turn;
        copy.whitePieces = new HashSet<>(whitePieces);
        copy.blackPieces = new HashSet<>(blackPieces);
        copy.isOngoing = isOngoing;
        copy.whiteKingPosition = whiteKingPosition;
        copy.blackKingPosition = blackKingPosition;
        for (int i = 0; i < ROWS; i++) {
            System.arraycopy(board[i], 0, copy.board[i], 0, COLUMNS);
        }
        copy.applyMove(move);
        copy.cachedCheck = null;
        return copy.isCheck();
    }

    public void handleTimeOver(PieceColor color) {
        isOngoing = false;
        if (isMaterialInsufficient(color.opposite()))
            movesHistory.setOutcome( new GameOutcome().withDraw().withCause(GameoverCause.TIME_VS_INSUFFICIENT_MATERIAL) );
        else
            movesHistory.setOutcome( new GameOutcome().withWinner(color.opposite()).withCause(GameoverCause.TIME_EXPIRED) );
    }

    public void resign(PieceColor color) {
        if (!isOngoing())
            return;
        isOngoing = false;
        movesHistory.setOutcome( new GameOutcome().withWinner(color.opposite()).withCause(GameoverCause.RESIGNATION) );
    }

    /**
     * @param color the color of the player requesting the draw
     * @return true if the draw is accepted with this call
     */
    public boolean requestDraw(PieceColor color) {
        if (!isOngoing())
            return false;
        drawAgreement.agree(color);
        if (drawAgreement.isAccepted()) {
            isOngoing = false;
            movesHistory.setOutcome( new GameOutcome().withDraw().withCause(GameoverCause.AGREED_DRAW) );
            return true;
        }
        return false;
    }

    public void denyDraw() {
        if (!isOngoing())
            return;
        drawAgreement.deny();
    }

    //TODO validate move method
}