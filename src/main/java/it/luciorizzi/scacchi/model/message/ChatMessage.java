package it.luciorizzi.scacchi.model.message;

import java.time.LocalDateTime;

public record ChatMessage(String playerName, String message, LocalDateTime dateTime) {
}
