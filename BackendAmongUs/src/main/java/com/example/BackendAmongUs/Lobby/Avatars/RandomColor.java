package com.example.BackendAmongUs.Lobby.Avatars;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RandomColor {
    private List<String> availableColors = Arrays.asList("Red", "Green", "Blue", "Yellow", "Purple", "Orange", "Cyan", "Magenta", "Lime", "Pink");
    private Map<Integer, List<String>> gameColors = new HashMap<>();

    public String randomColor(int lobbyId) {
        List<String> usedColor = gameColors.computeIfAbsent(lobbyId, k -> new ArrayList<>());
        System.out.println("Colors" + usedColor);
        if (usedColor.size() >= availableColors.size()) {
            return null;
        }

        List<String> remainingColors = new ArrayList<>(availableColors);
        remainingColors.removeAll(usedColor);

        if (remainingColors.isEmpty()) {
            return null;
        }

        Random random = new Random();
        String selectedColor = remainingColors.get(random.nextInt(remainingColors.size()));
        usedColor.add(selectedColor);

        gameColors.put(lobbyId, usedColor);

        return selectedColor;
    }

    public void resetColors(int lobbyId) {
        List<String> colors = gameColors.get(lobbyId);
        colors.clear();
        System.out.println(colors);
    }

    public void removeUsedColor(int lobbyId, String color){
        List<String> colors = gameColors.get(lobbyId);
        colors.remove(color);
    }
}
