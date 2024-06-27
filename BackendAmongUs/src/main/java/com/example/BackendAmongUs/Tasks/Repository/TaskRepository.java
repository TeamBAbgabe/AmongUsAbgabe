package com.example.BackendAmongUs.Tasks.Repository;

import com.example.BackendAmongUs.Map.Environment.Coords.Coordinates;
import com.example.BackendAmongUs.Tasks.Tasks;
import org.springframework.stereotype.Repository;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskRepository {

    private List<Tasks> tasks;

    public TaskRepository() {
        this.tasks = new ArrayList<>(Arrays.asList(
                new Tasks("5", "Asteriod Task", new Coordinates(55, 4,69), "button", Arrays.asList(1, 3, 5), null, null),
                new Tasks("3", "Asteriod Task", new Coordinates(3, 4,69), "button", null, null, null),
                new Tasks("2", "Button Task", new Coordinates(33, 59,60), "button", Arrays.asList(1, 3, 5), null, null),
                new Tasks("6", "Button Task", new Coordinates(16, 19,60), "button", null, null, null)

        ));
    }

    public List<Tasks> getTasks(){
        return tasks;
    }

    public void addTask(Tasks task) {
        tasks.add(task);
    }

    public void removeTask(String taskId) {
        tasks.removeIf(t -> t.getTaskId().equals(taskId));
    }
}
