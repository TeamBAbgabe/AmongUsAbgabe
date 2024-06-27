package com.example.BackendAmongUs.Map.Environment.Coords;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Grid {

    private Coordinates[][] grid;

    public void setTiles(Coordinates[][] tiles) {
        this.grid = tiles;
    }

}
