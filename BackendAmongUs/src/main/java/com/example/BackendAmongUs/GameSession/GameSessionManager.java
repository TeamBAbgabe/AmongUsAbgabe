package com.example.BackendAmongUs.GameSession;


import com.example.BackendAmongUs.Bot.Manager.BotManager;
import com.example.BackendAmongUs.GameSession.Event.GameStartEvent;
import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionRetrievable;
import com.example.BackendAmongUs.KillFunction.KillCoolDown;
import com.example.BackendAmongUs.Lobby.Helper.UserSession;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.example.BackendAmongUs.Map.PictureSender.PictureSender;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.RandomType.AssignTeam;
import com.example.BackendAmongUs.Tasks.Manager.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameSessionManager {


    @Autowired
    private KillCoolDown killCoolDown;
    @Autowired
    private ActivePlayerManager activePlayerManager = new ActivePlayerManager();
    @Autowired
    private AssignTeam assignTeam;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private PictureSender pictureSender;
    @Autowired
    private BotManager botManager;
    private Map<Integer, List<String>> gameSessions = new ConcurrentHashMap<>();
    private Map<Integer, Boolean> didGameHaveMeeting = new HashMap<>();
    private List<String> sessions = new ArrayList<>();
    private Random random = new Random();

    public void addSession(String sessionId, String username, String avatar, String color, String character) throws NoSessionIDs {
        if (!sessions.contains(sessionId)) {
            sessions.add(sessionId);
            createPlayer(sessionId, username, avatar, color, character);
        }
    }



    private void createPlayer(String sessionId, String username, String avatar, String color, String character) throws NoSessionIDs {
        int[] spawnLocation = getRandomSpawnLocation();
        activePlayerManager.setCurrentPlayers(new Player(spawnLocation[0], spawnLocation[1], sessionId, username, avatar, color, character));
    }

    private int[] getRandomSpawnLocation() {
        int[][] spawnArea = {
                {28, 28}, {28, 36}, {36, 29}, {36, 36}
        };
        int[][] restrictedArea = {
                {31, 31}, {31, 32}, {31, 33}, {32, 31}, {32, 32}, {32, 33}, {33, 31}, {33, 32}, {33, 33}
        };

        // Convert restrictedArea to pixel coordinates for easier comparison
        Set<String> restrictedPixels = new HashSet<>();
        for (int[] coord : restrictedArea) {
            restrictedPixels.add(coord[0] * 32 + "," + coord[1] * 32);
        }

        int spawnX, spawnY;
        do {
            int[] randomTile = spawnArea[random.nextInt(spawnArea.length)];
            spawnX = randomTile[0] * 32;
            spawnY = randomTile[1] * 32;
        } while (restrictedPixels.contains(spawnX + "," + spawnY));

        return new int[]{spawnX, spawnY};
    }

    public void startingNewGame(int currentGameId, List<UserSession> userSessions, int imposterAmount, String lobbyType) throws NoSessionIDs, IllegalMove {
        for(UserSession user: userSessions){
            addSession(user.getSessionId(), user.getUserId(), user.getAvatarUrl(), user.getColor(), user.getCharacter());
            pictureSender.setIsSendable(user.getSessionId());
        }

        botManager.setBotList(currentGameId,userSessions);
        assignTeam.randomRoleAssigned(sessions, imposterAmount, lobbyType);
        gameSessions.put(currentGameId, sessions);
        didGameHaveMeeting.put(currentGameId, false);
        eventPublisher.publishEvent(new GameStartEvent(this, "StartGame", sessions.get(0), currentGameId));
        taskManager.createTasks(sessions, currentGameId);
        taskManager.sendTasks(sessions);
        botManager.startBots(currentGameId);
        killCoolDown.initialKillTimer(retrieveCurrentGameSessionIDSByGameID(currentGameId));
        sessions = new ArrayList<>();
    }

    public boolean canGameHaveMeeting(int gameId){
        if(didGameHaveMeeting.get(gameId)){
            return true;
        }
        else return false;
    }
    public void changeMeetingStatus(int gameId, boolean meetingState){
        didGameHaveMeeting.put(gameId, meetingState);
    }

    public List<String> retrieveCurrentGameSessionIDS(String sessionid) throws NoSessionIDs {
        for (Map.Entry<Integer, List<String>> entry : gameSessions.entrySet()) {
            List<String> sessionList = entry.getValue();
            if (sessionList.contains(sessionid)) {
                return sessionList;
            }
        }
        throw new NoSessionIDs("Couldn't retrieve the current game SessionIds");
    }

    public List<String> retrieveCurrentGameSessionIDSByGameID(int gameId) throws NoSessionIDs {
        return gameSessions.get(gameId);
    }

    public String retrieveOneSessionIDByGameId(int gameId) throws NoSessionRetrievable {
        List<String> sessions = gameSessions.get(gameId);
        if (sessions != null && !sessions.isEmpty()) {
            return sessions.get(0);
        }
        throw new NoSessionRetrievable("With the given gameID there couldnt be a SessionId retrieved");
    }
    public Integer findGameIdBySessionId(String sessionId) throws GameIdNotFound {
        try {
            for (Map.Entry<Integer, List<String>> entry : gameSessions.entrySet()) {
                List<String> sessions = entry.getValue();
                if (sessions.contains(sessionId)) {
                    return entry.getKey();
                }
            }
        } catch (Exception e){
            e.getStackTrace();
        }
        throw new GameIdNotFound("With the given SessionId there was no GameId found");
    }

    public void deleteSessionsOutOfGame(int gameId){
        gameSessions.remove(gameId);
    }

/*
    private List<String> alivePlayers(String sessionId){
        List<String> alivePlayers = new ArrayList<>();
        List<String> sessions = retrieveCurrentGameSessionIDS(sessionId);
        for(String s : sessions){
            System.out.println(" DEAD OR NOT"+ activePlayerManager.getPlayer(s).isIsdead());
            if(!activePlayerManager.getPlayer(s).isIsdead()){
                System.out.println("gameaisfsadfasdf" + gameSessions);
                alivePlayers.add(s);
            } else {
                System.out.println("Player is dead");
            }
        }
        return alivePlayers;
    }


 */
}

