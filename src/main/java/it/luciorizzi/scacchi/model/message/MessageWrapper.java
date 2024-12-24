package it.luciorizzi.scacchi.model.message;

public record MessageWrapper<T>(String playerName, String playerToken, T message) {
}
