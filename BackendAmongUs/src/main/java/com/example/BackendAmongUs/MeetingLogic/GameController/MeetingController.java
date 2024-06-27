package com.example.BackendAmongUs.MeetingLogic.GameController;

import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Map.Environment.EnvironmentInteractionHandler;
import com.example.BackendAmongUs.Map.GamingMap;
import com.example.BackendAmongUs.MeetingLogic.Exceptions.MeetingCouldntStart;
import com.example.BackendAmongUs.MeetingLogic.Exceptions.VotingNotPossible;
import com.example.BackendAmongUs.MeetingLogic.GameServiceHandler;
import com.example.BackendAmongUs.MeetingLogic.Helpers.VoteRequest;
import com.example.BackendAmongUs.MeetingLogic.MeetingAndVoting;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.MeetingLogic.TimerManager;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.Player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MeetingController {

    @Autowired
    private GameServiceHandler gameServiceHandler;
    @Autowired
    private ActivePlayerManager player;
    @Autowired
    private MeetingAndVoting meetingAndVoting;
    @Autowired
    private TimerManager timerManager;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private GameSessionManager gameSession;
    @Autowired
    private ActivePlayerManager activePlayerManager = new ActivePlayerManager();
    @Autowired
    private EnvironmentInteractionHandler environmentInteractionHandler;

    @CrossOrigin(origins = "*")
    @PostMapping("/voting")
    public ResponseEntity voting(@RequestBody VoteRequest voteRequest) throws VotingNotPossible {
       //Logger for all System outs
        try {
            int gameId = gameSession.findGameIdBySessionId(voteRequest.getSessionId());
            if(!gameSession.canGameHaveMeeting(gameId) ){
                Player playervoting = player.getPlayer(voteRequest.getSessionId());
                Player playerSuspect = player.getPlayer(voteRequest.getSuspectId());
                System.out.println("Voter" + playervoting + " " + " suspect" + playerSuspect);
                meetingAndVoting.setVotes(playervoting, playerSuspect);
            } else {
                throw new VotingNotPossible("The Timer is still running");
            }
        } catch(Exception e){
            throw new VotingNotPossible("The Voting wasnt possible");
        }
        return ResponseEntity.ok().build();
    }


    public void sendVotingInterface(String sessionId){
        try {
            int gameId = gameSession.findGameIdBySessionId(sessionId);
            Player player = activePlayerManager.getPlayer(sessionId);
            if(!gameSession.canGameHaveMeeting(gameId) && !player.getDead() ){
                timerManager.startTrueTimer(gameId);
                System.out.println("Meeting is called");
                gameServiceHandler.startMeeting(sessionId);
            }
        } catch (Exception e){
            e.getStackTrace();
        }
    }

    @MessageMapping("/reportDeadPlayer")
    public void reportDeadPlayer(SimpMessageHeaderAccessor headerAccessor) throws MeetingCouldntStart {
        try {
            String sessionId = headerAccessor.getUser().getName();
            timerManager.reportTimer(gameSession.findGameIdBySessionId(sessionId));
            gameServiceHandler.startMeeting(sessionId);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }



}



