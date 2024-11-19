package it.luciorizzi.scacchi.model.type;

public enum GameStatus {
    ONGOING, WHITE_WIN, BLACK_WIN, DRAW;

    public boolean isEndStatus() {
        return this != ONGOING;
    }
}
