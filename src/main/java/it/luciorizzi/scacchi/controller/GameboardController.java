package it.luciorizzi.scacchi.controller;

import it.luciorizzi.scacchi.model.lobby.exception.LobbyNotFoundException;
import it.luciorizzi.scacchi.model.message.GameoverMessage;
import it.luciorizzi.scacchi.model.message.MessageWrapper;
import it.luciorizzi.scacchi.model.message.MoveMessage;
import it.luciorizzi.scacchi.model.message.StartMessage;
import it.luciorizzi.scacchi.model.type.GameOutcome;
import it.luciorizzi.scacchi.service.LobbyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameboardController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private LobbyService lobbyService;

    private final Logger logger = LoggerFactory.getLogger(GameboardController.class);


    @SubscribeMapping("/lobby/{lobbyId}/move")
    public void newClient(@DestinationVariable String lobbyId) {
        logger.debug("New client connected to lobby {}", lobbyId);
        sendSocketStart(lobbyId);
    }

    @MessageMapping("/lobby/{lobbyId}/move")
    public void move(@DestinationVariable String lobbyId, MessageWrapper<MoveMessage> messageWrapper) {
        MoveMessage moveMessage = messageWrapper.message();
        if (lobbyService.move(messageWrapper.playerToken(), lobbyId, moveMessage.fromRow(), moveMessage.fromCol(), moveMessage.toRow(), moveMessage.toCol(), moveMessage.promotion())) {
            Boolean isCheck = lobbyService.isCheck(messageWrapper.playerToken(), lobbyId);
            MoveMessage response = new MoveMessage(moveMessage.fromRow(), moveMessage.fromCol(), moveMessage.toRow(), moveMessage.toCol(), moveMessage.promotion(), isCheck);
            socketSendMove(lobbyId, response);
            checkGameEnded(messageWrapper.playerToken(), lobbyId);
        }
    }

    @MessageMapping("/lobby/{lobbyId}/resign")
    public void resign(@DestinationVariable String lobbyId, MessageWrapper<Void> messageWrapper) {
        lobbyService.resign(messageWrapper.playerToken(), lobbyId);
        checkGameEnded(messageWrapper.playerToken(), lobbyId);
    }

    private void checkGameEnded(String playerToken, String lobbyId) {
        if (lobbyService.gameEnded(playerToken, lobbyId)) {
            GameOutcome gameOutcome = lobbyService.getGameOutcome(playerToken, lobbyId);
            socketSendOutcome(lobbyId, new GameoverMessage(gameOutcome));
        }
    }


    private void sendSocketStart(String lobbyId) {
        if (lobbyService.gameStarted(lobbyId)) {
            simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/start", new StartMessage(lobbyService.getLobby(lobbyId).getPlayerTwo()));
        }
    }

    private void socketSendMove(String lobbyId, MoveMessage response) {
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/move", response);
    }

    private void socketSendOutcome(String lobbyId, GameoverMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/gameover", message);
    }

    @MessageExceptionHandler
    public void handleException(LobbyNotFoundException e) {
        logger.warn("Lobby not found. {}", e.getMessage());
    }
}
