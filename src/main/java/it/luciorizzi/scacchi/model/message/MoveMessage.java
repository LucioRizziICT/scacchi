package it.luciorizzi.scacchi.model.message;

import it.luciorizzi.scacchi.model.TimerInfo;

public record MoveMessage(int fromRow, int fromCol, int toRow, int toCol, Character promotion, Boolean isCheck, TimerInfo timerInfo) {
}
