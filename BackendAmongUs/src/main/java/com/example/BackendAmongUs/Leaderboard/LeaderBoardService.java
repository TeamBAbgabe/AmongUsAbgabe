package com.example.BackendAmongUs.Leaderboard;

import com.example.BackendAmongUs.DatenBank.DatenBankService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LeaderBoardService {
    @Autowired
    private DatenBankService datenBankService;

    public String getLeaderboard() {
        Map<String, Integer> winsMap = datenBankService.getAllWins();
        System.out.println(winsMap);
        LinkedHashMap<String, Integer> sortedMap = winsMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        System.out.println(sortedMap);
        String jsonResult = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonResult = mapper.writeValueAsString(sortedMap);
            System.out.println(jsonResult);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }


}
