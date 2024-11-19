package it.luciorizzi.scacchi.model.movement;

import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class MoveSet {
    private final Set<Move> moves = new HashSet<>();

    public void addMovement(Position origin, int row, int column) {
        moves.add(Move.movement(origin, new Position(row, column)));
    }

    public void addMovement(Position origin, Position destination) {
        moves.add(Move.movement(origin, destination));
    }

    public void addCapture(Position origin, int row, int column) {
        moves.add(Move.capture(origin, new Position(row, column)));
    }

    public void addCapture(Position origin, Position destination) {
        moves.add(Move.capture(origin, destination));
    }

    public void addCastling(Position origin, Position destination) {
        moves.add(Move.castling(origin, destination));
    }

    public void addEnPassant(Position origin, Position destination) {
        moves.add(Move.enPassant(origin, destination));
    }

    public boolean canReach(Position position) {
        return moves.stream().anyMatch(move -> move.getDestination().equals(position));
    }

    public boolean isEmpty() {
        return moves.isEmpty();
    }

    public void remove(Move move) {
        moves.remove(move);
    }

    public void removeAll(Collection<Move> moves) {
        this.moves.removeAll(moves);
    }

    public void addMove(Move move) {
        moves.add(move);
    }
}
