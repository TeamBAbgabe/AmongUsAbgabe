package com.example.BackendAmongUs.Websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    private final SessionStoreService sessionStoreService;

    @Autowired
    public CustomHandshakeHandler(SessionStoreService sessionStoreService) {
        this.sessionStoreService = sessionStoreService;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String sessionId = UUID.randomUUID().toString();
        System.out.println("Generated sessionId: " + sessionId);
        return () -> sessionId;
    }

}
