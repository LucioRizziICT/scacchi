package it.luciorizzi.scacchi.controller;

import it.luciorizzi.scacchi.model.lobby.exception.LobbyActionException;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyNotFoundException;
import it.luciorizzi.scacchi.model.lobby.exception.PlayerNotFoundException;
import it.luciorizzi.scacchi.model.lobby.exception.PlayerNotInLobbyException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class LobbyControllerExceprionHandler {

    Logger logger = LoggerFactory.getLogger(LobbyControllerExceprionHandler.class);

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(LobbyActionException.class)
    public void handleLobbyActionException(HttpServletRequest request, Exception exception) {
        logger.warn("Lobby action exception. {}", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(PlayerNotInLobbyException.class)
    public void handlePlayerNotInLobbyException(HttpServletRequest request, Exception exception) {
        logger.warn("Player not in lobby exception. {}", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PlayerNotFoundException.class)
    public void handlePlayerNotFoundException(HttpServletRequest request, Exception exception) {
        logger.warn("Player not found. {}", exception.getMessage());
    }

    @ExceptionHandler(LobbyNotFoundException.class)
    public ModelAndView handleLobbyNotFound() {
        return new ModelAndView("lobbyNotFound");
    }
}
