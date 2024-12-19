package it.luciorizzi.scacchi.model.movement;

import it.luciorizzi.scacchi.model.piece.Bishop;
import it.luciorizzi.scacchi.model.type.PieceColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveHistoryEntryTest {

    @Test
    void getNotation() {
        Move move = Move.movement(new Position(0, 0), new Position(1, 1));
        Bishop bishop = new Bishop(PieceColor.WHITE,new Position(0, 0));
        MoveHistoryEntry moveHistoryEntry = new MoveHistoryEntry(move, bishop, null);
        MoveHistoryEntry moveHistoryEntryCheck = new MoveHistoryEntry(move, bishop, ThreatType.CHECK);
        MoveHistoryEntry moveHistoryEntryCheckmate = new MoveHistoryEntry(move, bishop, ThreatType.CHECKMATE);
        assertEquals("Bb2", moveHistoryEntry.getNotation());
        assertEquals("Bb2+", moveHistoryEntryCheck.getNotation());
        assertEquals("Bb2#", moveHistoryEntryCheckmate.getNotation());
    }
}