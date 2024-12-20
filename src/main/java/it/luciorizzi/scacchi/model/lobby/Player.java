package it.luciorizzi.scacchi.model.lobby;

import it.luciorizzi.scacchi.model.type.PieceColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Player {
    @Setter
    private String name = "Anon";
    private final String gameId;
    private final PieceColor color;

    public Player(String gameId, PieceColor color) {
        this.gameId = gameId;
        this.color = color == null ? getRandomColor() : color;
    }

    private PieceColor getRandomColor() {
        return Math.random() < 0.5 ? PieceColor.WHITE : PieceColor.BLACK;
    }
}
