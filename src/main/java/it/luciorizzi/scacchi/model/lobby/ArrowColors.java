package it.luciorizzi.scacchi.model.lobby;

public record ArrowColors(String color1, String color2, String color3, String color4) {
    private static final ArrowColors DEFAULT_COLORS = new ArrowColors("#FF5F26", "#1EA5E4", "#1EE425", "#7E1EE4");

    public static ArrowColors getDefault() {
        return DEFAULT_COLORS;
    }
}
