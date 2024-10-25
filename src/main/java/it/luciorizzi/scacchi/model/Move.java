package it.luciorizzi.scacchi.model;

public record Move(Position origin, Position destination, boolean isCapture) {
}
