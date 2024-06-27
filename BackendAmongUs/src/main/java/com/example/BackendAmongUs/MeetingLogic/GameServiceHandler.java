package com.example.BackendAmongUs.MeetingLogic;

import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionRetrievable;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.Gamestate.GameState;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.example.BackendAmongUs.Map.GamingMap;
import com.example.BackendAmongUs.MeetingLogic.Events.TimerEvent;
import com.example.BackendAmongUs.MeetingLogic.Exceptions.MeetingCouldntStart;
import com.example.BackendAmongUs.MeetingLogic.Exceptions.NoPlayerVoted;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.Player.Imposter;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Player.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceHandler {
    @Autowired
    private GameState gameState;

    @Autowired
    private MeetingAndVoting meetingAndVoting = new MeetingAndVoting();
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private GameSessionManager gameSession;
    @Autowired
    private ActivePlayerManager activePlayerManager = new ActivePlayerManager();
    @Autowired
    private GameSessionCommunicator gameSessionCommunicator;
    @Autowired
    private GamingMap gamingMap;

    private Random random = new Random();


    @EventListener
    public void handleTimerEvent(TimerEvent event) {
        if ("Kicking".equals(event.getMessage())) {
            kicking(event.getGameId());
        }
    }


    public void kicking(int gameId){
        try {
            Player kickedPlayer = meetingAndVoting.resolveVoting(gameId);
            if(kickedPlayer == null){
                String destination = "/queue/removePlayer";
                String sessionId = gameSession.retrieveOneSessionIDByGameId(gameId);
                gameSessionCommunicator.sendMessageToSession(sessionId, 0, destination);
                throw new NoPlayerVoted("No Player was selected or two had the same amount of Votes");
            }

            String kickedSessionID = kickedPlayer.getSession_id();
            String destination = "/queue/removePlayer";
            String jsonMessage = String.format("{\"kickedSessionID\":\"%s\"}", kickedSessionID);
            String json = convert(kickedSessionID);
            gameSessionCommunicator.sendMessageToSession(kickedSessionID, json, destination);

            Player deadPlayer = activePlayerManager.getPlayer(kickedSessionID);
            deadPlayer.setDead(true);
            gameState.checkWinState(gameId);
            meetingAndVoting.cleanVotes(gameId);
        } catch (NoPlayerVoted | NoSessionIDs | NoSessionRetrievable | GameIdNotFound e) {
            throw new RuntimeException(e);
        }
    }

    public String convert(String kickedSessionID) {
        Player currentPlayer = activePlayerManager.getPlayer(kickedSessionID);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("kickedSessionID", kickedSessionID);
        data.put("username", currentPlayer.getUsername());
        data.put("role", currentPlayer.getRole().getRoleName());


        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private void moveToPoint(int gameId) throws NoSessionIDs, IllegalMove, GameIdNotFound, JsonProcessingException {
        List<String> teleportPlayers = gameSession.retrieveCurrentGameSessionIDSByGameID(gameId);
        if (teleportPlayers.isEmpty()) {
            System.out.println("No players found for teleportation");
            return;
        }

        System.out.println("Teleporting players: " + teleportPlayers);
        for (String s : teleportPlayers) {
            int[] newCoordinates = getRandomSpawnLocation();
            System.out.println("Teleporting Session ID " + s + " to coordinates: X=" + newCoordinates[0] + ", Y=" + newCoordinates[1]);
            gamingMap.votingMoving(s, newCoordinates[0], newCoordinates[1]);  // Update to ensure method correctly handles new coordinates.
        }
    }


    private int[] getRandomSpawnLocation() {
        int[][] spawnArea = {
                {28, 28}, {28, 36}, {36, 29}, {36, 36}
        };
        int[][] restrictedArea = {
                {31, 31}, {31, 32}, {31, 33}, {32, 31}, {32, 32}, {32, 33}, {33, 31}, {33, 32}, {33, 33}
        };

        Set<String> restrictedPixels = new HashSet<>();
        for (int[] coord : restrictedArea) {
            restrictedPixels.add((coord[0] * 32) + "," + (coord[1] * 32));
        }

        int spawnX, spawnY;
        do {
            int[] randomTile = spawnArea[random.nextInt(spawnArea.length)];
            spawnX = randomTile[0] * 32;
            spawnY = randomTile[1] * 32;
        } while (restrictedPixels.contains(spawnX + "," + spawnY));

        return new int[]{spawnX, spawnY};
    }

    public void startMeeting(String sessionid) throws MeetingCouldntStart {
        try {
            int gameId = gameSession.findGameIdBySessionId(sessionid);
            moveToPoint(gameId);
            meetingAndVoting.setMeeting(sessionid);
        } catch (Exception e){
            e.printStackTrace();
            throw new MeetingCouldntStart("The Meeting couldnt Start");
        }
    }
}
