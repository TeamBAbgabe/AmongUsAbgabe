package com.example.BackendAmongUs.Map.Environment;

import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.KillFunction.KillFunction;
import com.example.BackendAmongUs.Map.Environment.Coords.Coordinates;
import com.example.BackendAmongUs.Map.Environment.Coords.Grid;
import com.example.BackendAmongUs.Map.Environment.Coords.TeleportTimer;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.example.BackendAmongUs.Map.GamingMap;
import com.example.BackendAmongUs.Map.PictureSender.PictureSender;
import com.example.BackendAmongUs.MeetingLogic.GameController.MeetingController;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.MeetingLogic.ReportDeadPlayer;
import com.example.BackendAmongUs.Player.Crewmate;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Player.Imposter;
import com.example.BackendAmongUs.Tasks.Helper.TaskButton;
import com.example.BackendAmongUs.Tasks.Manager.TaskManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Component
public class EnvironmentInteractionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ReportDeadPlayer reportDeadPlayer;
    @Autowired
    private ActivePlayerManager activePlayerManager;
    @Autowired
    private TeleportTimer timer;
    @Autowired
    private PictureSender pictureSender;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private TaskButton taskButton;
    @Autowired
    private Grid grid;
    @Autowired
    private GameSessionManager gameSessionManager;
    @Autowired
    private GameSessionCommunicator gameSessionCommunicator;

    private KillFunction killFunction;
    private GamingMap gamingMap;
    private MeetingController meetingController;

    public EnvironmentInteractionHandler(@Lazy GamingMap gamingMap, @Lazy MeetingController meetingController, @Lazy KillFunction killFunction) {
        this.gamingMap = gamingMap;
        this.meetingController = meetingController;
        this.killFunction = killFunction;
    }

    private Map<Coordinates, Coordinates> teleportMap = new HashMap<>();
    private int[][] collisionMap; // 2D array to hold collision data
    private Coordinates[][] tiles;
    private Map<String, Coordinates> TilesCoord = new HashMap<>();
    private Map<Integer, Map<String, Coordinates>> gameIdCoords = new HashMap<>();


    @PostConstruct
    public void init() {
        try {
            loadTunnels();
            loadCollisionData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadTunnels() {
        addTunnelPair(new Coordinates(44, 10, 107), new Coordinates(56, 5, 107));
        addTunnelPair(new Coordinates(1, 3, 107), new Coordinates(10, 10, 107));
        addTunnelPair(new Coordinates(27, 46, 107), new Coordinates(31, 58, 107));
        addTunnelPair(new Coordinates(20, 23, 107), new Coordinates(17, 19, 107));
        addTunnelPair(new Coordinates(52, 41, 107), new Coordinates(46, 28, 107));

    }

    private void addTunnelPair(Coordinates tunnelA, Coordinates tunnelB) {
        teleportMap.put(tunnelA, tunnelB);
        teleportMap.put(tunnelB, tunnelA);
    }


    public void loadCollisionData() throws IOException {
        ClassPathResource resource = new ClassPathResource("updatedMap.json");
        ObjectMapper mapper = new ObjectMapper();
        TileMap tileMap = mapper.readValue(resource.getInputStream(), TileMap.class);

        if (tileMap.getLayers() != null && !tileMap.getLayers().isEmpty()) {
            List<Integer> firstLayerData = tileMap.getLayers().get(0).getData();
            int width = 64;
            convertTo2DArray(firstLayerData, width);
        }
    }

    private void convertTo2DArray(List<Integer> data, int width) {
        int height = (int) Math.ceil((double) data.size() / width); // Calculate height properly
        Coordinates[][] tiles = new Coordinates[height][width];
        collisionMap = new int[height][width];

        for (int i = 0; i < data.size(); i++) {
            int x = i % width;
            int y = i / width;
            int tileType = data.get(i);

            collisionMap[x][y] = tileType;
            tiles[x][y] = new Coordinates(x, y, tileType);
        }

        grid.setTiles(tiles);
    }




    public boolean checkIfWalkable(int x, int y, String sessionId) {
        int playerx = activePlayerManager.getPlayer(sessionId).getX();
        int playery = activePlayerManager.getPlayer(sessionId).getY();
        System.out.println(playerx + " " + playery);

        int newx = x / 64;
        int newy = y / 64;
        System.out.println(newx + " " + newy);

        int testx = (playerx + x + 15) / 32;
        int testy = (playery + y + 15) / 32;


        System.out.println(testx + " " + testy);
        System.out.println(collisionMap[testx][testy]);
        if(collisionMap[testx][testy] == 71 || collisionMap[testx][testy] == 74 ||collisionMap[testx][testy] == 147){
            return true;
        }
        return false;
    }


    public void votingTerminal(String sessionId) throws NoSessionIDs {
        int x = activePlayerManager.getPlayer(sessionId).getX() / 32;
        int y = activePlayerManager.getPlayer(sessionId).getY() / 32;

        boolean isNearVotingTile = false;
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (newX >= 0 && newY >= 0 && newX < collisionMap.length && newY < collisionMap[0].length) {
                if (collisionMap[newX][newY] == 74) {
                    isNearVotingTile = true;
                    break;
                }
            }
        }

        if (isNearVotingTile) {
            meetingController.sendVotingInterface(sessionId);
        }
    }


    public void tilesCoord(String sessionId, int updatedX, int updatedY) throws NoSessionIDs, GameIdNotFound, JsonProcessingException {
        Coordinates newCoordinates = new Coordinates(updatedX, updatedY);
        Coordinates actualCoordinates = TilesCoord.get(sessionId);
        String destination = "/queue/Coords";
        String impostersDestination = "/queue/ImposterCoords";

        if (!Objects.equals(newCoordinates, actualCoordinates)) {
            System.out.println(newCoordinates);
            String jsonResponse = objectMapper.writeValueAsString(newCoordinates);
            TilesCoord.put(sessionId, newCoordinates);
            int gameId = gameSessionManager.findGameIdBySessionId(sessionId);

            Map<String, Coordinates> gameCoords = gameIdCoords.get(gameId);
            if (gameCoords != null) {
                String gameCoordsJson = simplifyCoords(gameCoords);
                gameSessionCommunicator.sendToImposters(sessionId, impostersDestination, gameCoordsJson);
            }

            gameSessionCommunicator.sendToOnePlayer(sessionId, destination, jsonResponse);
        }
    }

    public void addtilesCoord(String sessionId, int x, int y, int gameId) throws NoSessionIDs {
        x /= 32;
        y /= 32;
        Coordinates coordinates = new Coordinates(x,y);

        String destination = "/queue/Coords";
        TilesCoord.put(sessionId, coordinates);
        gameIdCoords.put(gameId, TilesCoord);
        String jsonResponse = converting(coordinates);
        gameSessionCommunicator.sendToOnePlayer(sessionId, destination, jsonResponse);
    }

    public void sendInitialCoords(String sessionId, int gameId) throws NoSessionIDs, JsonProcessingException {
        String impostersDestination = "/queue/ImposterCoords";
        Map<String, Coordinates> coords = gameIdCoords.get(gameId);

        if (coords != null) {
            String jsonResponse = simplifyCoords(coords);
            gameSessionCommunicator.sendToImposters(sessionId, impostersDestination, jsonResponse);
        }
    }

    private String converting(Coordinates newCoordinates) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse;
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("x", newCoordinates.getX());
            response.put("y", newCoordinates.getY());

            jsonResponse = objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create JSON response", e);
        }
        return jsonResponse;
    }

    private String simplifyCoords(Map<String, Coordinates> coords) throws JsonProcessingException {
        Map<String, Map<String, Integer>> simplifiedCoords = new HashMap<>();
        for (Map.Entry<String, Coordinates> entry : coords.entrySet()) {
            Map<String, Integer> coordValues = new HashMap<>();
            coordValues.put("x", entry.getValue().getX());
            coordValues.put("y", entry.getValue().getY());
            simplifiedCoords.put(entry.getKey(), coordValues);
        }
        return objectMapper.writeValueAsString(simplifiedCoords);
    }

    public void checkTiles(String sessionId) throws NoSessionIDs, IllegalMove {
        checkforTasks(sessionId);
        checkForGraves(sessionId);
        int x = (activePlayerManager.getPlayer(sessionId).getX())/ 32;
        int y = (activePlayerManager.getPlayer(sessionId).getY())/ 32;


        Player player = activePlayerManager.getPlayer(sessionId);

        if (collisionMap[x][y] == 107 && player.getRole() instanceof Imposter ) {
            if(player.getRole().isCanTeleport()){
                pictureSender.sendTunnelToImposter(sessionId, true);
            } else {
                pictureSender.sendTunnelToImposter(sessionId, false);
            }
        }
        else if(!pictureSender.isSendable(sessionId)) {
            pictureSender.sendTunnelToImposter(sessionId);
        }
    }

    private void checkforTasks(String sessionId){
        if(taskManager.isPlayerNearTask(sessionId)){
            taskButton.sentTaskButton(sessionId, true);
        } else {
            taskButton.sentTaskButton(sessionId, false);
        }
    }

    public void tunnel(String sessionId) throws IllegalMove, NoSessionIDs {
        int x = activePlayerManager.getPlayer(sessionId).getX() / 32;
        int y = activePlayerManager.getPlayer(sessionId).getY() / 32;

        Player imposter = activePlayerManager.getPlayer(sessionId);
        if (collisionMap[x][y] == 107 && imposter.getRole() instanceof Imposter && imposter.getRole().isCanTeleport() && !imposter.getDead()) {
            Coordinates currentLocation = new Coordinates(x, y,107);
            Coordinates destination = teleportMap.get(currentLocation);
            gamingMap.teleport(sessionId, destination.getX() * 32, destination.getY() * 32);
            timer.setTeleportSessionId(sessionId);
        }
    }

    private void checkForGraves(String sessionId) throws NoSessionIDs {
        Player graveDigger = activePlayerManager.getPlayer(sessionId);
        if(checkForGrave(sessionId) && graveDigger.getRole() instanceof Crewmate && !graveDigger.getDead()){
            reportDeadPlayer.sendReportButton(sessionId, true);
        } else {
            reportDeadPlayer.sendReportButton(sessionId, false);
        }
    }

    private boolean checkForGrave(String sessionId) throws NoSessionIDs {
        List<String> sessions = gameSessionManager.retrieveCurrentGameSessionIDS(sessionId);
        List<Player> deadPlayersWithGrave = sessions.stream()
                .map(this::getPlayerFromSession)  // Convert sessionId to Player
                .filter(Player::getDead)           // Check if the player is dead
                .filter(Player::isGrave)         // Check if the player has a grave
                .collect(Collectors.toList());    // Collect the result into a List

        Player currentPlayer = activePlayerManager.getPlayer(sessionId);

        for (Player deadPlayer : deadPlayersWithGrave) {
            double distance = calculateDistance(currentPlayer.getX(), currentPlayer.getY(),
                    deadPlayer.getX(), deadPlayer.getY());
            if (distance <= 45) {
                return true;
            }
        }
       return false;
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private Player getPlayerFromSession(String sessionId) {
        Player player = activePlayerManager.getPlayer(sessionId);
        return player;
    }

    public void removeGameCoords(int gameId) {
        if (gameIdCoords.containsKey(gameId)) {
            Map<String, Coordinates> coordsMap = gameIdCoords.get(gameId);

            coordsMap.clear();

            gameIdCoords.remove(gameId);
        }
    }
}
