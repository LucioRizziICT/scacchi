package it.luciorizzi.scacchi.service;

import it.luciorizzi.scacchi.model.GameBoard;
import it.luciorizzi.scacchi.model.Move;
import it.luciorizzi.scacchi.model.MoveSet;
import it.luciorizzi.scacchi.model.Position;
import org.springframework.stereotype.Service;

@Service
public class GameBoardService {

    GameBoard gameBoard = new GameBoard();

    public void printGameBoard() {
        System.out.println("Printing game board...");

        new GameBoard().print();

        System.out.println("Game board printed");
    }

    public MoveSet possibleMoves(Position position) {
        return gameBoard.getPiece(position).getPossibleMoves(gameBoard);

    }

    public boolean move(Position from, Position to) {
        return gameBoard.movePiece(from, to);
    }
}