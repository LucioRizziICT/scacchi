package it.luciorizzi.scacchi.controller;

import it.luciorizzi.scacchi.model.timer.ChessTimer;
import it.luciorizzi.scacchi.model.timer.TimedGameTimeoutEvent;
import it.luciorizzi.scacchi.model.timer.TimerInfo;
import it.luciorizzi.scacchi.model.lobby.Player;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyNotFoundException;
import it.luciorizzi.scacchi.model.message.*;
import it.luciorizzi.scacchi.model.type.GameOutcome;
import it.luciorizzi.scacchi.service.LobbyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
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
        String token = messageWrapper.playerToken();
        MoveMessage moveMessage = messageWrapper.message();
        if (lobbyService.move(token, lobbyId, moveMessage.fromRow(), moveMessage.fromCol(), moveMessage.toRow(), moveMessage.toCol(), moveMessage.promotion())) {
            TimerInfo timerInfo = lobbyService.getTimerInfo(token, lobbyId);
            boolean isCheck = lobbyService.isCheck(token, lobbyId);
            MoveMessage response = new MoveMessage(moveMessage.fromRow(), moveMessage.fromCol(), moveMessage.toRow(), moveMessage.toCol(), moveMessage.promotion(), isCheck, timerInfo);
            socketSendMove(lobbyId, response);
            checkGameEnded(token, lobbyId);
        }
    }

    @EventListener
    public void handleTimedGameTimeoutEvent(TimedGameTimeoutEvent event) {
        String lobbyId = ((ChessTimer) event.getSource()).getLobbyId();
        GameOutcome gameOutcome = lobbyService.getGameOutcome(lobbyId);
        socketSendOutcome(lobbyId, new GameoverMessage(gameOutcome));
    }

    @MessageMapping("/lobby/{lobbyId}/resign")
    public void resign(@DestinationVariable String lobbyId, MessageWrapper<Void> messageWrapper) {
        lobbyService.resign(messageWrapper.playerToken(), lobbyId);
        checkGameEnded(messageWrapper.playerToken(), lobbyId);
    }

    @MessageMapping("/lobby/{lobbyId}/draw")
    public void requestDraw(@DestinationVariable String lobbyId, MessageWrapper<DrawMessage> messageWrapper) {
        boolean accept = messageWrapper.message().accept();
        String playerToken = messageWrapper.playerToken();
        Player player = lobbyService.getPlayer(playerToken);
        if (accept) {
            if (lobbyService.requestDraw(playerToken, lobbyId)) {
                socketSendNotification(lobbyId, NotificationMessage.defaultDrawAccepted(player.getId()));
                GameOutcome gameOutcome = lobbyService.getGameOutcome(playerToken, lobbyId);
                socketSendOutcome(lobbyId, new GameoverMessage(gameOutcome));
            } else {
                socketSendNotification(lobbyId, NotificationMessage.defaultDrawRequest(player.getId()));
            }
        } else {
            lobbyService.denyDraw(messageWrapper.playerToken(), lobbyId);
            socketSendNotification(lobbyId, NotificationMessage.defaultDrawDenied(player.getId()));
        }
    }

    @MessageMapping("/lobby/{lobbyId}/rematch")
    public void requestRematch(@DestinationVariable String lobbyId, MessageWrapper<Void> messageWrapper) {
        String playerToken = messageWrapper.playerToken();
        Player player = lobbyService.getPlayer(playerToken);
        if (lobbyService.requestRematch(playerToken, lobbyId)) {
            socketSendNotification(lobbyId, NotificationMessage.defaultRematchAccepted(player.getId()));
            sendSocketRematch(lobbyId);
        } else {
            socketSendNotification(lobbyId, NotificationMessage.defaultRematchRequest(player.getId()));
        }
    }

    private void sendSocketRematch(String lobbyId) {
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/rematch", "");
    }

    private void socketSendNotification(String lobbyId, NotificationMessage message) {
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/notification", message);
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
