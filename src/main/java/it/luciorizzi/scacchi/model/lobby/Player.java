package it.luciorizzi.scacchi.model.lobby;

import it.luciorizzi.scacchi.model.type.PieceColor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Player {
    public static final String DEFAULT_NAME = "Anon";
    private final UUID id = UUID.randomUUID();
    @Setter
    private String name;
    private final String gameId;
    private final PieceColor color;

    public Player(String name, String gameId, PieceColor color) {
        this.name = ( name == null || name.isBlank() ) ? DEFAULT_NAME : name;
        this.gameId = gameId;
        this.color = color;
    }

    public Player(String gameId, PieceColor color) {
        this.gameId = gameId;
        this.color = color == null ? getRandomColor() : color;
    }

    private PieceColor getRandomColor() {
        return Math.random() < 0.5 ? PieceColor.WHITE : PieceColor.BLACK;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Player player = (Player) obj;
        return this.id.equals(player.id);
    }
}
