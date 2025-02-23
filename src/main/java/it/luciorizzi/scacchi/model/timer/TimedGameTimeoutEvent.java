package it.luciorizzi.scacchi.model.timer;

import org.springframework.context.ApplicationEvent;

public class TimedGameTimeoutEvent extends ApplicationEvent {

    public TimedGameTimeoutEvent(Object source) {
        super(source);
    }
}
