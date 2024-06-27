package com.example.BackendAmongUs.Websockets;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionStoreService {

    private ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public void closeSession(String sessionId) throws IOException {
        WebSocketSession session = sessionMap.get(sessionId);
        if (session != null && session.isOpen()) {
            session.close(CloseStatus.NORMAL);
            unregisterSession(sessionId);
        }
    }

    private void unregisterSession(String sessionId) {
        sessionMap.remove(sessionId);
    }



    // Other methods as before
}
