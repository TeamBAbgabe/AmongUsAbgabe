package com.example.BackendAmongUs.Tasks.Manager;

import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TaskBar {
    private GameSessionManager gameSessionManager;

    private GameSessionCommunicator gameSessionCommunicator;

    @Autowired
    public void setGameSessionManager(@Lazy GameSessionManager gameSessionManager, @Lazy GameSessionCommunicator gameSessionCommunicator) {
        this.gameSessionManager = gameSessionManager;
        this.gameSessionCommunicator = gameSessionCommunicator;
    }

    private Map<Integer, Integer> taskBard = new HashMap<>();
    private Map<Integer, Double> taskPercentage = new HashMap<>();
    private Map<Integer, Double> completedTasks = new HashMap<>();

    public void addToTaskBar(int gameId, int crewmateAmount) {
        int totalTasks = crewmateAmount * 2;
        taskBard.put(gameId, totalTasks);
        completedTasks.put(gameId, 0.0);
        taskPercentage.put(gameId, 0.0);
    }


    public void updateTaskBar(String sessionId) throws GameIdNotFound, NoSessionIDs {
        int gameId = gameSessionManager.findGameIdBySessionId(sessionId);

        if (!taskBard.containsKey(gameId)) {
            throw new GameIdNotFound("Game ID not found in task bar data.");
        }

        int totalTasks = taskBard.get(gameId);


        System.out.println("Completed Tasks: " + completedTasks + " / " + totalTasks);
        double completedTask = completedTasks.get(gameId);
        completedTask += 1;
        completedTasks.put(gameId,completedTask);
        double newPercentage = (completedTask / totalTasks) * 100;
        taskPercentage.put(gameId, newPercentage);

        String destination = "/queue/taskBar";
        gameSessionCommunicator.sendToAllPlayers(sessionId, destination, newPercentage);
        System.out.println("Updated percentage: " + newPercentage + "%");
        checkWinCondition(newPercentage, sessionId);
    }





    private void checkWinCondition(double newPercentage, String sessionId) throws GameIdNotFound, NoSessionIDs {
        if(newPercentage == 100) {
            String destination = "/queue/Endgame";
            int gameId = gameSessionManager.findGameIdBySessionId(sessionId);
            gameSessionCommunicator.sendTaskEnd(gameId, destination); // false indicating crewmates won
        }
    }
}
