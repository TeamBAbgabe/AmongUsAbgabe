package com.example.BackendAmongUs.Movement;

import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Map.Environment.EnvironmentInteractionHandler;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.example.BackendAmongUs.Map.GamingMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MovementHandler {

    @Autowired
    private GamingMap matrix;

    @Autowired
    private EnvironmentInteractionHandler collisionDetection;


    public void movePlayer(String sessionId, String direction) throws NoSessionIDs, IllegalMove, GameIdNotFound, JsonProcessingException {

        int deltaX = 0, deltaY = 0;
        switch (direction) {
            case "w": deltaY = -3; break;
            case "a": deltaX = -3; break;
            case "s": deltaY = 3; break;
            case "d": deltaX = 3; break;
            case "e": collisionDetection.votingTerminal(sessionId); break;
            case "f": collisionDetection.tunnel(sessionId); break;
        }
        collisionDetection.checkTiles(sessionId);
        System.out.println(deltaX + " " + deltaY);
        if(!collisionDetection.checkIfWalkable(deltaX, deltaY, sessionId)) {
            matrix.updateMatrix(sessionId, deltaX, deltaY);
        }

    }
}
