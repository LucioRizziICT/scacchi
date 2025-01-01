package it.luciorizzi.scacchi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.luciorizzi.scacchi.model.ChessTimer;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyActionException;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyNotFoundException;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.service.LobbyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;

    @Autowired
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/lobby")
    public ResponseEntity<Map<String, Object>> createLobby(String lobbyName, String playerName, String password, PieceColor color, String lobbyType) {
        return ResponseEntity.ok(lobbyService.createLobby(lobbyName, playerName, password, color, lobbyType));
    }

    @GetMapping("/lobby")
    public ResponseEntity<List<Map<String, Object>>> getLobbies() {
        return ResponseEntity.ok(lobbyService.getPublicLobbies());
    }

    @GetMapping("/lobby/{lobbyId}/possibleMoves")
    public ResponseEntity<MoveSet> getPossibleMoves(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId, int row, int col) {
        return ResponseEntity.ok(lobbyService.getPossibleMoves(token, lobbyId, row, col));
    }

    @GetMapping("/lobby/{lobbyId}/gameStatus")
    public ResponseEntity<Boolean> gameStatus(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId) {
        return ResponseEntity.ok(lobbyService.gameEnded(token, lobbyId));
    }

    @GetMapping("/lobby/{lobbyId}/getMovesHistory")
    public ResponseEntity<List<String>> getMovesHistory(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId) {
        return ResponseEntity.ok(lobbyService.getMovesHistory(token, lobbyId));
    }

    @GetMapping("/lobby/{lobbyId}")
    public ModelAndView getLobbyView(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId) throws JsonProcessingException {
        return lobbyService.getLobbyView(token, lobbyId);
    }

    @PostMapping("/lobby/{lobbyId}/join")
    public ResponseEntity<String> joinLobby(@PathVariable("lobbyId") String lobbyId, String playerName, String password) {
        return ResponseEntity.ok(lobbyService.joinLobby(lobbyId, playerName, password));
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
