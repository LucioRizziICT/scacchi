package it.luciorizzi.scacchi.util;

import lombok.NonNull;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class EventPublisherHelper implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        EventPublisherHelper.publisher = applicationEventPublisher;
    }

    public static void publishEvent(ApplicationEvent event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}
