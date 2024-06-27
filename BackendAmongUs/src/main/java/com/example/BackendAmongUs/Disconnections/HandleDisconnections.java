package com.example.BackendAmongUs.Disconnections;

import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.Gamestate.GameState;
import com.example.BackendAmongUs.Lobby.LobbyManager;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HandleDisconnections {
    @Autowired
    private ActivePlayerManager activePlayerManager;
    @Autowired
    private GameState gameState;
    @Autowired
    private GameSessionManager gameSessionManager;
    @Autowired
    private GameSessionCommunicator gameSessionCommunicator;
    @Autowired
    private LobbyManager lobbyManager;

    public void removeDisconnections(String sessionId) throws GameIdNotFound, NoSessionIDs, IOException {
        lobbyManager.removePlayer(sessionId);
        try {
            if (gameSessionManager.findGameIdBySessionId(sessionId) != null) {
                String destination = "/queue/removePlayer";
                String jsonMessage = String.format("{\"kickedSessionID\":\"%s\"}", sessionId);
                System.out.println("The player who left is " + activePlayerManager.getPlayer(sessionId));

                gameSessionCommunicator.sendMessageToSession(sessionId, jsonMessage, destination);
                activePlayerManager.getPlayer(sessionId).setDead(true);
                gameState.checkWinState(gameSessionManager.findGameIdBySessionId(sessionId));
            }
        } catch (GameIdNotFound e){
            e.printStackTrace();
        } catch (NoSessionIDs n){
            n.printStackTrace();
        }
    }

}

