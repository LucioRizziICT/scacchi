package it.luciorizzi.scacchi.model.lobby;

public record LobbyPreferences(Boolean showPossibleMoves, Boolean showLastMove, Boolean showCheck, Boolean setWhiteAlwaysBottom, HighlightColors highlightColors, ArrowColors arrowColors) {
    private static final LobbyPreferences DEFAULT_PREFERENCES = new LobbyPreferences(true, true, true, false, HighlightColors.getDefault(), ArrowColors.getDefault());

    public static LobbyPreferences getDefault() {
        return DEFAULT_PREFERENCES;
    }
}
