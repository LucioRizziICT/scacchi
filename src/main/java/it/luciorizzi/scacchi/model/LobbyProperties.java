package it.luciorizzi.scacchi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LobbyProperties {
    private boolean isPrivate;
    private boolean isRated;
    private boolean isTimed;
    private int time; // in seconds
    private int increment; // in seconds
    private boolean allowsSpectators;

    public boolean allowsSpectators() {
        return allowsSpectators;
    }

    public static LobbyProperties defaultProperties() {
        return new LobbyProperties(false, false, false, 0, 0, true);
    }

}
