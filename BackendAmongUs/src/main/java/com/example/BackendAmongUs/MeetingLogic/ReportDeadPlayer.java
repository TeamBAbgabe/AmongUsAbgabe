package com.example.BackendAmongUs.MeetingLogic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class ReportDeadPlayer {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private String  reportDeadPlayer="https://i.imgur.com/ryDkw9I.png";
    private Map<String, Boolean> isSendable = new HashMap<>();

    public void sendReportButton(String sessionId, boolean status) {
        String destination = "/queue/reportDeadPlayer";
        String imageUrl = status ? reportDeadPlayer : "";
        messagingTemplate.convertAndSendToUser(sessionId, destination, imageUrl);
        if(status) {
            isSendable.put(sessionId, false);
        } else {
            isSendable.put(sessionId, true);
        }
    }


}
