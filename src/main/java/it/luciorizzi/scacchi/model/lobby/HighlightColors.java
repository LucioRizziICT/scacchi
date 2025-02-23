package it.luciorizzi.scacchi.model.lobby;

public record HighlightColors(String color1, String color2, String color3, String color4) {
    private static final HighlightColors DEFAULT_COLORS = new HighlightColors("#BC3030", "#1EA5E4", "#1EE425", "#7E1EE4");

    public static HighlightColors getDefault() {
        return DEFAULT_COLORS;
    }
}
