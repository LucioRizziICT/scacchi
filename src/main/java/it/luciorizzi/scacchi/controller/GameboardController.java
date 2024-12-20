package it.luciorizzi.scacchi.controller;

import it.luciorizzi.scacchi.model.lobby.LobbyActionException;
import it.luciorizzi.scacchi.model.message.ApplicationError;
import it.luciorizzi.scacchi.model.message.GameoverMessage;
import it.luciorizzi.scacchi.model.message.MessageWrapper;
import it.luciorizzi.scacchi.model.message.MoveMessage;
import it.luciorizzi.scacchi.model.type.GameOutcome;
import it.luciorizzi.scacchi.service.LobbyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
                socketSendOutcome(lobbyId, gameOutcome);
            }
        }
    }

    private void socketSendMove(String lobbyId, MoveMessage response) {
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/move", response);
    }

    private void socketSendOutcome(String lobbyId, GameOutcome gameOutcome) {
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/gameover", gameOutcome);
    }


    @MessageExceptionHandler(LobbyActionException.class) //TODO: Maybe remove, not sure its really useful
    public ApplicationError handleLobbyActionException(HttpServletRequest reqest, Exception exception) {
        HttpStatus status = ((LobbyActionException) exception).getStatus();
        return new ApplicationError(status.getReasonPhrase(), exception.getMessage()); //for now just use http status as cause
    }
}
