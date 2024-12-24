package it.luciorizzi.scacchi.model.message;

import java.time.Instant;

public record ChatMessage(String playerName, String message, Instant timestamp) {
}
