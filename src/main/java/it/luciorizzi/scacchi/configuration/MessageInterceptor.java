package it.luciorizzi.scacchi.configuration;

import it.luciorizzi.scacchi.service.LobbyService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MessageInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new RuntimeException("Null StompHeaderAccessor");
        }
        String destination = accessor.getDestination();

        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            return message;
        }

        if ( !StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && (destination == null || destination.contains("/topic")) ) {
            return null;
        }

        return message;
    }
}