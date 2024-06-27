package com.example.BackendAmongUs.Map.Controller;

import com.example.BackendAmongUs.Map.GamingMap;
import com.example.BackendAmongUs.Movement.Controller.MovementController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

//Wird nicht benutzt sollte aber die infos von Matrix bekommen und nach vorne schicken

@Controller
public class MatrixController {

    @Autowired
    private GamingMap matrix;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LogManager.getLogger(MovementController.class);


    @MessageMapping("/updateMatrix")
    @SendTo("/topic/map")
    public void updateMatrix(String matrixJson) {
        System.out.println("ich (Logger) bin hier");

        messagingTemplate.convertAndSend("/topic/map", matrixJson);
    }

}
