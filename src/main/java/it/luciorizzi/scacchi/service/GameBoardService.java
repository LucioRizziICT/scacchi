package it.luciorizzi.scacchi.service;

import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.movement.Position;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GameBoardService {

    GameBoard gameBoard = new GameBoard();

    public void printGameBoard() {
        System.out.println("Printing game board...");

        gameBoard.print();

        System.out.println("Game board printed");
    }

    public MoveSet possibleMoves(Position position) {
        return gameBoard.getPiece(position).getPossibleMoves(gameBoard);

    }

    public boolean move(Position from, Position to) {
        return gameBoard.movePiece(from, to);
    }

    public Map<String, Object> isCheck() {
        if (gameBoard.isCheck()) {
            return Map.of("isCheck", true, "isCheckmate", gameBoard.isCheckmate(), "king", gameBoard.getCurrentPlayerKingPosition());
        }
        return Map.of("isCheck", false);
    }
}
