package it.luciorizzi.scacchi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.luciorizzi.scacchi.model.lobby.*;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyActionException;
import it.luciorizzi.scacchi.model.lobby.exception.LobbyNotFoundException;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.GameOutcome;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.util.RandomToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final Map<String, Player> players = new HashMap<>();

    public Map<String, Object> createNewGame(String lobbyName, String playerName, String password, PieceColor playerOneColor, String lobbyType) {
        String lobbyId = RandomToken.generateToken(12);
        Player playerOne = new Player(playerName, lobbyId, playerOneColor);
        LobbyProperties properties = LobbyProperties.withDefaultProperties();
        properties.setPrivate(lobbyType.equals("private"));
        lobbies.put( lobbyId, new Lobby(lobbyName, playerOne, password, properties) ); //todo cambiare settings di default
        String token = RandomToken.generateToken(32);
        players.put(token, playerOne);
        Map<String, Object> result = new HashMap<>(); //TODO: Cambiare con DTO quando implementato
        result.put("lobbyId", lobbyId);
        result.put("playerToken", token);
        return result;
    } //DTO

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
        players.put(token, playerTwo);
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
            throw new LobbyActionException("Not your turn", HttpStatus.FORBIDDEN);
        }
        if (!lobby.gameStarted()) {
            throw new LobbyActionException("Game not started", HttpStatus.FORBIDDEN);
        }
        return lobby.getGameBoard().movePiece(fromRow, fromCol, toRow, toCol, promotion);
    }

    public boolean isCheck(String token, String lobbyId) {
        Lobby lobby = getValidLobby(token, lobbyId);
        return lobby.getGameBoard().isCheck();
    }

    public boolean gameEnded(String token, String lobbyId) {
        Lobby lobby = getValidLobby(token, lobbyId);
        return ! lobby.getGameBoard().isOngoing();
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

    public Lobby getLobby(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            throw new LobbyNotFoundException("Lobby id: " + lobbyId);
        }
        return lobby;
    }

    public Player getPlayer(String token) {
        Player player = players.get(token);
        if (player == null) {
            throw new RuntimeException("Player not found");
        }
        return player;
    }

    public List<Map<String, Object>> getPublicLobbies() {
        List<Map<String, Object>> result = new ArrayList<>();
        lobbies.forEach((id, lobby) -> {
            if (lobby.getProperties().isPrivate()) {
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
        Player presentPlayer = players.get(token);

        if ( presentPlayer != null && lobbyId.equals(presentPlayer.getGameId()) ) {
            int playerNumber = lobby.getPlayerOne().equals(presentPlayer) ? 1 : 2;
            return getPlayerLobbyView(presentPlayer, lobbyId, lobby, playerNumber);
        }
        if (!lobby.isFull()) {
            return getJoinLobbyView(lobbyId, lobby);
        }
        return getFullLobbyView(lobby);
    }

    private ModelAndView getPlayerLobbyView(Player player, String lobbyId, Lobby lobby, int playerNumber) throws JsonProcessingException {
        ModelAndView modelAndView = new ModelAndView("lobby");
        modelAndView.addObject("playerNumber", playerNumber);
        modelAndView.addObject("playerColor", player.getColor());
        modelAndView.addObject("player1Name", lobby.getPlayerOne().getName());
        modelAndView.addObject("player2Name", lobby.getPlayerTwo() == null ? null : lobby.getPlayerTwo().getName());
        modelAndView.addObject("player1Color", lobby.getPlayerOne().getColor());
        modelAndView.addObject("player2Color", lobby.getPlayerOne().getColor().opposite());
        modelAndView.addObject("lobbyName", lobby.getName());
        modelAndView.addObject("lobbyId", lobbyId);
        modelAndView.addObject("gameBoard", objectMapper.writeValueAsString(lobby.getGameBoard().getBoard()));
        modelAndView.addObject("gameStarted", lobby.gameStarted());
        modelAndView.addObject("gameOutcome", lobby.getGameBoard().getMovesHistory().getOutcome());
        return modelAndView;
    }

    private ModelAndView getJoinLobbyView(String lobbyId, Lobby lobby) {
        ModelAndView modelAndView = new ModelAndView("joinLobby");
        modelAndView.addObject("lobbyName", lobby.getName());
        modelAndView.addObject("lobbyId", lobbyId);
        return modelAndView;
    }

    private ModelAndView getFullLobbyView(Lobby lobby) {
        ModelAndView modelAndView = new ModelAndView("lobbyFull");
        modelAndView.addObject("lobbyName", lobby.getName());
        modelAndView.addObject("lobbyAllowsSpectators", lobby.getProperties().allowsSpectators());
        return new ModelAndView("lobbyFull");
    }

    public GameOutcome getGameOutcome(String token, String lobbyId) {
        Lobby lobby = getValidLobby(token, lobbyId);
        return lobby.getGameBoard().getMovesHistory().getOutcome();
    }

    public boolean gameStarted(String lobbyId) {
        Lobby lobby = getLobby(lobbyId);
        return lobby.gameStarted();
    }
}
