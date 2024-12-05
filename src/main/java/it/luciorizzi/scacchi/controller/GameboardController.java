package it.luciorizzi.scacchi.controller;

import it.luciorizzi.scacchi.model.message.MessageWrapper;
import it.luciorizzi.scacchi.model.message.MoveMessage;
import it.luciorizzi.scacchi.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameboardController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private LobbyService lobbyService;

    @MessageMapping("/lobby/{lobbyId}/move")
    public void provola(@DestinationVariable String lobbyId, MessageWrapper<MoveMessage> messageWrapper) {
        MoveMessage moveMessage = messageWrapper.message();
        if (lobbyService.move(messageWrapper.playerToken(), lobbyId, moveMessage.fromRow(), moveMessage.fromCol(), moveMessage.toRow(), moveMessage.toCol(), moveMessage.promotion())) {
            Boolean isCheck = lobbyService.isCheck(messageWrapper.playerToken(), lobbyId);
            MoveMessage response = new MoveMessage(moveMessage.fromRow(), moveMessage.fromCol(), moveMessage.toRow(), moveMessage.toCol(), moveMessage.promotion(), isCheck);
            simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, response);
        }
    }
}
