package it.luciorizzi.scacchi.model.message;

import lombok.Getter;

public record NotificationMessage(NotificationMessage.Type type, String title, String message) {

    public static NotificationMessage defaultDrawRequest() {
        return new NotificationMessage(NotificationMessage.Type.DRAW_REQUEST, "Richiesta di patta", "Il tuo avversario ha richiesto una patta");
    }

    public static NotificationMessage defaultDrawAccepted() {
        return new NotificationMessage(NotificationMessage.Type.DRAW_ACCEPTED, "Patta accettata", "Il tuo avversario ha accettato la tua richiesta di patta");
    }

    public static NotificationMessage defaultDrawDenied() {
        return new NotificationMessage(NotificationMessage.Type.DRAW_DENIED, "Patta rifiutata", "Il tuo avversario ha rifiutato la tua richiesta di patta");
    }

    public static NotificationMessage defaultRematchRequest() {
        return new NotificationMessage(NotificationMessage.Type.REMATCH_REQUEST, "Richiesta di rivincita", "Il tuo avversario ha richiesto una rivincita");
    }

    public static NotificationMessage defaultRematchAccepted() {
        return new NotificationMessage(NotificationMessage.Type.REMATCH_ACCEPTED, "Rivincita accettata", "Il tuo avversario ha accettato la tua richiesta di rivincita");
    }

    public static NotificationMessage defaultRematchDenied() {
        return new NotificationMessage(NotificationMessage.Type.REMATCH_DENIED, "Rivincita rifiutata", "Il tuo avversario ha rifiutato la tua richiesta di rivincita");
    }

    @Getter
    public enum Type {
        DRAW_REQUEST("DRAW_REQUEST"),
        DRAW_ACCEPTED("DRAW_ACCEPTED"),
        DRAW_DENIED("DRAW_DENIED"),
        REMATCH_REQUEST("REMATCH_REQUEST"),
        REMATCH_ACCEPTED("REMATCH_ACCEPTED"),
        REMATCH_DENIED("REMATCH_DENIED"),
        GENERIC("GENERIC");

        private final String value;

        Type(String value) {
            this.value = value;
        }
    }
}