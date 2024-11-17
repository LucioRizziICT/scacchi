package it.luciorizzi.scacchi.model.piece;

import it.luciorizzi.scacchi.model.movement.Position;

public class EmptyPiece extends Piece {
    public EmptyPiece(Position position) {
        super(null, position, ' ');
    }
}
