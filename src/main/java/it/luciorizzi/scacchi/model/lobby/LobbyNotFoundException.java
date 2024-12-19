package it.luciorizzi.scacchi.model.lobby;

public class LobbyNotFoundException extends RuntimeException {
    public LobbyNotFoundException(String message) {
        super(message);
    }
}
