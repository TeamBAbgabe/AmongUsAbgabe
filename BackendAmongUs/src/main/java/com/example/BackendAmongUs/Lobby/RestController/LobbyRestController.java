package com.example.BackendAmongUs.Lobby.RestController;

import com.example.BackendAmongUs.Bot.BotJoining;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Lobby.Exceptions.GameCouldntStart;
import com.example.BackendAmongUs.Lobby.Exceptions.LobbyAlreadyExists;
import com.example.BackendAmongUs.Lobby.Exceptions.LobbyDoesntExist;
import com.example.BackendAmongUs.Lobby.Helper.LobbyConnectingInformation;
import com.example.BackendAmongUs.Lobby.Helper.StartLobby;
import com.example.BackendAmongUs.Lobby.LobbyManager;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class LobbyRestController {
    @Autowired
    private LobbyManager lobbyManager;
    @Autowired
    private BotJoining botJoining;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/lobby")
    @SendTo("/topic/lobby")
    public void processMovement(LobbyConnectingInformation lobbyConnectingInformation, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Received LobbyConnectingInformation: " + lobbyConnectingInformation.getLobbyId());
        try {
            String sessionId = headerAccessor.getUser().getName();
            lobbyManager.setPlayers(lobbyConnectingInformation.getUserName(), sessionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/create")
    public void createLobby(LobbyConnectingInformation lobbyConnectingInformation, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Received LobbyConnectingInformation: " + lobbyConnectingInformation);
        try {
            String sessionId = headerAccessor.getUser().getName();
            System.out.println("Received lobbyId: " + lobbyConnectingInformation.getLobbyId());
            lobbyManager.createCustomGame(lobbyConnectingInformation.getLobbyId(), lobbyConnectingInformation.getUserName(), sessionId);
        } catch (LobbyAlreadyExists e) {
            e.printStackTrace();
        } catch (NoSessionIDs e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("/join")
    public void joinCustomLobby(LobbyConnectingInformation lobbyConnectingInformation, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getUser().getName();
            lobbyManager.joinCustomLobby(lobbyConnectingInformation.getLobbyId(),lobbyConnectingInformation.getUserName(), sessionId);
        } catch (LobbyDoesntExist e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/back")
    public void backToLobbys(SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getUser().getName();
            lobbyManager.removePlayer(sessionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/bot")
    public void joinBot() {
        try {
            lobbyManager.setPlayers("Bot", botJoining.joinRandomLobby());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/clear")
    public void clearBots() {
        try {
            lobbyManager.setPlayers("Bot", botJoining.joinRandomLobby());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/gameStart")
    public void gameStart(@RequestBody StartLobby startLobby) throws GameCouldntStart, NoSessionIDs, IllegalMove {
        System.out.println("Received LobbyConnectingInformation: " + startLobby.getLobbyId());
        lobbyManager.startTheGame(startLobby.getLobbyId(), startLobby.getImposterAmount(), startLobby.getLobbyType());
    }

    @MessageMapping("/changeCharacter")
    public void changeCharacter(LobbyConnectingInformation lobbyConnectingInformation, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getUser().getName();
            lobbyManager.changeCharacter(lobbyConnectingInformation.getLobbyId(), sessionId, lobbyConnectingInformation.getCharacter());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
