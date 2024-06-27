package com.example.BackendAmongUs.RandomType;

import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Player.Crewmate;
import com.example.BackendAmongUs.Player.Imposter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssignTeam {

    @Autowired
    private ActivePlayerManager activePlayerManager = new ActivePlayerManager();

    public void randomRoleAssigned(List<String> sessions, int imposter, String lobbyType) {
        int imposterAmount;

        if(lobbyType.equals("Public")) {
            imposterAmount = calculateImposterAmount(sessions.size());
        } else {
            imposterAmount = imposter;
        }

        Collections.shuffle(sessions);

        for (int i = 0; i < imposterAmount; i++) {
            Player player = activePlayerManager.getPlayer(sessions.get(i));
            player.setRole(new Imposter());

            if(player.getUsername().equals("Bot")){
                player.setRole(new Crewmate());
            }
        }

        for (int i = imposterAmount; i < sessions.size(); i++) {
            Player player = activePlayerManager.getPlayer(sessions.get(i));
            player.setRole(new Crewmate());

            if(player.getUsername().equals("Bot")){
                player.setRole(new Crewmate());
            }
        }
    }

    private int calculateImposterAmount(int playerCount) {
        if (playerCount <= 3) {
            return 1;
        } else if (playerCount <= 6) {
            return 2;
        } else {
            return (playerCount - 4) / 3 + 2;
        }
    }
}
