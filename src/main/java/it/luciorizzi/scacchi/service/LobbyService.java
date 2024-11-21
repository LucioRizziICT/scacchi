package it.luciorizzi.scacchi.service;

import it.luciorizzi.scacchi.model.Lobby;
import it.luciorizzi.scacchi.model.Player;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.type.GameStatus;
import it.luciorizzi.scacchi.model.type.PieceColor;
import it.luciorizzi.scacchi.util.RandomToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LobbyService {
    private final Map<String, Lobby> lobbies = new HashMap<>();
    private final Map<String, Player> tokens = new HashMap<>();

    public String createNewGame(PieceColor playerOneColor, String lobbyName, String playerName) {
        String lobbyId = RandomToken.generateToken(12);
        Player playerOne = new Player(playerName, lobbyId, playerOneColor);
        lobbies.put(lobbyId, new Lobby(playerOne, lobbyName));
        String token = RandomToken.generateToken(32);
        tokens.put(token, playerOne);
        return token;
    }

    //TODO: Get lobby by id from user

    //TODO: Get lobby info by lobby id

    private String joinLobby(String lobbyId, String playerName) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby.isFull()) {
            return null;
        }
        Player playerTwo = new Player(playerName, lobbyId, lobby.getPlayerOne().getColor().opposite());
        lobby.setPlayerTwo(playerTwo);
        String token = RandomToken.generateToken(32);
        tokens.put(token, playerTwo);
        return token;
    }

    public MoveSet getPossibleMoves(String token, String lobbyId, int row, int col) {
        Lobby lobby = getValidLobby(token, lobbyId);
        return lobby.getGameBoard().getPossibleMoves(row, col);
    }

    public boolean move(String token, String lobbyId, int fromRow, int fromCol, int toRow, int toCol, Character promotion) {
        Lobby lobby = getValidLobby(token, lobbyId);
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
}
