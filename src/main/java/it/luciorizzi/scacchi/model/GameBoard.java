package it.luciorizzi.scacchi.model;

import it.luciorizzi.scacchi.model.movement.*;
import it.luciorizzi.scacchi.model.piece.*;
import it.luciorizzi.scacchi.model.timer.ChessTimer;
import it.luciorizzi.scacchi.model.timer.TimerInfo;
import it.luciorizzi.scacchi.model.type.GameOutcome;
import it.luciorizzi.scacchi.model.type.GameoverCause;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.util.BoardValidator;
import lombok.Getter;

import java.util.*;

public class GameBoard { //TODO: add thread safety
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

    public GameBoard(int timeSeconds, int incrementSeconds, String lobbyId) throws IllegalArgumentException{
        initialize();
        saveCurrentState();
        timer = new ChessTimer(timeSeconds, incrementSeconds, this, lobbyId);
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
        fiftyMovesCounter = 0;
        initialize();
        saveCurrentState();
        if (timer != null)
            timer.reset();
        isOngoing = true;
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

    /**
     * Sets the board to the initial default position of a chess game, fills the PiecesSets with the initial pieces and sets initial kings positions
     */
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

    public MoveSet getPossibleMoves(Position position) throws BoardValidationException {
        if (!isOngoing) {
            return new MoveSet();
        }
        return getPieceAt(position).getPossibleMoves(this);
    }

    public boolean isEmpty(Position position) {
        if (!BoardValidator.isValidPosition(position)) {
            return false;
        }
        return getPieceAt(position) instanceof EmptyPiece;
    }

    public boolean isEnemy(Position position, PieceColor color) {
        if (!BoardValidator.isValidPosition(position)) {
            return false;
        }
        if (isEmpty(position)) {
            return false;
        }
        Piece piece = getPieceAt(position);
        if (piece == null) {
            return false;
        }
        return piece.getColor() != color;
    }

    /**
     * @return true if the move is correctly executed, false otherwise
     * @throws BoardValidationException if the move is invalid
     * @see BoardValidator
     */
    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol, Character promotion) throws BoardValidationException {
        if (!isOngoing)
            return false;

        return movePieceWithCorrectMove(new Position(fromRow, fromCol), new Position(toRow, toCol), promotion);
    }

    private boolean movePieceWithCorrectMove(Position origin, Position destination, Character promotion) throws BoardValidationException {
        Piece movedPiece = getPieceAt(origin);

        BoardValidator.validatePosition(destination);

        if (movedPiece instanceof Pawn && (destination.row() == 0 || destination.row() == 7)) {
            if (promotion == null) {
                return false;
            }
            if (isEnemy(destination, movedPiece.getColor())) {
                return movePieceInternal(Move.promotionCapture(origin, destination, promotion));
            }
            return movePieceInternal(Move.promotionMovement(origin, destination, promotion));
        }
        if (isEnemy(destination, movedPiece.getColor())) {
            return movePieceInternal(Move.capture(origin, destination));
        }
        if (movedPiece instanceof Pawn && Math.abs(destination.column() - origin.column()) == 1) {
            return movePieceInternal(Move.enPassant(origin, destination));
        }
        if (movedPiece instanceof King && Math.abs(destination.column() - origin.column()) == 2) {
            return movePieceInternal(Move.castling(origin, destination));
        }
        return movePieceInternal(Move.movement(origin, destination));
    }

    private boolean movePieceInternal(Move move) throws BoardValidationException {
        if (getPieceAt(move.getOrigin()).getColor() != turn)
            return false;

        if (getPieceAt(move.getOrigin()).move(this, move.getDestination())) {
            if (move.isCastling()) {
                moveCastlingRook(move);
            }

            applyMove(move);
            executePostMoveOperations(move);
            return true;
        }
        return false;
    }

    private void moveCastlingRook(Move move) {
        if (move.getDestination().column() == 2) {
            getPieceAt(new Position(move.getDestination().row(), 0)).move(this, new Position(move.getDestination().row(), 3));
        } else {
            getPieceAt(new Position(move.getDestination().row(), 7)).move(this, new Position(move.getDestination().row(), 5));
        }
    }

    private void applyMove(Move move) {
        Piece movedPiece = getPieceAt(move.getOrigin());

        updateKingPosition(move, movedPiece);
        handleCaptureMove(move);
        handleEnPassantMove(move);
        handleCastlingMove(move);
        actuallyMovePiece(move, movedPiece);
        handlePromotion(move, movedPiece);
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

    private void handleCaptureMove(Move move) {
        if (move.isCapture())
            getPieces(getPieceAt(move.getDestination()).getColor()).remove(getPieceAt(move.getDestination()));
    }

    private void handleEnPassantMove(Move move) {
        if (move.isEnPassant()) {
            Position takenPiecePosition = new Position(move.getDestination().row() - turn.getValue(), move.getDestination().column());
            Piece takenPiece = getPieceAt(takenPiecePosition);
            setPieceAt(takenPiecePosition, new EmptyPiece(takenPiecePosition));
            getPieces(turn.opposite()).remove(takenPiece);
        }
    }

    private void handleCastlingMove(Move move) {
        if (move.isCastling()) {
            Position rookOrigin = new Position(move.getDestination().row(), move.getDestination().column() > 4 ? 7 : 0);
            Position rookDestination = new Position(move.getDestination().row(), move.getDestination().column() > 4 ? 5 : 3);
            setPieceAt(rookDestination, getPieceAt(rookOrigin));
            setPieceAt(rookOrigin, new EmptyPiece(rookOrigin));
        }
    }

    private void actuallyMovePiece(Move move, Piece movedPiece) {
        setPieceAt(move.getDestination(), movedPiece);
        setPieceAt(move.getOrigin(), new EmptyPiece(move.getOrigin()));
    }

    private void handlePromotion(Move move, Piece movedPiece) {
        if (move.getPromotion() != null) {
            promotePiece(move, movedPiece);
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
        setPieceAt(move.getDestination(), promotedPiece);
    }


    private void executePostMoveOperations(Move move) {
        cachedCheck = null;
        incrementOrResetFiftyMovesCounter(move);
        handleEnPassantable(move);
        saveCurrentState();
        switchTurn();
        addMoveToHistory(move);
        checkGameStatus();
        if (timer != null) {
            if (isOngoing)
                timer.switchTurn();
            else
                timer.stop();
        }
    }

    private void incrementOrResetFiftyMovesCounter(Move move) {
        if (move.isCapture() || move.isEnPassant() || getPieceAt(move.getDestination()) instanceof Pawn) {
            fiftyMovesCounter = 0;
        } else {
            fiftyMovesCounter++;
        }
    }

    private void handleEnPassantable(Move move) {
        if (enPassantablePawn != null) {
            enPassantablePawn.setEnPassantable(false);
            enPassantablePawn = null;
        }
        Piece movedPiece = getPieceAt(move.getDestination());
        if (movedPiece instanceof Pawn && ((Pawn) movedPiece).isEnPassantable()) {
            enPassantablePawn = (Pawn) movedPiece;
        }
    }

    private void saveCurrentState() {
        String state = getPositionHash();
        if (previousStates.containsKey(state)) {
            previousStates.put(state, previousStates.get(state) + 1);
        } else {
            previousStates.put(state, 1);
        }
    }

    private void switchTurn() {
        turn = turn.opposite();
    }

    private void addMoveToHistory(Move move) {
        Piece movedPiece = getPieceAt(move.getDestination());
        Class<? extends Piece> movedPieceOriginalClass = move.getPromotion() != null ? Pawn.class : movedPiece.getClass();

        if (movedPiece instanceof King)
            addMoveWithThreatAndDisambiguation(move, movedPiece, false, false);
        else
            findDisambiguationAndAddMove(move, movedPieceOriginalClass, movedPiece);
    }

    private void findDisambiguationAndAddMove(Move move, Class<? extends Piece> movedPieceOriginalClass, Piece movedPiece) {
        setMockPieceInDestination(move);

        boolean disambiguationColumn = checkDisambiguationColumn(move, movedPieceOriginalClass);
        boolean disambiguationRow = checkDisambiguationRow(move, movedPieceOriginalClass);

        setPieceAt(move.getDestination(), movedPiece);

        addMoveWithThreatAndDisambiguation(move, movedPiece, disambiguationColumn, disambiguationRow);
    }

    private void setMockPieceInDestination(Move move) {
        if (move.isCapture()) {
            setPieceAt(move.getDestination(), new Queen(turn, move.getDestination()));
        } else {
            setPieceAt(move.getDestination(), new EmptyPiece(move.getDestination()));
        }
    }

    private boolean checkDisambiguationColumn(Move move, Class<? extends Piece> movedPieceOriginalClass) {
        for (Piece piece : getPieces(turn.opposite())) {
            if (movedPieceOriginalClass.equals(piece.getClass()) && piece.couldReach(this, move.getDestination())) {
                if (move.getOrigin().column() != piece.getPosition().column()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDisambiguationRow(Move move, Class<? extends Piece> movedPieceOriginalClass) {
        for (Piece piece : getPieces(turn.opposite())) {
            if (movedPieceOriginalClass.equals(piece.getClass()) && piece.couldReach(this, move.getDestination())) {
                if (move.getOrigin().column() == piece.getPosition().column()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addMoveWithThreatAndDisambiguation(Move move, Piece movedPiece, boolean disambiguationColumn, boolean disambiguationRow) {
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

    private void checkGameStatus() {
        if (isCheckmate()) {
            endGameWithOutcome(GameOutcome.win(turn.opposite()).withCause(GameoverCause.CHECKMATE));
        }
        else {
            checkDrawConditions();
        }
    }

    private boolean isCheckmate() {
        return isCheck() && currentPlayerCantMove();
    }

    private void checkDrawConditions() {
        if(gameRepeatedThreeTimes()) {
            endGameWithOutcome(GameOutcome.draw().withCause(GameoverCause.THREEFOLD_REPETITION));
        }
        else if(isFiftyMovesRuleBroken()) {
            endGameWithOutcome(GameOutcome.draw().withCause(GameoverCause.FIFTY_MOVES_RULE));
        }
        else if(isStalemate()) {
            endGameWithOutcome(GameOutcome.draw().withCause(GameoverCause.STALEMATE));
        }
        else if(isMaterialInsufficient()) {
            endGameWithOutcome(GameOutcome.draw().withCause(GameoverCause.INSUFFICIENT_MATERIAL));
        }
    }

    private boolean gameRepeatedThreeTimes() {
        return previousStates.get(getPositionHash()) >= 3;
    }

    private boolean isFiftyMovesRuleBroken() {
        return fiftyMovesCounter >= 100; //100 moves = 50 turns
    }

    private boolean isStalemate() {
        return !isCheck() && currentPlayerCantMove();
    }

    private boolean currentPlayerCantMove() {
        for (Piece piece : getCurrentPlayerPieces()) {
            if (!piece.getPossibleMoves(this).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Set<Piece> getCurrentPlayerPieces() {
        return getPieces(turn);
    }

    private void endGameWithOutcome(GameOutcome outcome) {
        isOngoing = false;
        movesHistory.setOutcome( outcome );
    }

    private boolean isMaterialInsufficient() {
        return isMaterialInsufficient(PieceColor.WHITE) && isMaterialInsufficient(PieceColor.BLACK);
    }

    private boolean isMaterialInsufficient(PieceColor color) {
        Set<Piece> pieces = getPieces(color);
        return pieces.size() < 3 && onlyKingAndOneMinorPieceRemaining(pieces);
    }

    private boolean onlyKingAndOneMinorPieceRemaining(Set<Piece> pieces) {
        for (Piece piece : pieces) {
            if (!(piece instanceof Bishop || piece instanceof Knight || piece instanceof King)) {
                return false;
            }
        }
        return true;
    }



    public boolean isNotCurrentPlayer(PieceColor color) {
        return turn != color;
    }

    public PieceColor getCurrentPlayer() {
        return turn;
    }

    public boolean isCheck() {
        if (cachedCheck == null) {
            cachedCheck = isCheckInternal();
        }
        return cachedCheck;
    }

    private boolean isCheckInternal() {
        for (Piece piece : getPieces(turn.opposite())) {
            if (pieceCouldCaptureKing(piece))
                return true;
        }
        return false;
    }

    private boolean pieceCouldCaptureKing(Piece piece) {
        Position kingPosition = getCurrentPlayerKingPosition();

        if (piece instanceof King) //using manual king to avoid infinite loop call to isCheck
            return kingCouldCaptureKing((King) piece, kingPosition);
        else
            return piece.couldReach(this, kingPosition);
    }

    private Position getCurrentPlayerKingPosition() {
        return turn == PieceColor.WHITE ? whiteKingPosition : blackKingPosition;
    }

    private boolean kingCouldCaptureKing(King piece, Position kingPosition) {
        return Math.abs(piece.getPosition().row() - kingPosition.row()) <= 1 && Math.abs(piece.getPosition().column() - kingPosition.column()) <= 1;
    }


    private Set<Piece> getPieces(PieceColor color) {
        return color == PieceColor.WHITE ? whitePieces : blackPieces;
    }

    public boolean isLegalMove(Move move) throws BoardValidationException {
        BoardValidator.validateMove(move);
        if (!isOngoing || getPieceAt(move.getOrigin()).getColor() != turn) {
            return false;
        }
        return !wouldAllowCheck(move);
    }

    //TODO rewrite without copying the whole board
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
        return copy.isCheck();
    }

    public Piece getPieceAt(Position position) throws BoardValidationException{
        BoardValidator.validatePosition(position);
        return board[position.row()][position.column()];
    }

    private void setPieceAt(Position position, Piece piece) {
        board[position.row()][position.column()] = piece;
    }


    /**
     * Handle ChessTimer callback for time over
     * @param color
     * @return true if the game ends thanks to this call, false if the game already ended before the time over
     */
    public boolean handleTimeOver(PieceColor color) { //TODO handle with semaphore specifically
        if (!isOngoing)
            return false;

        if (isMaterialInsufficient(color.opposite()))
            endGameWithOutcome(GameOutcome.draw().withCause(GameoverCause.INSUFFICIENT_MATERIAL));
        else
            endGameWithOutcome(GameOutcome.win(color.opposite()).withCause(GameoverCause.TIME_EXPIRED));
        return true;
    }

    public void resign(PieceColor color) {
        if (!isOngoing)
            return;
        endGameWithOutcome(GameOutcome.win(color.opposite()).withCause(GameoverCause.RESIGNATION));
    }

    /**
     * @param color the color of the player requesting the draw
     * @return true if the draw is accepted with this call
     */
    public boolean requestDraw(PieceColor color) {
        if (!isOngoing)
            return false;

        drawAgreement.agree(color);
        if (drawAgreement.isAccepted()) {
            endGameWithOutcome(GameOutcome.draw().withCause(GameoverCause.AGREED_DRAW));
            return true;
        }
        return false;
    }

    public void denyDraw() {
        if (!isOngoing)
            return;

        drawAgreement.deny();
    }

    public TimerInfo getTimerInfo() {
        if (timer == null) {
            return null;
        }
        return timer.getTimerInfo();
    }

    public boolean areNoMovesPlayed() {
        return movesHistory.isEmpty();
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
}