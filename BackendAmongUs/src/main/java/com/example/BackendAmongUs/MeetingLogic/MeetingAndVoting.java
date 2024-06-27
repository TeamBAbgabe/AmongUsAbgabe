package com.example.BackendAmongUs.MeetingLogic;


import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.example.BackendAmongUs.Map.GamingMap;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.Player.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MeetingAndVoting {
    private Map<Player, Player> votes = new HashMap<>();
    private Map<Integer, Map<Player, Player>> gameVotes = new HashMap<>();
    @Autowired
    private ActivePlayerManager activePlayerManager = new ActivePlayerManager();

    @Autowired
    private GameSessionManager gameSession;
    @Autowired
    private GameSessionCommunicator gameSessionCommunicator;
    @Autowired
    private ReportDeadPlayer reportDeadPlayer;
    @Autowired
    private GamingMap gamingMap;

    private boolean hasMeetingStarted = false;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void cleanVotes(int gameId){
        Map<Player, Player> getVotes = gameVotes.get(gameId);
        getVotes.clear();
    }
    public void setVotes(Player voter, Player suspect) throws GameIdNotFound {
        if(!voter.equals(suspect) && !voter.getDead() && !suspect.getDead()){
            votes.put(voter, suspect);
            int gameid = gameSession.findGameIdBySessionId(voter.getSession_id());
            gameVotes.put(gameid, votes);
        } else System.out.println("Cant do that");
    }

    public Player resolveVoting(int gameId){
        Map<Player, Integer> voteCounts = new HashMap<>();
        Map<Player, Player> voting = gameVotes.get(gameId);
        if(voting == null) {
            return null;
        }
        for (Player suspect : voting.values()) {
            voteCounts.put(suspect, voteCounts.getOrDefault(suspect, 0) + 1);
        }

        Player mostVotedSuspect = null;
        int maxVotes = 0;

        for (Map.Entry<Player, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() >= maxVotes) {
                if(maxVotes == entry.getValue() &&mostVotedSuspect != null){
                    mostVotedSuspect = null;
                } else {
                    mostVotedSuspect = entry.getKey();
                    maxVotes = entry.getValue();
                }
            }
        }
        return mostVotedSuspect;
    }

    public void setMeeting(String sessionId) throws NoSessionIDs, GameIdNotFound, IllegalMove, JsonProcessingException {
        hasMeetingStarted = true;
        List<Map<String, Object>> playersData = new ArrayList<>();
        List<String> playerIds = gameSession.retrieveCurrentGameSessionIDS(sessionId);
        removeGraves(playerIds);


        for (String playerId : playerIds) {
            reportDeadPlayer.sendReportButton(playerId, false);
            Player player = activePlayerManager.getPlayer(playerId);
            if (!player.getDead()) {
                Map<String, Object> playerInfo = new HashMap<>();
                playerInfo.put("sessionId", player.getSession_id());
                playerInfo.put("userName", player.getUsername());
                playerInfo.put("avatar", player.getAvatar());
                playerInfo.put("color", player.getColor());

                playersData.add(playerInfo);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        String jsonOutput = "{}";
        try {
            jsonOutput = mapper.writeValueAsString(playersData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(playersData);
        String destination = "/queue/voting";
        gameSessionCommunicator.sendMessageToSession(sessionId, jsonOutput, destination);
    }

    private void removeGraves(List<String> removeGraves){
        for(String graveCheck : removeGraves){
            Player grave = activePlayerManager.getPlayer(graveCheck);
            if(grave.isGrave()){
                grave.setGrave(false);
            }
        }
    }
}


