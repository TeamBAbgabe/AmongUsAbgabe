package com.example.BackendAmongUs.MeetingLogic;

import com.example.BackendAmongUs.MeetingLogic.Events.TimerEvent;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import java.util.Timer;
import java.util.TimerTask;

@Getter
@Component
public class TimerManager {

    @Autowired
    private final MeetingAndVoting meetingAndVoting;
    private boolean timerActive = false;
    private Timer timer = new Timer();
    private TimerTask activeTask;
    private boolean currentState = true;
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private GameSessionManager gameSession;

    public TimerManager(MeetingAndVoting meetingAndVoting, ApplicationEventPublisher eventPublisher){
        this.meetingAndVoting = meetingAndVoting;
        this.eventPublisher = eventPublisher;
    }

    public void startTrueTimer(int gameId) {
        if (!gameSession.canGameHaveMeeting(gameId)) {

            gameSession.changeMeetingStatus(gameId, false);
            System.out.println("Voting session has started" + gameId);
            activeTask = new TimerTask() {
                public void run() {
                    try {
                        gameSession.changeMeetingStatus(gameId, true);
                        System.out.println("Voting session has ended, cooldown started");
                        eventPublisher.publishEvent(new TimerEvent(this, "Kicking", gameId));
                        startFalseTimer(gameId);
                    } catch (Exception e) {
                        startFalseTimer(gameId);
                    }
                }
            };
            timer.schedule(activeTask, 30000);
        }
    }

    private void startFalseTimer(int gameId) {
        if (gameSession.canGameHaveMeeting(gameId)) {
            gameSession.changeMeetingStatus(gameId, true);
            activeTask = new TimerTask() {
                public void run() {
                    try {
                        System.out.println("Cooldown has ended, voting is enabled again");
                        gameSession.changeMeetingStatus(gameId, false);
                    } catch (Exception e){
                        e.getStackTrace();
                    }
                }
            };
            timer.schedule(activeTask, 30000);
        }
    }

    public void reportTimer(int gameId) {
            System.out.println("Voting session has started" + gameId);
            activeTask = new TimerTask() {
                public void run() {
                    try {
                        System.out.println("Voting session has ended, cooldown started");
                        eventPublisher.publishEvent(new TimerEvent(this, "Kicking", gameId));
                        startFalseTimer(gameId);
                    } catch (Exception e) {
                        startFalseTimer(gameId);
                    }
                }
            };
            timer.schedule(activeTask, 30000);
        }
}
