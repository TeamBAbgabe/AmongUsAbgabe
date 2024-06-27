package com.example.BackendAmongUs.Map.PictureSender;

import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PictureSender {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private String  tunnelPossible="https://i.imgur.com/R9pwwP3.png";
    private String  tunnelNotPossible = "https://i.imgur.com/934x18c.png";
    private Map<String, Boolean> isSendable = new HashMap<>();

    public void setIsSendable(String SessionId) {
        isSendable.put(SessionId, false);
    }

    public void sendTunnelToImposter(String sessionId, boolean status) {
        String destination = "/queue/tunnel";
        String imageUrl = status ? tunnelPossible : tunnelNotPossible;
        messagingTemplate.convertAndSendToUser(sessionId, destination, imageUrl);
        isSendable.put(sessionId, false);
    }
    public void sendTunnelToImposter(String sessionId) {
        String destination = "/queue/tunnel";

        messagingTemplate.convertAndSendToUser(sessionId, destination, "");
        isSendable.put(sessionId, true);
    }

    public boolean isSendable(String sessionId){
        return isSendable.get(sessionId);
    }



}
