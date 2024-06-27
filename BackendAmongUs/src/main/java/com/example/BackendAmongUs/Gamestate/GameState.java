package com.example.BackendAmongUs.Gamestate;

import com.example.BackendAmongUs.DatenBank.DatenBankService;
import com.example.BackendAmongUs.Disconnections.KickBots;
import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.GameSession.GameSessionCommunicator;
import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.Map.Environment.EnvironmentInteractionHandler;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Player.Crewmate;
import com.example.BackendAmongUs.Player.Imposter;
import com.example.BackendAmongUs.Player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameState {

    @Autowired
    private ActivePlayerManager activePlayerManager;
    @Autowired
    private GameSessionManager gameSessionManager;
    @Autowired
    private GameSessionCommunicator gameSessionCommunicator;
    @Autowired
    private DatenBankService datenBankService;
    @Autowired
    private KickBots kickBots;
    @Autowired
    private EnvironmentInteractionHandler environmentInteractionHandler;


    public void checkWinState(int gameId) throws NoSessionIDs, GameIdNotFound {
        List<String> usersInGame = gameSessionManager.retrieveCurrentGameSessionIDSByGameID(gameId);
        int aliveImposters = 0;
        int aliveCrewmates = 0;
        for(String s : usersInGame){
            Player player = activePlayerManager.getPlayer(s);
            if(player.getRole() instanceof Imposter && !player.getDead() && player.getUsername() != "Bot"){
                aliveImposters++;
            }
            else if(player.getRole() instanceof Crewmate && !player.getDead() && player.getUsername() != "Bot"){
                aliveCrewmates++;
            }
        }
        determineWinner(aliveImposters, aliveCrewmates, gameId);

    }

    private void determineWinner(int aliveImposters, int aliveCrewmates, int gameId) throws NoSessionIDs, GameIdNotFound {
        String destination = "/queue/Endgame";
        if (aliveImposters == 0) {
            System.out.println("Crewmates won");
            saveWinStats(gameId, false);
            gameSessionCommunicator.sendEndGame(gameId, destination, false); // false indicating crewmates won
        } else if (aliveCrewmates < aliveImposters) {
            System.out.println("Imposters won");
            saveWinStats(gameId, true);
            gameSessionCommunicator.sendEndGame(gameId, destination, true); // true indicating imposters won
        } else {
            System.out.println("Game continues. Imposters: " + aliveImposters + ", Crewmates: " + aliveCrewmates);
        }
    }

    private void saveWinStats(int gameId, boolean Winners) throws NoSessionIDs {
        List<String> gameSession = gameSessionManager.retrieveCurrentGameSessionIDSByGameID(gameId);
        List<String> winners = new ArrayList<>();
        Map<String, Integer> winnerMap = new HashMap<>();

        environmentInteractionHandler.removeGameCoords(gameId);
        for(String s : gameSession) {
            Player player = activePlayerManager.getPlayer(s);
            if(Winners) {
                if(player.getRole() instanceof Imposter && !player.getUsername().equals("Bot")){
                    winnerMap.put(player.getUsername(), 1);
                    winners.add(player.getUsername());
                } else {
                    winnerMap.put(player.getUsername(), 0);
                }
            }
            else {
                if(player.getRole() instanceof Crewmate && !player.getUsername().equals("Bot")) {
                    winners.add(player.getUsername());
                    winnerMap.put(player.getUsername(), 1);
                }
                else {
                    winnerMap.put(player.getUsername(), 0);
                }
            }

        }
        System.out.println("Winner Winner Chicken Dinner " + winners);
        Set<String> allwinners = new HashSet<>(winners);
        datenBankService.updateWins(allwinners);
        datenBankService.updateWinLossStats(winnerMap);
    }



}