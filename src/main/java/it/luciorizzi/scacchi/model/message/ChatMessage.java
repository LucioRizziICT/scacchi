package it.luciorizzi.scacchi.model.message;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessage(UUID playerId, String playerName, String message, LocalDateTime dateTime) {
}
