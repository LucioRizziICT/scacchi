package it.luciorizzi.scacchi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.GameStatus;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/lobby/createNewGame")
    public ResponseEntity<Map<String, Object>> createNewGame(String lobbyName, String playerName, String password, PieceColor color, String lobbyType) {
        return ResponseEntity.ok(lobbyService.createNewGame(lobbyName, playerName, password, color, lobbyType));
    }

    @GetMapping("/lobby/getLobbies")
    public ResponseEntity<List<Map<String, Object>>> getLobbies() {
        return ResponseEntity.ok(lobbyService.getPublicLobbies());
    }

    @GetMapping("/lobby/{lobbyId}/possibleMoves")
    public ResponseEntity<MoveSet> possibleMoves(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId, int row, int col) throws JsonProcessingException {
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
    public ModelAndView getLobbyInfo(@CookieValue(value = "playerToken", required = false) String token, @PathVariable("lobbyId") String lobbyId) throws JsonProcessingException {
        return lobbyService.getLobbyView(token, lobbyId);
    }

    @PostMapping("/lobby/{lobbyId}/join")
    public ResponseEntity<String> joinLobby(@PathVariable("lobbyId") String lobbyId, String playerName, String password) {
        return ResponseEntity.ok(lobbyService.joinLobby(lobbyId, playerName, password));
    }
}
