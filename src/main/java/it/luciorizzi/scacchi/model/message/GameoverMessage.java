package it.luciorizzi.scacchi.model.message;

import it.luciorizzi.scacchi.model.type.GameOutcome;

public record GameoverMessage(GameOutcome outcome) {
}
