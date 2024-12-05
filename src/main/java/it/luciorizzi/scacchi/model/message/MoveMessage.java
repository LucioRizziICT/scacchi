package it.luciorizzi.scacchi.model.message;

import lombok.Setter;

public record MoveMessage(int fromRow, int fromCol, int toRow, int toCol, Character promotion, Boolean isCheck) {
}
