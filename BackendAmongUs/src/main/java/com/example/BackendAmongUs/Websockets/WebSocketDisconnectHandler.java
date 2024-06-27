package com.example.BackendAmongUs.Websockets;

import com.example.BackendAmongUs.Disconnections.HandleDisconnections;
import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Lobby.LobbyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.IOException;

@Component
public class WebSocketDisconnectHandler implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    private HandleDisconnections handleDisconnections;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String sessionID = event.getUser().getName();
        System.out.println("Websocket session disconnected: " + sessionID);
        try {
            handleDisconnections.removeDisconnections(sessionID);
        } catch (GameIdNotFound | NoSessionIDs | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
