package com.example.BackendAmongUs.Chat.RestController;

import com.example.BackendAmongUs.Chat.ChatHandler;
import com.example.BackendAmongUs.Chat.Exceptions.PlayerDead;
import com.example.BackendAmongUs.Chat.Helper.ChatMessage;
import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Lobby.Exceptions.GameCouldntStart;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Player.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class ChatController {

    @Autowired
    private ChatHandler chatHandler;
    @Autowired
    private ActivePlayerManager activePlayerManager;

    @CrossOrigin(origins = "*")
    @PostMapping("/sendMessage")
    public void chatMessage(@RequestBody ChatMessage chatMessage) throws GameCouldntStart, GameIdNotFound, NoSessionIDs, JsonProcessingException, PlayerDead {
        Player player = activePlayerManager.getPlayer(chatMessage.getSessionId());
        if(!player.getDead()){
            chatHandler.chatting(chatMessage.getSessionId(), chatMessage.getMessage());
        } else {
            throw new PlayerDead("Player is dead and cant write");
        }
    }
}
