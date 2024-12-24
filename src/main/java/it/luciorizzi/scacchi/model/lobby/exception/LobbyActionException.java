package it.luciorizzi.scacchi.model.lobby.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class LobbyActionException extends RuntimeException {

    private final HttpStatus status;

    public LobbyActionException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
