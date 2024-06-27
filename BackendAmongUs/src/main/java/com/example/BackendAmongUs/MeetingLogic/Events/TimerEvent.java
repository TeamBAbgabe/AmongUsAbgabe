package com.example.BackendAmongUs.MeetingLogic.Events;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TimerEvent extends ApplicationEvent {
    private String message;
    private int gameId;

    public TimerEvent(Object source, String message, int gameID) {
        super(source);
        this.message = message;
        this.gameId = gameID;
    }


}
