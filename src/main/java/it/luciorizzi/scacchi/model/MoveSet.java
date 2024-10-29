package it.luciorizzi.scacchi.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class MoveSet {
    private final Set<Move> moves = new HashSet<>();

    public void addMovement(Position origin, int row, int column) {
        moves.add(new Move(origin, new Position(row, column), false));
    }

    public void addMovement(Position origin, Position destination) {
        moves.add(new Move(origin, destination, false));
    }

    public void addCapture(Position origin, int row, int column) {
        moves.add(new Move(origin, new Position(row, column), true));
    }

    public void addCapture(Position origin, Position destination) {
        moves.add(new Move(origin, destination, true));
    }

    public boolean canReach(Position position) {
        return moves.stream().anyMatch(move -> move.destination().equals(position));
    }
}
