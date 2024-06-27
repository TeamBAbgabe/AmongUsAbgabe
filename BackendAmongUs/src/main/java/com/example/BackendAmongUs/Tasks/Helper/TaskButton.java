package com.example.BackendAmongUs.Tasks.Helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TaskButton {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private String  tunnelPossible="https://i.imgur.com/LFcxEOq.png";
    private String  tunnelNotPossible = "";
    private Map<String, Boolean> isSendable = new HashMap<>();

    public void setIsSendable(String SessionId) {
        isSendable.put(SessionId, false);
    }

    public void sentTaskButton(String sessionId, boolean status) {
        String destination = "/queue/taskButton";
        String imageUrl = status ? tunnelPossible : tunnelNotPossible;
        messagingTemplate.convertAndSendToUser(sessionId, destination, imageUrl);
        isSendable.put(sessionId, false);
    }

    public boolean isSendable(String sessionId){
        return isSendable.get(sessionId);
    }



}

