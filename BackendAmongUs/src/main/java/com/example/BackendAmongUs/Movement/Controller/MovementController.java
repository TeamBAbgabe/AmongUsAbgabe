package com.example.BackendAmongUs.Movement.Controller;

import com.example.BackendAmongUs.GameSession.GameSessionManager;
import com.example.BackendAmongUs.Movement.MovementHandler;
import com.example.BackendAmongUs.Movement.Helper.MovementJSON;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@Controller
public class MovementController {

    private final MovementHandler gameService;
    @Autowired
    private GameSessionManager gameSessionManager;

    private static final Logger logger = LogManager.getLogger(MovementController.class);

    @Autowired
    public MovementController(MovementHandler gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/move")
    public void processMovement(@Payload MovementJSON movement, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getUser().getName();
            if(gameSessionManager.findGameIdBySessionId(sessionId) != null) {
                gameService.movePlayer(sessionId, movement.getInput());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
