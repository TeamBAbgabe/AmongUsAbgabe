package com.example.BackendAmongUs.KillFunction;

import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Player.Imposter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class KillCoolDown {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ActivePlayerManager activePlayerManager;
    private Timer timer = new Timer();
    private TimerTask activeTask;
    private String activeKillButton = "https://i.imgur.com/sKkg1T8.png";
    private String inactiveKillButton = "https://i.imgur.com/29lbOu1.png";

    public void killingTimer(String killerSessionId) {
        activePlayerManager.getPlayer(killerSessionId).getRole().setCanKill(false);
        System.out.println(activePlayerManager.getPlayer(killerSessionId).getRole().isCanKill());
        messagingTemplate.convertAndSendToUser(killerSessionId, "/queue/killButton", inactiveKillButton);

        activeTask = new TimerTask() {
                public void run() {
                    try {
                        activePlayerManager.getPlayer(killerSessionId).getRole().setCanKill(true);
                        messagingTemplate.convertAndSendToUser(killerSessionId, "/queue/killButton", activeKillButton);
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            };
            timer.schedule(activeTask, 15000);
        }

    public void initialKillTimer(List<String> imposter) {
        for(String s : imposter){
            Player player = activePlayerManager.getPlayer(s);
            if(player.getRole() instanceof Imposter){
                player.getRole().setCanKill(false);
                messagingTemplate.convertAndSendToUser(s, "/queue/killButton", inactiveKillButton);
            }
        }
        activeTask = new TimerTask() {
            public void run() {
                try {
                    for(String s: imposter){
                        activePlayerManager.getPlayer(s).getRole().setCanKill(true);
                        messagingTemplate.convertAndSendToUser(s, "/queue/killButton", activeKillButton);
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        };
        timer.schedule(activeTask, 15000);
    }

}


