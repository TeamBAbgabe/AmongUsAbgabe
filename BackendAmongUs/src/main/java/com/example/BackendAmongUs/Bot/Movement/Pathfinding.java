package com.example.BackendAmongUs.Bot.Movement;

import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Map.Environment.Coords.Coordinates;
import com.example.BackendAmongUs.Map.Environment.Coords.Grid;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Player.Crewmate;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Tasks.Manager.TaskManager;
import com.example.BackendAmongUs.Tasks.Tasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Pathfinding {

    @Autowired
    private TaskManager taskManager;
    @Autowired
    private ActivePlayerManager activePlayerManager;
    @Autowired
    private Grid grid;
    @Autowired
    private Moving moving;

    private Coordinates[][] gridMap;

    public void getCoordinates(List<String> crewMateBots) throws IllegalMove, NoSessionIDs {
        gridMap = grid.getGrid();
        for(String bots : crewMateBots){
            if(activePlayerManager.getPlayer(bots).getRole() instanceof Crewmate){
                List<Tasks> assignedTask = taskManager.tasks(activePlayerManager.getPlayer(bots).getSession_id());
                System.out.println("Show me tasks" + assignedTask.get(0));
                pathFinding(assignedTask, bots);
                seeGrid(assignedTask.get(0) , bots);
            }
        }
    }

    private void pathFinding(List<Tasks> botTasks, String sessionId) {


    }

    public void seeGrid(Tasks task, String sessionId) throws IllegalMove, NoSessionIDs {
        Player bot = activePlayerManager.getPlayer(sessionId);
        Coordinates start = new Coordinates(bot.getX() / 32, bot.getY() / 32, 72);
        Coordinates ending = new Coordinates(task.getCoordinates().getX(), task.getCoordinates().getY(), task.getCoordinates().getTileType());


        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        start.setG(0);
        start.setH(manhattanDistance(start, ending));
        start.setF(start.getG() + start.getH());

        PriorityQueue<Coordinates> openSet = new PriorityQueue<>((c1, c2) -> Integer.compare(c1.getF(), c2.getF()));
        HashSet<Coordinates> closedNodes = new HashSet<>();
        openSet.add(start);

        while(!openSet.isEmpty()){
            Coordinates currentNode = openSet.poll();

            if(isGoal(currentNode, ending)){
                System.out.println("Found the path");
                List<Coordinates> path = new ArrayList<>();
                Coordinates current = currentNode;

                while (current != null) {
                    path.add(current);
                    current = current.getParent();
                }
                Collections.reverse(path);
                moving.schedulePathMovement(bot.getSession_id(), path, task);
                break;
            }

            closedNodes.add(currentNode);

            for (int[] direction : directions) {
                int nX = currentNode.getX() + direction[0];
                int nY = currentNode.getY() + direction[1];
                isWalkable(nX, nY);
                if(isWalkable(nX, nY) && !closedNodes.contains(new Coordinates(nX, nY, gridMap[nX][nY].getTileType()))) {
                    Coordinates neighborNode = new Coordinates(nX, nY, gridMap[nX][nY].getTileType());
                    int newG = currentNode.getG() + 1;
                    int newH = manhattanDistance(neighborNode, ending);
                    int newF = newG + newH;

                    boolean needToUpdate = false;
                    Coordinates nodeToUpdate = null;

                    for(Coordinates openNode : openSet) {
                        if(openNode.equals(neighborNode) && newG < neighborNode.getG()){
                            nodeToUpdate = neighborNode;
                            needToUpdate = true;
                            break;
                        }
                    }

                    if(nodeToUpdate != null) {
                        openSet.remove(nodeToUpdate);
                    }
                    if(nodeToUpdate == null || needToUpdate) {
                        neighborNode.setG(newG);
                        neighborNode.setH(newH);
                        neighborNode.setF(newF);
                        neighborNode.setParent(currentNode);
                        openSet.add(neighborNode);
                    }
                }

            }
        }

    }

    private boolean isGoal(Coordinates node, Coordinates goal) {
        return node.getX() == goal.getX() && node.getY() == goal.getY();
    }

    private int manhattanDistance(Coordinates current, Coordinates goal) {
        return Math.abs(current.getX() - goal.getX()) + Math.abs(current.getY() - goal.getY());
    }
    private boolean isWalkable(int nX, int nY) {
        System.out.println(gridMap[nX][nY].getTileType());
        if(gridMap[nX][nY].getTileType() == 72 || gridMap[nX][nY].getTileType() != 71 || gridMap[nX][nY].getTileType() != 74 || gridMap[nX][nY].getTileType() != 147) {
            return true;
        }
        return false;
    }
}
