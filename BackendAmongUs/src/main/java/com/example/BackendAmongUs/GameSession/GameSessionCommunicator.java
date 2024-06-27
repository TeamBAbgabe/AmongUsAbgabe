package com.example.BackendAmongUs.GameSession;

import com.example.BackendAmongUs.Bot.Movement.Moving;
import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Lobby.Helper.UserSession;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Player.Crewmate;
import com.example.BackendAmongUs.Player.Imposter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GameSessionCommunicator {
    @Autowired
    private ActivePlayerManager activePlayerManager;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private GameSessionManager gameSessionManager;
    @Autowired
    private Moving moving;

    public void sendMessageToSession(String sessionId, Object dataObject, String destination) throws NoSessionIDs {
        Player currentPlayer = activePlayerManager.getPlayer(sessionId);
        if(currentPlayer.getDead()){
            messagingTemplate.convertAndSendToUser(sessionId, destination, dataObject);
        } else {
            List<String> game = gameSessionManager.retrieveCurrentGameSessionIDS(sessionId);
            for (String i : game) {
                messagingTemplate.convertAndSendToUser(i, destination, dataObject);
            }
        }
    }


    public void sendRoleMessages(String sessionId, String jsonData, String destination) throws NoSessionIDs {
        ObjectMapper mapper = new ObjectMapper();
        Player currentPlayer = activePlayerManager.getPlayer(sessionId);

        if (currentPlayer.getDead()) {
            messagingTemplate.convertAndSendToUser(sessionId, destination, jsonData);
        } else {
            List<String> game = gameSessionManager.retrieveCurrentGameSessionIDS(sessionId);
            for (String i : game) {
                Player player = activePlayerManager.getPlayer(i);
                String modifiedJsonData = jsonData;
                if (!(player.getRole() instanceof Imposter)) {
                    try {
                        if (jsonData.trim().startsWith("[")) {
                            List<Map<String, Object>> playerList = mapper.readValue(jsonData, new TypeReference<List<Map<String, Object>>>() {});
                            List<Map<String, Object>> modifiedList = new ArrayList<>();
                            for (Map<String, Object> playerData : playerList) {
                                if (!(player.getRole() instanceof Imposter)) {
                                    playerData.remove("Role");
                                }
                                modifiedList.add(playerData);
                            }
                            modifiedJsonData = mapper.writeValueAsString(modifiedList);
                        } else {
                            Map<String, Object> playerData = mapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
                            if (!(player.getRole() instanceof Imposter)) {
                                playerData.remove("Role");
                            }
                            modifiedJsonData = mapper.writeValueAsString(playerData);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                messagingTemplate.convertAndSendToUser(i, destination, modifiedJsonData);
            }
        }
    }


    public void sendStartGame(List<UserSession> userSessions, String destination) throws NoSessionIDs {

        for (UserSession i : userSessions) {
            if(activePlayerManager.getPlayer(i.getSessionId()).getRole() instanceof Imposter){
                messagingTemplate.convertAndSendToUser(i.getSessionId(), destination, "Imposter");
            }
            else {
                messagingTemplate.convertAndSendToUser(i.getSessionId(), destination, "Crewmate");
            }
        }
    }

    public void sendEndGame(int gameId, String destination, boolean impostersWin) throws NoSessionIDs, GameIdNotFound {
        List<String> game = gameSessionManager.retrieveCurrentGameSessionIDSByGameID(gameId);
        for (String s : game) {
            Player player = activePlayerManager.getPlayer(s);
            String message = "";
            if (player.getRole() instanceof Crewmate) {
                message = impostersWin ? "Defeat: Imposters have taken over the ship." : "Victory: All imposters have been eliminated.";
            } else if (player.getRole() instanceof Imposter) {
                message = impostersWin ? "Victory: You have successfully sabotaged the mission." : "Defeat: The crewmates have uncovered your deception.";
            }
            gameSessionManager.deleteSessionsOutOfGame(gameId);
            kickBots(game);
            messagingTemplate.convertAndSendToUser(s, destination, message);
        }
        remove(game);
    }

    private void remove(List<String> players){
        for(String s : players){
            Player removePlayer = activePlayerManager.getPlayer(s);
            activePlayerManager.deletePlayer(removePlayer);
        }
    }

    public void sendTaskEnd(int gameId, String destination) throws NoSessionIDs {
        List<String> game = gameSessionManager.retrieveCurrentGameSessionIDSByGameID(gameId);
        for (String s : game) {
            Player player = activePlayerManager.getPlayer(s);
            String message = "";
            if (player.getRole() instanceof  Crewmate) {
                message = "Victory: All tasks were completed";
            } else if (player.getRole() instanceof Imposter) {
                message = "Defeat: The Crewmates finished all Tasks";
            }
            gameSessionManager.deleteSessionsOutOfGame(gameId);
            messagingTemplate.convertAndSendToUser(s, destination, message);
        }
    }



    public void sendToAllPlayers(String sessionId, String destination, Object dataObject) throws NoSessionIDs {
        List<String> game = gameSessionManager.retrieveCurrentGameSessionIDS(sessionId);
        for (String i : game) {
            messagingTemplate.convertAndSendToUser(i, destination, dataObject);
        }
    }

    private void kickBots(List<String> bots) throws NoSessionIDs, GameIdNotFound {
        for(String s : bots){
            Player bot = activePlayerManager.getPlayer(s);
            if(bot.getUsername() != "Bot"){
                bots.remove(bot);
            }
        }
        moving.stopBots(bots);
    }

    public void sendToOnePlayer(String sessionId, String destination, Object dataObject) throws NoSessionIDs {
        System.out.println("dawdawd" + dataObject);
        messagingTemplate.convertAndSendToUser(sessionId, destination, dataObject);
    }

    public void sendToImposters(String sessionId, String destination, Object dataObject) throws NoSessionIDs, JsonProcessingException {
        System.out.println("My coords" + dataObject);
        List<String> gameSessions = gameSessionManager.retrieveCurrentGameSessionIDS(sessionId);
        for (String s : gameSessions) {
            Player player = activePlayerManager.getPlayer(s);
            if (player.getRole() instanceof Imposter) {
                messagingTemplate.convertAndSendToUser(sessionId, destination, dataObject);
            }
        }

    }
}
