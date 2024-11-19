package it.luciorizzi.scacchi.model.movement;

import it.luciorizzi.scacchi.model.piece.Piece;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MoveHistory {
    private final List<MoveHistoryEntry> history = new ArrayList<>();

    public void add(Move move, Piece movedPiece, ThreatType threatType) {
        history.add(new MoveHistoryEntry(move, movedPiece, threatType));
    }

    public void add(Move move, Piece movedPiece) {
        history.add(new MoveHistoryEntry(move, movedPiece, null));
    }

    public void addCheck(Move move, Piece movedPiece) {
        history.add(new MoveHistoryEntry(move, movedPiece, ThreatType.CHECK));
    }

    public void addCheckmate(Move move, Piece movedPiece) {
        history.add(new MoveHistoryEntry(move, movedPiece, ThreatType.CHECKMATE));
    }

    public void clear() {
        history.clear();
    }

    public void print() {
        for (int i = 0; i < history.size(); i++) {
            if (i % 2 == 0) {
                System.out.print("\n" + ((i / 2) + 1) + ". ");
            }
            System.out.print(history.get(i).getNotation() + " ");
        }
    }
}
