package it.luciorizzi.scacchi.model;

import lombok.Getter;

@Getter
public class GameBoard {
    private final static int ROWS = 8;
    private final static int COLUMNS = 8;
    private final Piece[][] board;

    public GameBoard() {
        board = new Piece[ROWS][COLUMNS];
    }

    public void initialize() {

    }
}
