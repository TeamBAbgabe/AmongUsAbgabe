package com.example.BackendAmongUs.Lobby;

import com.example.BackendAmongUs.DatenBank.Pojo.User;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.Lobby.Avatars.ListOfAvatars;
import com.example.BackendAmongUs.Lobby.Avatars.RandomColor;
import com.example.BackendAmongUs.Lobby.Exceptions.GameCouldntStart;
import com.example.BackendAmongUs.Lobby.Exceptions.LobbyAlreadyExists;
import com.example.BackendAmongUs.Lobby.Exceptions.LobbyDoesntExist;
import com.example.BackendAmongUs.Lobby.Helper.UserSession;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Service
public class LobbyManager {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private GameSessionManager gameSession;
    @Autowired
    private ListOfAvatars listOfAvatars;
    @Autowired
    private RandomColor randomColor;
    @Autowired
    private GameSessionCommunicator gameSessionCommunicator;

    private int gameId = 0;
    private Map<Integer, List<UserSession>> mutlitpleLobbys = new HashMap<>();
    private List<UserSession> myUsers = new ArrayList<>();
    private List<UserSession> publicGame = new ArrayList<>();
    private String character = "M1";
    private int publicGameId;


    public void setPlayers(String username, String sessionId) {
        if(!username.equals("Bot") && doesLobbyExist(gameId) && gameId != publicGameId){
            gameId++;
        }
        System.out.println("my game id is already " + gameId);
        String avatar = listOfAvatars.getRandomAvatar();
        String color = randomColor.randomColor(gameId);
        publicGame.add(new UserSession(username, avatar, sessionId ,color, gameId, character));
        ObjectMapper mapper = new ObjectMapper();
        mutlitpleLobbys.put(gameId, publicGame);
        publicGameId = gameId;
        System.out.println("Where is my user" + publicGame);
        String jsonOutput = "{}";
        try {
            jsonOutput = mapper.writeValueAsString(publicGame);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(jsonOutput);
        for(UserSession u : publicGame) {
            String session = u.getSessionId();
            messagingTemplate.convertAndSendToUser(session, "/queue/Lobby",jsonOutput);
        }
    }

    public void removeBots(int publicG) {
        List<UserSession> botLobby = mutlitpleLobbys.get(publicG);
        Iterator<UserSession> iterator = botLobby.iterator();
        randomColor.resetColors(publicG);
        while (iterator.hasNext()) {
            UserSession user = iterator.next();
            if (user.getUserId().equals("Bot")) {
                iterator.remove();
            }
        }

    }



    public void startTheGame(int lobbyId, int imposterAmount, String lobbyType) throws GameCouldntStart, NoSessionIDs, IllegalMove {
        System.out.println("The lobby" + mutlitpleLobbys);
        gameSession.startingNewGame(lobbyId, mutlitpleLobbys.get(lobbyId), imposterAmount, lobbyType);
        gameSessionCommunicator.sendStartGame(mutlitpleLobbys.get(lobbyId), "/queue/startGame");
        mutlitpleLobbys.get(lobbyId).clear();
        mutlitpleLobbys.remove(lobbyId);
        randomColor.resetColors(lobbyId);
        publicGameId = -1;
    }

    public void createCustomGame(int lobbyId, String username, String sessionId) throws NoSessionIDs, LobbyAlreadyExists {
        System.out.println(lobbyId);
        if(doesLobbyExist(lobbyId) || lobbyId == publicGameId){
            Map<String, String> message = new HashMap<>();
            message.put("message", "Lobby exists already");
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/customLobby",message);
            throw new LobbyAlreadyExists("This Lobby already exists");
        }

        String avatar = listOfAvatars.getRandomAvatar();
        String color = randomColor.randomColor(lobbyId);
        myUsers = new ArrayList<>();
        myUsers.add(new UserSession(username, avatar, sessionId ,color, lobbyId, character));
        mutlitpleLobbys.put(lobbyId, myUsers);
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(myUsers + "this is what i need");
        String jsonOutput = "{}";
        try {
            jsonOutput = mapper.writeValueAsString(myUsers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(jsonOutput);
        String destination = "/queue/customLobby";
        messagingTemplate.convertAndSendToUser(sessionId, destination,jsonOutput);
    }

    public void joinCustomLobby(int lobbyId, String username, String sessionId) throws LobbyDoesntExist {
        if(!mutlitpleLobbys.containsKey(lobbyId) || lobbyId == publicGameId){
            Map<String, String> message = new HashMap<>();
            if(lobbyId == publicGameId){
                message.put("message", "This is a public LobbyID");

            } else {
                message.put("message", "LobbyId doesnt exist");
            }
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/joinCustomLobby", message);
            throw new LobbyDoesntExist("This lobby doesnt exist");
        }
        String avatar = listOfAvatars.getRandomAvatar();
        String color = randomColor.randomColor(lobbyId);
        myUsers = mutlitpleLobbys.get(lobbyId);
        myUsers.add(new UserSession(username, avatar, sessionId ,color, lobbyId, character));
        mutlitpleLobbys.put(lobbyId, myUsers);
        ObjectMapper mapper = new ObjectMapper();

        String jsonOutput = "{}";
        try {
            jsonOutput = mapper.writeValueAsString(myUsers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String destination = "/queue/joinCustomLobby";
        for(UserSession u : myUsers) {
            String session = u.getSessionId();
            messagingTemplate.convertAndSendToUser(session, destination,jsonOutput);
        }
    }

    private boolean doesLobbyExist(int lobbyId){
        if(mutlitpleLobbys.containsKey(lobbyId)){
            return true;
        }
        else return false;
    }


    public void removePlayer(String username) {
        Predicate<UserSession> byName = userSession -> userSession.getSessionId().equals(username);
        UserSession targetUser = null;
        int lobbyId = -1;
        String sendingLobby = "";

        // Find the lobby containing the user
        for (Map.Entry<Integer, List<UserSession>> entry : mutlitpleLobbys.entrySet()) {
            List<UserSession> lobby = entry.getValue();
            if (lobby.stream().anyMatch(byName)) {
                targetUser = lobby.stream().filter(byName).findFirst().orElse(null);
                lobbyId = entry.getKey();
                break;
            }
        }

        // If user is not found in any lobby, return
        if (targetUser == null) {
            System.out.println("User not found: " + username);
            return;
        }

        // Determine which lobby the user is in (public or private)
        if (publicGame.contains(targetUser)) {
            sendingLobby = "publicgames";
            publicGame.remove(targetUser);
        } else if (myUsers.contains(targetUser)) {
            sendingLobby = "myUsers";
            myUsers.remove(targetUser);
        }

        String color = targetUser.getColor();
        System.out.println("Removing user: " + username + ", Color: " + color);

        // Remove user from the lobby and handle empty lobbies
        List<UserSession> lobby = mutlitpleLobbys.get(lobbyId);
        if (lobby != null) {
            lobby.remove(targetUser);
            randomColor.removeUsedColor(lobbyId, color);
            if (lobby.isEmpty()) {
                System.out.println("Lobby with ID: " + lobbyId + " is now empty and will be removed.");
                if(sendingLobby.equals("publicgames")){
                    publicGameId = -1;
                }
                mutlitpleLobbys.remove(lobbyId);
                randomColor.resetColors(lobbyId);
            } else {
                boolean onlyBotsLeft = lobby.stream().allMatch(userSession -> userSession.getUserId().equals("Bot"));
                if (onlyBotsLeft) {
                    System.out.println("Lobby with ID: " + lobbyId + " contains only bots and will be removed.");
                    removeBots(lobbyId);
                    mutlitpleLobbys.remove(lobbyId);
                    System.out.println("LOBBBBBBY" + mutlitpleLobbys);
                } else {
                    String destination = "/queue/joinCustomLobby";
                    List<UserSession> targetList = sendingLobby.equals("publicgames") ? publicGame : myUsers;
                    for (UserSession u : targetList) {
                        String session = u.getSessionId();
                        messagingTemplate.convertAndSendToUser(session, destination, targetList);
                    }
                }
            }
        }

        System.out.println("User " + username + " removed from lobby ID: " + lobbyId + " with color: " + color);
        System.out.println("The player left: " + username + " " + mutlitpleLobbys);
    }




    public void changeCharacter(int lobbyId, String sessionId, String character){
        myUsers = mutlitpleLobbys.get(lobbyId);
        for(UserSession user : myUsers){
            if(user.getSessionId().equals(sessionId)) {
                user.setCharacter(character);
            }
        }
    }

}

