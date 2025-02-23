package it.luciorizzi.scacchi.model.lobby.exception;

public class LobbyIsFullException extends RuntimeException {
    public LobbyIsFullException(String message) {
        super(message);
    }
}
