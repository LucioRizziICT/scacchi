package it.luciorizzi.scacchi.model.lobby;

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
    private long time; // in seconds
    private long increment; // in seconds
    private boolean allowsSpectators;

    public boolean allowsSpectators() {
        return allowsSpectators;
    }

    public static LobbyProperties defaultProperties() {
        return new LobbyProperties(false, false, false, 0, 0, true);
    }

}
