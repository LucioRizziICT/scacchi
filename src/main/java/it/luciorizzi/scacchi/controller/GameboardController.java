package it.luciorizzi.scacchi.controller;

import it.luciorizzi.scacchi.model.message.GameoverMessage;
import it.luciorizzi.scacchi.model.message.MessageWrapper;
import it.luciorizzi.scacchi.model.message.MoveMessage;
import it.luciorizzi.scacchi.model.message.StartMessage;
import it.luciorizzi.scacchi.model.type.GameOutcome;
import it.luciorizzi.scacchi.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class GameboardController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private LobbyService lobbyService;

    @MessageMapping("/lobby/{lobbyId}/move")
    public void move(@DestinationVariable String lobbyId, MessageWrapper<MoveMessage> messageWrapper) {
        MoveMessage moveMessage = messageWrapper.message();
        if (lobbyService.move(messageWrapper.playerToken(), lobbyId, moveMessage.fromRow(), moveMessage.fromCol(), moveMessage.toRow(), moveMessage.toCol(), moveMessage.promotion())) {
            Boolean isCheck = lobbyService.isCheck(messageWrapper.playerToken(), lobbyId);
            MoveMessage response = new MoveMessage(moveMessage.fromRow(), moveMessage.fromCol(), moveMessage.toRow(), moveMessage.toCol(), moveMessage.promotion(), isCheck);
            socketSendMove(lobbyId, response);
            boolean gameEnd = lobbyService.gameEnded(messageWrapper.playerToken(), lobbyId);
            if (gameEnd) {
                GameOutcome gameOutcome = lobbyService.getGameOutcome(messageWrapper.playerToken(), lobbyId);
                socketSendOutcome(lobbyId, new GameoverMessage(gameOutcome));
            }
        }
    }

    private void socketSendMove(String lobbyId, MoveMessage response) {
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/move", response);
    }

    private void socketSendOutcome(String lobbyId, GameoverMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/gameover", message);
    }

    @SubscribeMapping("/lobby/{lobbyId}/move")
    public void newClient(@DestinationVariable String lobbyId) {
        System.out.println("New client connected to lobby " + lobbyId);
        sendSocketStart(lobbyId);
    }

    private void sendSocketStart(String lobbyId) {
        if (lobbyService.gameStarted(lobbyId)) {
            simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/start", new StartMessage(lobbyService.getLobby(lobbyId).getPlayerTwo()));
        }
    }
}
