package it.luciorizzi.scacchi.model.type;

public enum GameoverCause {
    CHECKMATE,
    STALEMATE,
    THREEFOLD_REPETITION,
    FIFTY_MOVES_RULE,
    INSUFFICIENT_MATERIAL,
    TIME_VS_INSUFFICIENT_MATERIAL,
    RESIGNATION,
    AGREED_DRAW,
    TIME_EXPIRED,
    DISCONNECTION //TODO: implement
}
