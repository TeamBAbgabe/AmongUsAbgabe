package com.example.BackendAmongUs.GameSession.Event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GameStartEvent extends ApplicationEvent {
    private String message;
    private String sessionId;
    private int gameId;

    public GameStartEvent(Object source, String message, String sessionId, int gameId) {
        super(source);
        this.message = message;
        this.sessionId = sessionId;
        this.gameId = gameId;
    }


}
