package it.luciorizzi.scacchi.model;

public class BoardValidationException extends RuntimeException {
    public BoardValidationException(String message) {
        super(message);
    }
}