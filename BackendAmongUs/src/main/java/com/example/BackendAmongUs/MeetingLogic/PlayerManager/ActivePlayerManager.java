package com.example.BackendAmongUs.MeetingLogic.PlayerManager;

import com.example.BackendAmongUs.Player.Player;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ActivePlayerManager {

    private List<Player> currentPlayers = new ArrayList<>();


    public Player getPlayer(String sessionID) {
        Optional<Player> playerPosition = currentPlayers.stream()
                .filter(p -> p.getSession_id().equals(sessionID))
                .findFirst();

        return playerPosition.orElse(null);
    }

    public List<Player> getAllPlayer(){
        return new ArrayList<>(currentPlayers);
    }

    public void setCurrentPlayers(Player player) {
        this.currentPlayers.add(player);
    }

    public void deletePlayer(Player player){
        this.currentPlayers.remove(player);
    }


}


