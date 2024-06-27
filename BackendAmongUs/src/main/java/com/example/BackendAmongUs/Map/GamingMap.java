package com.example.BackendAmongUs.Map;

import com.example.BackendAmongUs.GameSession.Event.GameStartEvent;
import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.Map.Environment.EnvironmentInteractionHandler;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.Player.Imposter;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Player.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GamingMap {
    @Autowired
    private EnvironmentInteractionHandler environmentInteractionHandler;
    @Autowired
    private ActivePlayerManager activePlayerManager = new ActivePlayerManager();
    @Autowired
    private GameSessionManager gameSession;
    @Autowired
    private GameSessionCommunicator gameSessionCommunicator;
    @EventListener
    public void handleTimerEvent(GameStartEvent event) throws NoSessionIDs, JsonProcessingException {
        if ("StartGame".equals(event.getMessage())) {
            mapGameStart(event.getSessionId(), event.getGameId());
        }
    }

    public void updateMatrix(String sessionId, int movementX, int movementY) throws NoSessionIDs, IllegalMove, GameIdNotFound, JsonProcessingException {
        Player currentPosition = activePlayerManager.getPlayer(sessionId);
        if (currentPosition == null) {
            System.out.println("Player position not found for session ID: " + sessionId);
            return;
        }
        //Validate Movement with CollisionDetection
        int newX = currentPosition.getX() + movementX;
        int newY = currentPosition.getY() + movementY;
        environmentInteractionHandler.tilesCoord(sessionId, newX / 32, newY / 32);
        currentPosition.setX(newX);
        currentPosition.setY(newY);

        String matrixJson = convert(newX, newY, sessionId, currentPosition.getRole());
        String destination = "/queue/map";
        gameSessionCommunicator.sendRoleMessages(sessionId, matrixJson, destination);
    }

    public void votingMoving(String sessionId, int movementX, int movementY) throws NoSessionIDs, IllegalMove, GameIdNotFound, JsonProcessingException {
        Player currentPosition = activePlayerManager.getPlayer(sessionId);

        currentPosition.setX(movementX);
        currentPosition.setY(movementY);
        String matrixJson = convert(movementX, movementY, sessionId, currentPosition.getRole());
        String destination = "/queue/map";
        gameSessionCommunicator.sendRoleMessages(sessionId, matrixJson, destination);
    }

    public void teleport(String sessionId, int teleportX, int teleportY) throws NoSessionIDs, IllegalMove {
        Player currentPosition = activePlayerManager.getPlayer(sessionId);
        int newX = teleportX;
        int newY = teleportY;

        currentPosition.setX(newX);
        currentPosition.setY(newY);

        String matrixJson = convert(newX, newY, sessionId, currentPosition.getRole());
        String destination = "/queue/map";
        gameSessionCommunicator.sendMessageToSession(sessionId, matrixJson, destination);
    }


    public String convert(int x, int y, String sessionId, Role includeRole) {
        Player currentPlayer = activePlayerManager.getPlayer(sessionId);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("x", x);
        data.put("y", y);
        data.put("sessionId", currentPlayer.getSession_id());
        data.put("username", currentPlayer.getUsername());
        data.put("color", currentPlayer.getColor());
        data.put("characterType", currentPlayer.getCharacter());
        data.put("Role", currentPlayer.getRole().getRoleName());


        if (includeRole instanceof Imposter) {
            data.put("Role", currentPlayer.getRole().getRoleName());
        }
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }


    public void mapGameStart(String sessionId, int gameId) throws NoSessionIDs, JsonProcessingException {
        List<String> gameToStart = gameSession.retrieveCurrentGameSessionIDS(sessionId);
        String sendablesession = gameToStart.get(0);
        List<Map<String, Object>> playersData = new ArrayList<>();

        for (String playerId : gameToStart) {
            Player currentPosition = activePlayerManager.getPlayer(playerId);
            Map<String, Object> playerData = new HashMap<>();
            playerData.put("x", currentPosition.getX());
            playerData.put("y", currentPosition.getY());
            playerData.put("sessionId", currentPosition.getSession_id());
            playerData.put("username", currentPosition.getUsername());
            playerData.put("color", currentPosition.getColor());
            playerData.put("Role", currentPosition.getRole().getRoleName());
            playerData.put("characterType", currentPosition.getCharacter());
            playersData.add(playerData);

            environmentInteractionHandler.addtilesCoord(currentPosition.getSession_id(), currentPosition.getX(), currentPosition.getY(), gameId);
        }
        environmentInteractionHandler.sendInitialCoords(sessionId, gameId);
        ObjectMapper mapper = new ObjectMapper();
        String jsonData = "";
        try {
            jsonData = mapper.writeValueAsString(playersData);
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON");
            e.printStackTrace();
        }
        String destination = "/queue/fullUpdate";
        gameSessionCommunicator.sendRoleMessages(sendablesession, jsonData,destination);
    }



}
