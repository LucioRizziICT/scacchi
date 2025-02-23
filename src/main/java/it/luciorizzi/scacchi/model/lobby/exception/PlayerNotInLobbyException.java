package it.luciorizzi.scacchi.model.lobby.exception;

public class PlayerNotInLobbyException extends RuntimeException {
    public PlayerNotInLobbyException(String message) {
        super(message);
    }
}
