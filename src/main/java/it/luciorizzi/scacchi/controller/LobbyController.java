package it.luciorizzi.scacchi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.luciorizzi.scacchi.model.ChessTimer;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyActionException;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyNotFoundException;
import it.luciorizzi.scacchi.openapi.api.LobbyApiDelegate;
import it.luciorizzi.scacchi.openapi.model.*;
import it.luciorizzi.scacchi.service.LobbyService;
import it.luciorizzi.scacchi.util.ApiDTOConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class LobbyController implements LobbyApiDelegate {

    private final LobbyService lobbyService;

    @Autowired
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    Logger logger = LoggerFactory.getLogger(LobbyController.class);

    public ResponseEntity<LobbyDTO> createLobby(ColorEnum playerOneColor, LobbyDTO lobbyDTO) {
        logger.debug("Creating new lobby");
        return ResponseEntity.ok( lobbyService.createLobby(lobbyDTO, playerOneColor) );
    }

    public ResponseEntity<List<LobbyDTO>> getPublicLobbies() {
        return ResponseEntity.ok(lobbyService.getPublicLobbies());
    }

    public ResponseEntity<List<MoveDTO>> getPossibleMoves(String lobbyId, String playerToken, PositionDTO position) {
        return ResponseEntity.ok(
                ApiDTOConverter.toListOfMoves(
                        lobbyService.getPossibleMoves(playerToken, lobbyId, ApiDTOConverter.toPosition(position))));
    }

    public ResponseEntity<GameStatusDTO> getGameStatus(String lobbyId, String playerToken) {
        if (lobbyService.gameStarted(lobbyId)) {
            return ResponseEntity.ok(GameStatusDTO.NOT_STARTED);
        } else if (lobbyService.gameEnded(playerToken, lobbyId)) {
            return ResponseEntity.ok(GameStatusDTO.ENDED);
        } else {
            return ResponseEntity.ok(GameStatusDTO.ONGOING);

        }
    }

    public ResponseEntity<LobbyDTO> joinLobby(String lobbyId, LobbyJoinRequestDTO lobbyJoinRequestDTO) {
        return ResponseEntity.ok( lobbyService.joinLobby(lobbyId, lobbyJoinRequestDTO) );
    }

    //TODO add openapi definition
    @GetMapping("/lobby/{lobbyId}/movesHistory")
    public ResponseEntity<List<String>> getMovesHistory(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId) { //TODO aggiungere alla definizione openapi
        return ResponseEntity.ok(lobbyService.getMovesHistory(token, lobbyId));
    }

    @GetMapping("/lobby/{lobbyId}")
    public ModelAndView getLobbyView(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId) throws JsonProcessingException {
        return lobbyService.getLobbyView(token, lobbyId);
    }

    @GetMapping("/lobby/testLobbyFull")
    public ModelAndView testLobbyFull() {
        ModelAndView modelAndView = new ModelAndView("lobbyFull");
        modelAndView.addObject("lobbyName", "testicolo");
        modelAndView.addObject("lobbyAllowsSpectators", true);
        return modelAndView;
    }

    @GetMapping("/lobby/testTimer")
    public void testTimer() {
        ChessTimer timer = new ChessTimer(180, 0, null);
        timer.switchTurn();
    }

    //TODO add complete errors handling and not found pages
}

//TODO Add Logging
