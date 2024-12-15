package it.luciorizzi.scacchi.model.type;

public enum GameoverCause {
    CHECKMATE,
    STALEMATE,
    THREEFOLD_REPETITION,
    FIFTY_MOVES_RULE, //TODO: implement
    INSUFFICIENT_MATERIAL,
    RESIGNATION,
    AGREED_DRAW,
    TIME_EXPIRED,
    DISCONNECTION //TODO: implement
}
