package com.example.BackendAmongUs.Tasks.Controller;

import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Tasks.Helper.TaskPosition;
import com.example.BackendAmongUs.Tasks.Helper.TaskRequest;
import com.example.BackendAmongUs.Tasks.Manager.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TaskController {
    @Autowired
    private TaskManager taskManager;

    @MessageMapping("/taskInitialize")
    public void taskController(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getUser().getName();
        System.out.println("Task initialization request received for session: " + sessionId);
        taskManager.canDoTask(sessionId);

    }

    @MessageMapping("/checkTask")
    public void checkTask(SimpMessageHeaderAccessor headerAccessor, @RequestParam TaskRequest taskRequest) throws GameIdNotFound, NoSessionIDs {
        String sessionId = headerAccessor.getUser().getName();
        System.out.println("Task initialization request received for session: " + sessionId);
        taskManager.checkDone(sessionId, taskRequest.getTaskId());
    }
}

