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

    public Lobby(Player playerOne) {
        this.playerOne = playerOne;
        this.name = "Lobby di " + playerOne.getName();
    }

    public Lobby(Player playerOne, String name) {
        this.name = name;
        this.playerOne = playerOne;
    }

    public boolean isFull() {
        return playerOne != null && playerTwo != null;
    }



}
