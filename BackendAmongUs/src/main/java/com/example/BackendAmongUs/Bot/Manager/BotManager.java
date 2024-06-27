package com.example.BackendAmongUs.Bot.Manager;

import com.example.BackendAmongUs.Bot.BotJoining;
import com.example.BackendAmongUs.Bot.Movement.Moving;
import com.example.BackendAmongUs.Bot.Movement.Pathfinding;
import com.example.BackendAmongUs.DatenBank.Pojo.User;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Lobby.Helper.UserSession;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@Setter
public class BotManager {

    @Autowired
    private Pathfinding pathfinding;

    private Moving moving;

    public BotManager(@Lazy Moving moving){
        this.moving = moving;
    }

    private Map<Integer, List<String>> botList = new HashMap<>();

    public void setBotList(int gameId, List<UserSession> users) {
        List<String> bots = new ArrayList<>();
        for(UserSession user : users){
            if(user.getUserId().equals("Bot")){
                bots.add(user.getSessionId());
            }
        }
        botList.put(gameId, bots);
    }

    public void startBots(int gameId) throws IllegalMove, NoSessionIDs {
        int holdDuration = 5000;
        System.out.println("Damn game is game");
        pathfinding.getCoordinates(botList.get(gameId));
        //moving.movement(botList.get(gameId), holdDuration);
    }




}
