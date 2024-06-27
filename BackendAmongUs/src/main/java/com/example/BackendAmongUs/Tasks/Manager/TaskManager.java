package com.example.BackendAmongUs.Tasks.Manager;


import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.Map.Environment.Coords.Coordinates;
import com.example.BackendAmongUs.MeetingLogic.PlayerManager.ActivePlayerManager;
import com.example.BackendAmongUs.Player.Player;
import com.example.BackendAmongUs.Player.Crewmate;
import com.example.BackendAmongUs.Tasks.Helper.TaskButton;
import com.example.BackendAmongUs.Tasks.Repository.TaskRepository;
import com.example.BackendAmongUs.Tasks.Tasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class TaskManager {

    @Autowired
    private ActivePlayerManager activePlayerManager;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private TaskBar taskBar;
    @Autowired
    private TaskButton taskButton;

    private Map<String, List<Tasks>> individualTasks = new HashMap<>();

    public void createTasks(List<String> sessions, int gameId){
        List<String> crewMates = new ArrayList<>();
        int crewmateAmount = 0;
        for(String s : sessions){
            if(activePlayerManager.getPlayer(s).getRole() instanceof Crewmate){
                crewmateAmount++;
                crewMates.add(s);
            }
        }
        taskBar.addToTaskBar(gameId, crewmateAmount);
        assignTasks(crewMates);
    }



    public void assignTasks(List<String> crewMates) {

        List<Tasks> shuffledTasks = taskRepository.getTasks();
        Collections.shuffle(shuffledTasks);
        List<Tasks> selectedTasks = shuffledTasks.subList(0, 2);

        for (String sessionId : crewMates) {
            individualTasks.put(sessionId, new ArrayList<>(selectedTasks));
            System.out.println("the task list is" + individualTasks);
        }
    }

    private Map<String, List<Tasks>> getIndividualTasks(String sessionId) {
        return (Map<String, List<Tasks>>) individualTasks.get(sessionId);
    }



    public List<Tasks> tasks(String sessionId) {
        return individualTasks.get(sessionId);
    }

    public void sendTasks(List<String> sessions){
        for(String s : sessions){
            if(individualTasks.containsKey(s)){
                messagingTemplate.convertAndSendToUser(s, "/queue/taskList", individualTasks.get(s));
                taskButton.sentTaskButton(s, false);
            }
        }
    }

    public void canDoTask(String sessionId) {

        Player player = activePlayerManager.getPlayer(sessionId);
        int positionX = (player.getX() + 15) / 32;
        int positionY = (player.getY() + 15) / 32;
        Coordinates currentPosition = new Coordinates(positionX, positionY, 0);

        List<Tasks> tasks = individualTasks.get(sessionId);

        if (player.getRole() instanceof Crewmate) {
            Optional<Tasks> nearbyTask = tasks.stream()
                    .filter(task -> isTaskNearby(task, currentPosition) && !task.isCompleted())
                    .findFirst();

            if (nearbyTask.isPresent()) {
                Tasks task = nearbyTask.get();
                System.out.println("Player can perform task: " + task.getTaskId() + " - " + task.getTaskId());
                messagingTemplate.convertAndSendToUser(sessionId, "/queue/task", task);
            } else {
                System.out.println("No tasks are nearby or all nearby tasks are completed.");
            }
        } else {
            System.out.println("Only crewmates can perform tasks.");
        }
    }

    public boolean isPlayerNearTask(String sessionId) {
        Player player = activePlayerManager.getPlayer(sessionId);
        if (!(player.getRole() instanceof Crewmate)) {
            return false;
        }

        int positionX = (player.getX() + 15) / 32;
        int positionY = (player.getY() + 15) / 32;
        Coordinates currentPosition = new Coordinates(positionX, positionY,69);

        List<Tasks> tasks = individualTasks.get(sessionId);

        return tasks.stream()
                .anyMatch(task -> isTaskNearby(task, currentPosition) && !task.isCompleted());
    }


    private boolean isTaskNearby(Tasks task, Coordinates currentPosition) {
        int deltaX = Math.abs(currentPosition.getX() - task.getCoordinates().getX());
        int deltaY = Math.abs(currentPosition.getY() - task.getCoordinates().getY());
        return deltaX <= 1 && deltaY <= 1;
    }

    public void checkDone(String sessionId, String taskId) throws GameIdNotFound, NoSessionIDs {
        List<Tasks> taskList = individualTasks.get(sessionId);
        Tasks completedTask = null;
        for(Tasks t : taskList){
            if(t.getTaskId().equals(taskId)){
                completedTask = t;
            }
        }
        taskButton.sentTaskButton(sessionId, false);
        taskList.remove(completedTask);
        taskBar.updateTaskBar(sessionId);

    }

}


