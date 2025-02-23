package it.luciorizzi.scacchi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.luciorizzi.scacchi.model.TimerInfo;
import it.luciorizzi.scacchi.model.lobby.*;
import it.luciorizzi.scacchi.model.lobby.exception.*;
import it.luciorizzi.scacchi.model.movement.MoveSet;
import it.luciorizzi.scacchi.model.movement.Position;
import it.luciorizzi.scacchi.model.type.GameOutcome;
import it.luciorizzi.scacchi.openapi.model.ColorEnum;
import it.luciorizzi.scacchi.openapi.model.LobbyDTO;
import it.luciorizzi.scacchi.openapi.model.LobbyJoinRequestDTO;
import it.luciorizzi.scacchi.openapi.model.PlayerDTO;
import it.luciorizzi.scacchi.util.ApiDTOConverter;
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
    private final Map<String, Player> players = new HashMap<>();

    public LobbyDTO createLobby(LobbyDTO lobbyDTO, ColorEnum playerOneColor) {
        String lobbyId = RandomToken.generateLobbyToken();

        Player playerOne = new Player(lobbyDTO.getPlayerOne().getName(), lobbyId, ApiDTOConverter.toPieceColor(playerOneColor));

        LobbyProperties createdProperties = LobbyProperties.withDefaultProperties(); //TODO: Cambiare con DTO quando lobby prop verranno implementate
        createdProperties.setPrivate(lobbyDTO.getProperties().getIsPrivate());

        Lobby createdLobby = new Lobby(lobbyDTO.getName(), playerOne, lobbyDTO.getPassword(), createdProperties);
        lobbies.put( lobbyId, createdLobby ); //todo cambiare settings di default

        String token = RandomToken.generatePlayerToken();
        players.put(token, playerOne);

        return new LobbyDTO()
                .id(lobbyId)
                .name(createdLobby.getName())
                .playerOne(new PlayerDTO()
                        .token(token)
                        .name(playerOne.getName())
                        .color( ApiDTOConverter.toColorEnum(playerOne.getColor()) )
                )
                .playerTwo(null)
                .properties( ApiDTOConverter.toLobbyPropertiesDTO(createdProperties) );
    }

    public LobbyDTO joinLobby(String lobbyId, LobbyJoinRequestDTO joinRequest) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby.getPassword() != null && !lobby.getPassword().equals(joinRequest.getPassword())) {
            throw new LobbyLoginException("Wrong password");
        }
        if (lobby.isFull()) {
            throw new LobbyIsFullException("Lobby is full");
        }

        Player playerTwo = new Player(joinRequest.getPlayerName(), lobbyId, lobby.getPlayerOne().getColor().opposite());
        lobby.setPlayerTwo(playerTwo);

        String token = RandomToken.generatePlayerToken();
        players.put(token, playerTwo);

        return new LobbyDTO()
                .id(lobbyId)
                .name( lobby.getName() )
                .playerOne( ApiDTOConverter.toPlayerDTO(lobby.getPlayerOne()) )
                .playerTwo( ApiDTOConverter.toPlayerDTO(playerTwo)
                        .token(token)
                )
                .properties( ApiDTOConverter.toLobbyPropertiesDTO(lobby.getProperties()) );
    }

    public MoveSet getPossibleMoves(String token, String lobbyId, Position position) {
        Lobby lobby = getValidLobby(token, lobbyId);
        if (lobby.getGameBoard().getCurrentPlayer() != getPlayer(token).getColor()) {
            throw new LobbyActionException("Not your turn");
        }
        return lobby.getGameBoard().getPossibleMoves(position);
    }

    public boolean move(String token, String lobbyId, int fromRow, int fromCol, int toRow, int toCol, Character promotion) {
        Lobby lobby = getValidLobby(token, lobbyId);
        if (lobby.getGameBoard().getCurrentPlayer() != getPlayer(token).getColor()) {
            throw new LobbyActionException("Not your turn");
        }
        if (!lobby.gameStarted()) {
            throw new LobbyActionException("Game not started");
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
            throw new PlayerNotInLobbyException("Player not in lobby");
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
            throw new PlayerNotFoundException("Player not found");
        }
        return player;
    }

    public List<LobbyDTO> getPublicLobbies() {
        List<LobbyDTO> result = new ArrayList<>();
        lobbies.forEach( (id, lobby) -> {
            if (lobby.getProperties().isPrivate()) {
                return;
            }

            LobbyDTO lobbyDTO = new LobbyDTO()
                    .id(id)
                    .name(lobby.getName())
                    .playerOne( ApiDTOConverter.toPlayerDTO(lobby.getPlayerOne()) )
                    .playerTwo( ApiDTOConverter.toPlayerDTO(lobby.getPlayerTwo()) )
                    .properties( ApiDTOConverter.toLobbyPropertiesDTO(lobby.getProperties()) );

            result.add(lobbyDTO);
        });
        return result;
    }

    public ModelAndView getLobbyView(String token, String lobbyId, String lobbyPreferencesString) throws JsonProcessingException {
        LobbyPreferences lobbyPreferences = parsePreferences(lobbyPreferencesString);

        Lobby lobby = getLobby(lobbyId);
        Player presentPlayer = players.get(token);

        if ( presentPlayer != null && lobbyId.equals(presentPlayer.getGameId()) ) {
            int playerNumber = lobby.getPlayerOne().equals(presentPlayer) ? 1 : 2;
            return getPlayerLobbyView(presentPlayer, lobbyId, lobby, playerNumber, lobbyPreferences);
        }
        if (!lobby.isFull()) {
            return getJoinLobbyView(lobbyId, lobby);
        }
        return getFullLobbyView(lobby);
    }

    private LobbyPreferences parsePreferences(String lobbyPreferencesString) {
        try {
            return lobbyPreferencesString == null ? LobbyPreferences.getDefault() : objectMapper.readValue(lobbyPreferencesString, LobbyPreferences.class);
        } catch (JsonProcessingException e) {
            return LobbyPreferences.getDefault();
        }
    }

    private ModelAndView getPlayerLobbyView(Player player, String lobbyId, Lobby lobby, int playerNumber, LobbyPreferences lobbyPreferences ) throws JsonProcessingException {
        ModelAndView modelAndView = new ModelAndView("lobby");
        modelAndView.addObject("playerId", player.getId());
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
        modelAndView.addObject("lobbyPreferences", lobbyPreferences);
        modelAndView.addObject("lobbyPreferencesJsonString", objectMapper.writeValueAsString(lobbyPreferences));
        modelAndView.addObject("lobbyProperties", lobby.getProperties());
        modelAndView.addObject("timerInfoJsonString", objectMapper.writeValueAsString(lobby.getGameBoard().getTimerInfo()));
        modelAndView.addObject("noMovesPlayed", lobby.getGameBoard().noMovesPlayed());
        modelAndView.addObject("playerTurn", lobby.getGameBoard().getCurrentPlayer());
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

    public void resign(String token, String lobbyId) {
        Player player = getPlayer(token);
        if (!lobbyId.equals(player.getGameId())) {
            throw new PlayerNotInLobbyException("Player not in lobby");
        }

        getLobby(lobbyId).getGameBoard().resign(player.getColor());
    }

    public boolean requestDraw(String token, String lobbyId) {
        Player player = getPlayer(token);
        if (!lobbyId.equals(player.getGameId())) {
            throw new PlayerNotInLobbyException("Player not in lobby");
        }

        return getLobby(lobbyId).getGameBoard().requestDraw(player.getColor());
    }

    public void denyDraw(String token, String lobbyId) {
        Lobby lobby = getValidLobby(token, lobbyId);
        lobby.getGameBoard().denyDraw();
    }

    public boolean requestRematch(String token, String lobbyId) {
        Player player = getPlayer(token);
        if (!lobbyId.equals(player.getGameId())) {
            throw new PlayerNotInLobbyException("Player not in lobby");
        }

        return getLobby(lobbyId).requestRematch(player.getColor());
    }

    public TimerInfo getTimerInfo(String token, String lobbyId) {
        Lobby lobby = getValidLobby(token, lobbyId);
        return lobby.getGameBoard().getTimerInfo();
    }
}
