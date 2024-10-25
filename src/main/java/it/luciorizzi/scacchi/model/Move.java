package it.luciorizzi.scacchi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Move {
    private final Position origin;
    private final Position destination;
    private final boolean isCapture;
}
