package com.example.BackendAmongUs.Map.Environment.Coords;

import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Map.Environment.EnvironmentInteractionHandler;
import com.example.BackendAmongUs.Map.Exceptions.IllegalMove;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;
@Component
public class TeleportTimer {
    @Autowired
    private ActivePlayerManager activePlayerManager;
    private Timer timer = new Timer();
    private TimerTask activeTask;

    private EnvironmentInteractionHandler environmentInteractionHandler;

    public TeleportTimer(@Lazy EnvironmentInteractionHandler environmentInteractionHandler){
        this.environmentInteractionHandler = environmentInteractionHandler;
    }


    public void setTeleportSessionId(String teleportSessionId) throws NoSessionIDs, IllegalMove {
        activePlayerManager.getPlayer(teleportSessionId).getRole().setCanTeleport(false);
        environmentInteractionHandler.checkTiles(teleportSessionId);
        activeTask = new TimerTask() {
            public void run() {
                try {
                    activePlayerManager.getPlayer(teleportSessionId).getRole().setCanTeleport(true);
                    environmentInteractionHandler.checkTiles(teleportSessionId);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        };
        timer.schedule(activeTask, 30000);
    }
}
