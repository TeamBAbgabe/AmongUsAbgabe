package com.example.BackendAmongUs.Lobby.Helper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSession {
    private String userId;
    private String avatarUrl;
    private String sessionId;
    private String color;
    private int lobbyId;
    private String character;

    // Constructor, getters, and setters
    public UserSession(String userId, String avatarUrl, String sessionId, String color, int lobbyId, String character) {
        this.userId = userId;
        this.avatarUrl = avatarUrl;
        this.sessionId = sessionId;
        this.color = color;
        this.lobbyId = lobbyId;
        this.character = character;
    }
}