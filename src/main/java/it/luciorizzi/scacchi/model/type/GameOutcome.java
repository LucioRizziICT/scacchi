package it.luciorizzi.scacchi.model.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GameOutcome {
    private GameStatus status;
    private GameoverCause cause;

    public GameOutcome withWinner(PieceColor winner) {
        if (winner == PieceColor.WHITE) {
            this.status = GameStatus.WHITE_WIN;
        } else {
            this.status = GameStatus.BLACK_WIN;
        }
        return this;
    }

    public static GameOutcome draw() {
        return new GameOutcome().withDraw();
    }

    public GameOutcome withDraw() {
        this.status = GameStatus.DRAW;
        return this;
    }

    public static GameOutcome win(PieceColor winner) {
        return new GameOutcome().withWinner(winner);
    }

    public GameOutcome withCause(GameoverCause cause) {
        this.cause = cause;
        return this;
    }
}
