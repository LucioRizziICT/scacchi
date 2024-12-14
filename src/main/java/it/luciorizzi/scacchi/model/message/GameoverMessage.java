package it.luciorizzi.scacchi.model.message;

import it.luciorizzi.scacchi.model.type.GameStatus;

public record GameoverMessage(GameStatus outcome, String cause) {
}
