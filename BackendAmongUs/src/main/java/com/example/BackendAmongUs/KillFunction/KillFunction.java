package com.example.BackendAmongUs.KillFunction;

import com.example.BackendAmongUs.DatenBank.DatenBankService;
import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.Gamestate.GameState;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Player.Crewmate;
import com.example.BackendAmongUs.Player.Imposter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KillFunction {
    @Autowired
    private ActivePlayerManager activePlayerManager;
    @Autowired
    private KillCoolDown killCoolDown;
    @Autowired
    private GameSessionManager gameSessionManager;
    @Autowired
    private GameState gameState;
    @Autowired
    private GameSessionCommunicator gameSessionCommunicator;
    @Autowired
    private DatenBankService datenBankService;

    /**
     * Nearest Player Search Algorithm with Range and Role Checks.
     * This method finds the nearest player within a 15-unit radius who also matches a specified role.
     *
     *
     * @param myPlayerId The ID of the player initiating the search.
     * Set killable Timer for 1 Minute
     * Set killed Player as dead
     * Send out the Death Message to the killed Player
     * Check Wining Conditions
     * @throws NoSessionIDs If there are issues retrieving player IDs or coordinates.
     */
    public void findNearestKillablePlayer(String myPlayerId) throws NoSessionIDs, GameIdNotFound {
        Player myPlayer = activePlayerManager.getPlayer(myPlayerId);
        if(!myPlayer.getDead() && myPlayer.getRole() instanceof Imposter && myPlayer.getRole().isCanKill()){
            List<String> allPlayers = gameSessionManager.retrieveCurrentGameSessionIDS(myPlayerId);
            String nearestKillablePlayer = null;
            int minDistanceSquared = Integer.MAX_VALUE;


            for (String playerId : allPlayers) {
                if (!playerId.equals(myPlayerId)) {
                    Player otherPlayer = activePlayerManager.getPlayer(playerId);
                    int distanceSquared = (myPlayer.getX() - otherPlayer.getX()) * (myPlayer.getX() - otherPlayer.getX()) + (myPlayer.getY() - otherPlayer.getY()) * (myPlayer.getY() - otherPlayer.getY());

                    if (distanceSquared <= 225 && otherPlayer.getRole() instanceof Crewmate && distanceSquared < minDistanceSquared && !otherPlayer.getDead()) {  // 15*15 for Euclidean distance squared, role check, and nearer than any previously checked
                        nearestKillablePlayer = playerId;
                        minDistanceSquared = distanceSquared;
                    }
                }
            }
            Player deadplayer = activePlayerManager.getPlayer(nearestKillablePlayer);
            String destination = "/queue/Death";
            deadplayer.setDead(true);
            deadplayer.setGrave(true);
            String json = convert(nearestKillablePlayer);
            gameSessionCommunicator.sendToAllPlayers(nearestKillablePlayer, destination, json);
            //gameSessionCommunicator.sendMessageToSession(nearestKillablePlayer, json, destination);
            gameState.checkWinState(gameSessionManager.findGameIdBySessionId(nearestKillablePlayer));
            killCoolDown.killingTimer(myPlayerId);
            datenBankService.updateKills(myPlayer.getUsername());
            datenBankService.updateDeath(deadplayer.getUsername());

        }
    }

    public String convert(String sessionId) {
        Player deathPlayer = activePlayerManager.getPlayer(sessionId);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("x", deathPlayer.getX());
        data.put("y", deathPlayer.getY());
        data.put("sessionId", deathPlayer.getSession_id());
        data.put("username", deathPlayer.getUsername());
        data.put("color", deathPlayer.getColor());

        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
