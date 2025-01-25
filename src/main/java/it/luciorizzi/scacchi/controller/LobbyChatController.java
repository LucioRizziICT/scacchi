package it.luciorizzi.scacchi.controller;

import it.luciorizzi.scacchi.model.lobby.Player;
import it.luciorizzi.scacchi.model.message.ChatMessage;
import it.luciorizzi.scacchi.model.message.MessageWrapper;
import it.luciorizzi.scacchi.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class LobbyChatController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private LobbyService lobbyService;

    @MessageMapping("/lobby/{lobbyId}/chat")
    public void chat(@DestinationVariable String lobbyId, MessageWrapper<ChatMessage> messageWrapper) {
        Player player = lobbyService.getPlayer(messageWrapper.playerToken());
        if (lobbyId.equals(player.getGameId())) {
            String messageContent = messageWrapper.message().message();
            if (messageContent != null && !messageContent.isBlank() && messageContent.length() <= 255) {
                ChatMessage chatMessage = new ChatMessage(player.getName(), messageContent, LocalDateTime.now());
                socketSendChat(lobbyId, chatMessage);
            }
        }
    }

    private void socketSendChat(String lobbyId, ChatMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/chat", message);
    }
}
