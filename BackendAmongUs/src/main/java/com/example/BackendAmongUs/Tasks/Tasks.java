package com.example.BackendAmongUs.Tasks;

import com.example.BackendAmongUs.Map.Environment.Coords.Coordinates;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Tasks {
    private String taskId;
    private String description;
    private Coordinates coordinates;
    private boolean isCompleted;
    private String taskType; // Type of task, e.g., "button", "question"
    private List<Integer> stages; // Used for multi-stage tasks like button clicking
    private String question; // Question text for quiz-type tasks
    private String correctAnswer; // Correct answer for quiz-type tasks

    // Full constructor for all fields
    public Tasks(String taskId, String description, Coordinates coordinates, String taskType, List<Integer> stages, String question, String correctAnswer){
        this.taskId = taskId;
        this.description = description;
        this.coordinates = coordinates;
        this.taskType = taskType;
        this.stages = stages;
        this.question = question;
        this.correctAnswer = correctAnswer;
    }
}
