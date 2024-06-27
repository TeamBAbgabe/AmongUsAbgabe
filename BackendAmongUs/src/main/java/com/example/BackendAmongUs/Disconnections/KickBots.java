package com.example.BackendAmongUs.Disconnections;

import com.example.BackendAmongUs.GameSession.GameSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KickBots {
    @Autowired
    private GameSessionManager gameSessionManager;

    public void kickBots(int lobbyId){

        gameSessionManager.deleteSessionsOutOfGame(lobbyId);
    }
}
