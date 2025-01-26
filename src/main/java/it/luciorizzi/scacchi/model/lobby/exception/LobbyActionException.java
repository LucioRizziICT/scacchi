package it.luciorizzi.scacchi.model.lobby.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class LobbyActionException extends RuntimeException {
    public LobbyActionException(String message) {
        super(message);
    }
}
