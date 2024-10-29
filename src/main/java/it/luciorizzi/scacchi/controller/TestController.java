package it.luciorizzi.scacchi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.luciorizzi.scacchi.service.GameBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class TestController {

    private final GameBoardService gameBoardService;

    @Autowired
    public TestController(GameBoardService gameBoardService) {
        this.gameBoardService = gameBoardService;
    }

    @GetMapping("/printGameBoard")
    public ResponseEntity<String> printGameBoard() {
        gameBoardService.printGameBoard();
        return ResponseEntity.ok("Game board printed successfully");
    }
}
