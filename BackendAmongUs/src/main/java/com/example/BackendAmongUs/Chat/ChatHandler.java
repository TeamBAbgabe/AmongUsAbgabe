package com.example.BackendAmongUs.Chat;

import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChatHandler {
    @Autowired
    private GameSessionManager gameSessionManager;
    @Autowired
    private ActivePlayerManager activePlayerManager;
    @Autowired
    private GameSessionCommunicator gameSessionCommunicator;

    public void chatting(String sessionId, String message) throws GameIdNotFound, JsonProcessingException, NoSessionIDs {
        String username = activePlayerManager.getPlayer(sessionId).getUsername();

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("username", username);
        dataMap.put("message", message);

        ObjectMapper mapper = new ObjectMapper();
        String jsonData = mapper.writeValueAsString(dataMap);
        System.out.println(jsonData);
        String destination = "/queue/Chat";
        gameSessionCommunicator.sendMessageToSession(sessionId, jsonData, destination);

    }
}
