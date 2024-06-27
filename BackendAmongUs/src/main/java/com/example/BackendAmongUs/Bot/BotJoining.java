package com.example.BackendAmongUs.Bot;

import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class BotJoining {
    private Map<Integer, List<String>> botLobby = new HashMap<>();
    private List<String> bots;

    public String joinRandomLobby(){
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        return generatedString;
    }

}
