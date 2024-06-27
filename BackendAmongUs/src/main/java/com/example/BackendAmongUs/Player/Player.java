package com.example.BackendAmongUs.Player;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Player {
    private int x;
    private int y;
    private String session_id;
    private boolean dead = false;
    private String username;
    private String avatar;
    private String color;
    private Role role;
    private String character;
    private boolean grave = false;

    public Player(int x, int y, String session_id, String username, String avatar, String color, String character) {
        this.x = x;
        this.y = y;
        this.session_id = session_id;
        this.username = username;
        this.avatar = avatar;
        this.color = color;
        this.character = character;
    }

    public boolean getDead(){
        return this.dead;
    }


}
