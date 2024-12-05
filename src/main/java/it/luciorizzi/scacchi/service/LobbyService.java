package it.luciorizzi.scacchi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.luciorizzi.scacchi.model.Lobby;
import it.luciorizzi.scacchi.model.Player;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.GameStatus;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.util.RandomToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LobbyService {

    @Autowired
    ObjectMapper objectMapper;

    private final Map<String, Lobby> lobbies = new HashMap<>();
    private final Map<String, Player> tokens = new HashMap<>();

    public Map<String, Object> createNewGame(String lobbyName, String playerName, String password, PieceColor playerOneColor, String lobbyType) {
        String lobbyId = RandomToken.generateToken(12);
        Player playerOne = new Player(playerName, lobbyId, playerOneColor);
        lobbies.put(lobbyId, new Lobby(lobbyName, playerOne, password, lobbyType.equals("private")));
        String token = RandomToken.generateToken(32);
        tokens.put(token, playerOne);
        Map<String, Object> result = new HashMap<>();
        result.put("lobbyId", lobbyId);
        result.put("playerToken", token);
        return result;
    }

    //TODO: Get lobby by id from user

    //TODO: Get lobby info by lobby id

    public String joinLobby(String lobbyId, String playerName, String password) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby.getPassword() != null && !lobby.getPassword().equals(password)) {
            throw new IllegalArgumentException("Wrong password");
        }
        if (lobby.isFull()) {
            throw new IllegalArgumentException("Lobby is full");
        }
        Player playerTwo = new Player(playerName, lobbyId, lobby.getPlayerOne().getColor().opposite());
        lobby.setPlayerTwo(playerTwo);
        String token = RandomToken.generateToken(32);
        tokens.put(token, playerTwo);
        return token;
    }

    public MoveSet getPossibleMoves(String token, String lobbyId, int row, int col) {
        Lobby lobby = getValidLobby(token, lobbyId);
        if (lobby.getGameBoard().getCurrentPlayer() != getPlayer(token).getColor()) {
            throw new IllegalArgumentException("Not your turn");
        }
        return lobby.getGameBoard().getPossibleMoves(row, col);
    }

    public boolean move(String token, String lobbyId, int fromRow, int fromCol, int toRow, int toCol, Character promotion) {
        Lobby lobby = getValidLobby(token, lobbyId);
        if (lobby.getGameBoard().getCurrentPlayer() != getPlayer(token).getColor()) {
            throw new IllegalArgumentException("Not your turn");
        }
        return lobby.getGameBoard().movePiece(fromRow, fromCol, toRow, toCol, promotion);
    }

    public boolean isCheck(String token, String lobbyId) {
        Lobby lobby = getValidLobby(token, lobbyId);
        return lobby.getGameBoard().isCheck();
    }

    public GameStatus getGameStatus(String token, String lobbyId) {
        Lobby lobby = getValidLobby(token, lobbyId);
        return lobby.getGameBoard().getGameStatus();
    }

    public List<String> getMovesHistory(String token, String lobbyId) {
        Lobby lobby = getValidLobby(token, lobbyId);
        return lobby.getGameBoard().getMovesHistory().getNotation();
    }

    private Lobby getValidLobby(String token, String lobbyId) {
        Player player = getPlayer(token);
        if (!lobbyId.equals(player.getGameId())) {
            throw new IllegalArgumentException("Player not in lobby");
        }
        return getLobby(lobbyId);
    }

    private Lobby getLobby(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            throw new RuntimeException("Lobby not found");
        }
        return lobby;
    }

    public Player getPlayer(String token) {
        Player player = tokens.get(token);
        if (player == null) {
            throw new RuntimeException("Player not found");
        }
        return player;
    }

    public List<Map<String, Object>> getPublicLobbies() {
        List<Map<String, Object>> result = new ArrayList<>();
        lobbies.forEach((id, lobby) -> {
            if (lobby.isPrivate()) {
                return;
            }
            Map<String, Object> lobbyInfo = new HashMap<>();
            lobbyInfo.put("id", id);
            lobbyInfo.put("name", lobby.getName());
            lobbyInfo.put("playerOne", lobby.getPlayerOne().getName());
            lobbyInfo.put("playerTwo", lobby.getPlayerTwo() == null ? null : lobby.getPlayerTwo().getName());
            result.add(lobbyInfo);
        });
        return result;
    }

    public ModelAndView getLobbyView(String token, String lobbyId) throws JsonProcessingException {
        Lobby lobby = getLobby(lobbyId);
        Player presentPlayer = tokens.get(token);
        if ( token != null && presentPlayer != null && lobbyId.equals(presentPlayer.getGameId()) ) {
            ModelAndView modelAndView = new ModelAndView("lobby");
            modelAndView.addObject("playerColor", getPlayer(token).getColor());
            modelAndView.addObject("lobbyName", lobby.getName());
            modelAndView.addObject("lobbyId", lobbyId);
            modelAndView.addObject("gameBoard", objectMapper.writeValueAsString(lobby.getGameBoard().getBoard()));
            modelAndView.addObject("playerToken", token);
            return modelAndView;
        }
        if (!lobby.isFull()) {
            ModelAndView modelAndView = new ModelAndView("joinLobby");
            modelAndView.addObject("lobbyName", lobby.getName());
            modelAndView.addObject("lobbyId", lobbyId);
            return modelAndView;
        }
        return new ModelAndView("lobbyFull"); //TODO: Implement
    }
}
