package it.luciorizzi.scacchi.model.lobby;

import it.luciorizzi.scacchi.model.GameBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@AllArgsConstructor
public class Lobby {
    private final GameBoard gameBoard = new GameBoard();
    @Setter
    private String name;
    @Setter
    private String password;
    @Setter
    private Player playerOne;
    @Setter
    private Player playerTwo = null;
    @Setter
    LobbyProperties properties;

    public Lobby(String name, Player playerOne, String password, LobbyProperties properties) {
        this.name = ( name == null || name.isEmpty() ) ? "Lobby di " + playerOne.getName() : name;
        this.playerOne = playerOne;
        this.password = password;
        this.properties = properties;
    }

    public boolean isFull() {
        return playerOne != null && playerTwo != null;
    }
}
