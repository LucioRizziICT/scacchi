package it.luciorizzi.scacchi.model.movement;

import it.luciorizzi.scacchi.model.piece.Piece;
import it.luciorizzi.scacchi.model.type.GameOutcome;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MoveHistory {
    private final List<MoveHistoryEntry> history = new ArrayList<>();
    @Setter
    private GameOutcome outcome = null;

    public void add(Move move, Piece movedPiece, boolean disambiguationColumn, boolean disambiguationRow) {
        history.add(new MoveHistoryEntry(move, movedPiece, null, disambiguationColumn, disambiguationRow));
    }

    public void addCheck(Move move, Piece movedPiece, boolean disambiguationColumn, boolean disambiguationRow) {
        history.add(new MoveHistoryEntry(move, movedPiece, ThreatType.CHECK, disambiguationColumn, disambiguationRow));
    }

    public void addCheckmate(Move move, Piece movedPiece, boolean disambiguationColumn, boolean disambiguationRow) {
        history.add(new MoveHistoryEntry(move, movedPiece, ThreatType.CHECKMATE, disambiguationColumn, disambiguationRow));
    }

    public void clear() {
        history.clear();
        outcome = null;
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

    public boolean isEmpty() {
        return history.isEmpty();
    }
}
