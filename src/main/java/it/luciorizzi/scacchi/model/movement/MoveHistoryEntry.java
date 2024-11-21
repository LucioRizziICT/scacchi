package it.luciorizzi.scacchi.model.movement;

import it.luciorizzi.scacchi.model.piece.Piece;

public record MoveHistoryEntry(Move move, Piece movedPiece, ThreatType threatType) {
    public String getNotation() {
        StringBuilder sb = new StringBuilder();
        if (move.getMoveType() == MoveType.CASTLING) {
            return move.getDestination().column() == 2 ? "O-O-O" : "O-O";
        }
        sb.append(movedPiece.getSymbol() == 'P' ? "" : movedPiece.getSymbol());
        sb.append(move.isCapture() ? "x" : "");
        sb.append((char) (move.getDestination().column()+97));
        sb.append(move.getDestination().row()+1);
        sb.append(move.getPromotion() != null ? "=" + move.getPromotion() : "");
        sb.append(threatType == null ? "" : threatType == ThreatType.CHECK ? "+" : "#"); //TODO: buggato
        return sb.toString();
    }
}
