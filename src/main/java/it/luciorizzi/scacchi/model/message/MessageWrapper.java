package it.luciorizzi.scacchi.model.message;

public record MessageWrapper<T>(String playerToken, T message) {
}
