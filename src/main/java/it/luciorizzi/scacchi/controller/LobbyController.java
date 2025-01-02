package it.luciorizzi.scacchi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.luciorizzi.scacchi.model.ChessTimer;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyActionException;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyNotFoundException;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.openapi.api.LobbyApiDelegate;
import it.luciorizzi.scacchi.openapi.model.*;
import it.luciorizzi.scacchi.service.LobbyService;
import it.luciorizzi.scacchi.util.ApiDTOConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
public class LobbyController implements LobbyApiDelegate {

    private final LobbyService lobbyService;

    @Autowired
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    public ResponseEntity<LobbyDTO> createLobby(ColorEnum playerOneColor, LobbyDTO lobbyDTO) {
        return ResponseEntity.ok( lobbyService.createLobby(lobbyDTO) );
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

    @GetMapping("/lobby/{lobbyId}/getMovesHistory")
    public ResponseEntity<List<String>> getMovesHistory(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId) { //TODO aggiungere alla definizione openapi
        return ResponseEntity.ok(lobbyService.getMovesHistory(token, lobbyId));
    }

    @GetMapping("/lobby/{lobbyId}")
    public ModelAndView getLobbyView(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId) throws JsonProcessingException {
        return lobbyService.getLobbyView(token, lobbyId);
    }


    public ResponseEntity<LobbyDTO> joinLobby(String lobbyId, LobbyJoinRequestDTO lobbyJoinRequestDTO) {
        return ResponseEntity.ok( lobbyService.joinLobby(lobbyId, lobbyJoinRequestDTO) );
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
        System.out.println("Starting timer at " + System.nanoTime());
        timer.switchTurn();
    }


    //TODO add complete errors handling and not found pages

    @ExceptionHandler(LobbyNotFoundException.class)
    public ModelAndView handleLobbyNotFound() {
        return new ModelAndView("lobbyNotFound");
    }

    @ExceptionHandler(LobbyActionException.class)
    public ResponseEntity<String> handleLobbyActionException(HttpServletRequest reqest, Exception exception) {
        HttpStatus status = ((LobbyActionException) exception).getStatus();
        return ResponseEntity.status(status).body(exception.getMessage()); //TODO change with DTO, not only string
    }
}

//TODO Add Logging
