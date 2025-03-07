package it.luciorizzi.scacchi.model.movement;

import it.luciorizzi.scacchi.model.piece.Piece;

public record MoveHistoryEntry(Move move, Piece movedPiece, ThreatType threatType, boolean disambiguationColumn, boolean disambiguationRow) {

    MoveHistoryEntry(Move move, Piece movedPiece, ThreatType threatType) {
        this(move, movedPiece, threatType, false, false);
    }

    public String getNotation() {
        StringBuilder sb = new StringBuilder();
        if (move.getMoveType() == MoveType.CASTLING) {
            return move.getDestination().column() == 2 ? "O-O-O" : "O-O";
        }
        sb.append(movedPiece.getSymbol() == 'P' || move.getPromotion() != null ? "" : movedPiece.getSymbol());
        if (disambiguationColumn || (move.isCapture() && (movedPiece.getSymbol() == 'P' || move.getPromotion() != null)) ) {
            sb.append((char) (move.getOrigin().column()+97));
        }
        if (disambiguationRow) {
            sb.append(move.getOrigin().row()+1);
        }
        sb.append(move.isCapture() ? "x" : "");
        sb.append((char) (move.getDestination().column()+97));
        sb.append(move.getDestination().row()+1);
        sb.append(move.getPromotion() != null ? "=" + Character.toUpperCase(move.getPromotion()) : "");
        sb.append(threatType == null ? "" : threatType == ThreatType.CHECK ? "+" : "#");
        return sb.toString();
    }
}
