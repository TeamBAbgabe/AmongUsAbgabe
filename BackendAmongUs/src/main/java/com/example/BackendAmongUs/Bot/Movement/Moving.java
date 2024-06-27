package com.example.BackendAmongUs.Bot.Movement;

import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Map.Environment.Coords.Coordinates;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.example.BackendAmongUs.Map.GamingMap;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Movement.MovementHandler;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Tasks.Manager.TaskManager;
import com.example.BackendAmongUs.Tasks.Tasks;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class Moving {


    private GamingMap gamingMap;
    @Autowired
    private ActivePlayerManager activePlayerManager;
    @Autowired
    private TaskManager taskManager;
    private Pathfinding pathfinding;

    private ScheduledExecutorService executorService;
    private Map<String, Future<?>> botTasks = new HashMap<>();
    private Map<String, List<Coordinates>> botPaths = new HashMap<>();
    private boolean isFirstRun = true; // Flag to check if it's the first run

    public Moving(@Lazy GamingMap gamingMap, @Lazy Pathfinding pathfinding) {
        this.executorService = Executors.newScheduledThreadPool(10);
        this.gamingMap = gamingMap;
        this.pathfinding = pathfinding;
    }

    public void schedulePathMovement(String bot, List<Coordinates> path, Tasks tasks) {
        if (path.isEmpty()) {
            System.out.println("No path found for bot: " + bot);
            return;
        }

        Runnable movementTask = () -> {
            Iterator<Coordinates> pathIterator = path.iterator();
            Player botMover = activePlayerManager.getPlayer(bot);
            try {
                moveTowardsNode(bot, pathIterator, botMover, tasks);
            } catch (NoSessionIDs | GameIdNotFound | IllegalMove e) {
                throw new RuntimeException(e);
            }
        };

        if (isFirstRun) {
            System.out.println("Starting movement sequence after 5 seconds...");
            executorService.schedule(movementTask, 5, TimeUnit.SECONDS);
            isFirstRun = false;
        } else {
            System.out.println("Starting movement sequence immediately...");
            executorService.schedule(movementTask, 0, TimeUnit.SECONDS);
        }
    }

    private void moveTowardsNode(String bot, Iterator<Coordinates> pathIterator, Player botMover, Tasks tasks) throws NoSessionIDs, GameIdNotFound, IllegalMove {
        if (pathIterator.hasNext()) {
            Coordinates nextStep = pathIterator.next();
            int targetX = nextStep.getX() * 32;
            int targetY = nextStep.getY() * 32;

            ScheduledFuture<?> task = executorService.scheduleAtFixedRate(() -> {
                if (botMover.getX() != targetX || botMover.getY() != targetY) {
                    int moveX = Integer.compare(targetX, botMover.getX());
                    int moveY = Integer.compare(targetY, botMover.getY());

                    System.out.println("Moving bot " + bot + " to pixel: " + (botMover.getX() + moveX) + ", " + (botMover.getY() + moveY));

                    botMover.setX(botMover.getX() + moveX + 3);
                    botMover.setY(botMover.getY() + moveY + 3);
                    try {
                        gamingMap.updateMatrix(bot, moveX, moveY);
                    } catch (NoSessionIDs | IllegalMove | GameIdNotFound | JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (botTasks.containsKey(bot)) {
                        botTasks.get(bot).cancel(false);
                    }
                    try {
                        moveTowardsNode(bot, pathIterator, botMover, tasks);
                    } catch (NoSessionIDs | GameIdNotFound | IllegalMove e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 0, 100, TimeUnit.MILLISECONDS);

            botTasks.put(bot, task);
        } else {
            System.out.println("Path completed for bot: " + bot);
            stopBot(bot, tasks);
        }
    }

    private void stopBot(String bot, Tasks tasks) throws NoSessionIDs, GameIdNotFound, IllegalMove {
        if (botTasks.containsKey(bot)) {
            botTasks.get(bot).cancel(true);
            botTasks.remove(bot);
            executorService.schedule(() -> {
                try {
                    taskManager.checkDone(bot, tasks.getTaskId());
                } catch (GameIdNotFound | NoSessionIDs e) {
                    throw new RuntimeException(e);
                }

                List<Tasks> newTasks = taskManager.tasks(bot);
                if (!newTasks.isEmpty()) {
                    try {
                        pathfinding.seeGrid(newTasks.get(0), bot);
                    } catch (IllegalMove | NoSessionIDs e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("No new tasks available for bot: " + bot);
                }
            }, 10, TimeUnit.SECONDS);
            System.out.println("Stopped movement for bot: " + bot);
        }
    }

    public void stopBots(List<String> bots) throws NoSessionIDs, GameIdNotFound {
        for (String bot : bots) {
            stopBots(bot);
        }
    }

    private void stopBots(String bot) throws NoSessionIDs, GameIdNotFound {
        if (botTasks.containsKey(bot)) {
            botTasks.get(bot).cancel(true);
            botTasks.remove(bot);
            System.out.println("Stopped movement for bot: " + bot);
        }
    }
}
