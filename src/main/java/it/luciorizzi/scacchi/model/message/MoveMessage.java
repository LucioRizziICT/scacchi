package it.luciorizzi.scacchi.model.message;

public record MoveMessage(int fromRow, int fromCol, int toRow, int toCol, Character promotion, Boolean isCheck) {
}
