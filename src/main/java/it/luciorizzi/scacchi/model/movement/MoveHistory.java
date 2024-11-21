package it.luciorizzi.scacchi.model.movement;

import it.luciorizzi.scacchi.model.piece.Piece;
import it.luciorizzi.scacchi.model.type.GameStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MoveHistory {
    private final List<MoveHistoryEntry> history = new ArrayList<>();
    @Setter
    private GameStatus outcome = null;

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

    public List<String> getNotation() {
        List<String> result = new ArrayList<>(history.size());
        for (MoveHistoryEntry entry : history) {
            result.add(entry.getNotation());
        }
        return result;
    }

    public String getNotationString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            if (i % 2 == 0) {
                sb.append("\n").append((i / 2) + 1).append(". ");
            }
            sb.append(history.get(i).getNotation()).append(" ");
        }
        if (outcome != null) {
            sb.append("\n").append(outcome);
        }
        return sb.toString();
    }
}
