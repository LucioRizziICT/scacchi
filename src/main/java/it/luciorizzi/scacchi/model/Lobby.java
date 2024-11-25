package it.luciorizzi.scacchi.model;

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
    private Player playerOne = null;
    @Setter
    private Player playerTwo = null;
    @Setter
    private String password = null;
    @Setter
    private boolean isPrivate = false;

    public Lobby(String name, Player playerOne, String password, boolean isPrivate) {
        this.name = name == null || name.isEmpty() ? "Lobby di " + playerOne.getName() : name;
        this.playerOne = playerOne;
        this.password = password;
        this.isPrivate = isPrivate;
    }

    public boolean isFull() {
        return playerOne != null && playerTwo != null;
    }



}
