package it.luciorizzi.scacchi.model.movement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class Move {
    private final Position origin;
    private final Position destination;
    private final MoveType moveType;
    @Setter
    private ThreatType threatType = null;
    @Setter
    private Character promotion = null;

    public Move(Position origin, Position destination, MoveType moveType) {
        this.origin = origin;
        this.destination = destination;
        this.moveType = moveType;
    }

    public static Move movement(Position origin, Position destination) {
        return new Move(origin, destination, MoveType.MOVEMENT);
    }

    public static Move capture(Position origin, Position destination) {
        return new Move(origin, destination, MoveType.CAPTURE);
    }

    public static Move castling(Position origin, Position destination) {
        return new Move(origin, destination, MoveType.CASTLING);
    }

    public static Move enPassant(Position origin, Position destination) {
        return new Move(origin, destination, MoveType.EN_PASSANT);
    }

    public static Move promotion(Position origin, Position destination) {
        return new Move(origin, destination, MoveType.PROMOTION);
    }

    public static Move unspecified(Position origin, Position destination) {
        return new Move(origin, destination, null);
    }

    public boolean isMovement() {
        return moveType == MoveType.MOVEMENT;
    }

    public boolean isCapture() {
        return moveType == MoveType.CAPTURE;
    }

    public boolean isCastling() {
        return moveType == MoveType.CASTLING;
    }

    public boolean isEnPassant() {
        return moveType == MoveType.EN_PASSANT;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return origin.equals(move.origin) && destination.equals(move.destination) && moveType == move.moveType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destination, moveType);
    }
}
