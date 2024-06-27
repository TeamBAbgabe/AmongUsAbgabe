package com.example.BackendAmongUs.MeetingLogic.Helpers;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoteRequest {

    private String sessionId;

    private String suspectId;

}
