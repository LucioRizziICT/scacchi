package it.luciorizzi.scacchi.model.lobby;

import it.luciorizzi.scacchi.model.Agreement;
import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.type.PieceColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Lobby {
    private final GameBoard gameBoard;
    private final String id;
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
    private Agreement rematchAgreement = new Agreement();

    public Lobby(String id, String name, Player playerOne, String password, LobbyProperties properties) {
        this.name = ( name == null || name.isBlank() ) ? "Lobby di " + playerOne.getName() : name;
        this.playerOne = playerOne;
        this.password = password == null || password.isEmpty() ? null : password;
        this.properties = properties;
        this.id = id;
        this.gameBoard = new GameBoard(10, 2, id);
    }

    public boolean isFull() {
        return playerOne != null && playerTwo != null;
    }

    public boolean gameStarted() {
        return isFull();
    }

    /**
     * @param color the color of the player requesting the rematch
     * @return true if the rematch is accepted with this call
     */
    public boolean requestRematch(PieceColor color) {
        rematchAgreement.agree(color);
        if (rematchAgreement.isAccepted()) {
            rematchAgreement = new Agreement();
            playerOne.setColor(playerOne.getColor().opposite());
            playerTwo.setColor(playerTwo.getColor().opposite());
            gameBoard.reset();
            return true;
        }
        return false;
    }
}
