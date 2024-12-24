package it.luciorizzi.scacchi.model.lobby.exception;

public class LobbyNotFoundException extends RuntimeException {
    public LobbyNotFoundException(String message) {
        super(message);
    }
}
