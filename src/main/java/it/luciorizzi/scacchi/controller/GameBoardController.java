package it.luciorizzi.scacchi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.luciorizzi.scacchi.model.Position;
import it.luciorizzi.scacchi.service.GameBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameBoardController {

    private final ObjectMapper objectMapper;

    private final GameBoardService gameBoardService;

    @Autowired
    public GameBoardController(GameBoardService gameBoardService, ObjectMapper objectMapper) {
        this.gameBoardService = gameBoardService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/possibleMoves")
    public ResponseEntity<String> possibleMoves(int row, int col) throws JsonProcessingException {
        System.out.println("Getting possible moves for position (" + row + ", " + col + ")");
        return ResponseEntity.ok(objectMapper.writeValueAsString(gameBoardService.possibleMoves(new Position(row, col))));
    }

    @GetMapping("/move")
    public ResponseEntity<String> move(int fromRow, int fromCol, int toRow, int toCol) {
        System.out.println("Moving piece from (" + fromRow + ", " + fromCol + ") to (" + toRow + ", " + toCol + ")");
        gameBoardService.move(new Position(fromRow, fromCol), new Position(toRow, toCol));
        return ResponseEntity.ok("Moved piece");
    }
}