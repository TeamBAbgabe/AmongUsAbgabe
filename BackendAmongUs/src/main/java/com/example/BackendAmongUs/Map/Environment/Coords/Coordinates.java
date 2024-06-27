package com.example.BackendAmongUs.Map.Environment.Coords;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Coordinates {
    private int x;
    private int y;
    private int tileType;

    private int g; // Cost from start node to this node
    private int h; // Heuristic estimate from this node to the goal
    private int f; // Total cost (f = g + h)
    private Coordinates parent; // To reconstruct the path later

    public Coordinates(int x, int y, int tileType) {
        this.x = x;
        this.y = y;
        this.tileType = tileType;
        this.g = 0;
        this.h = 0;
        this.f = 0;
        this.parent = null;
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Coordinates(x=%d, y=%d, f=%d, g=%d, h=%d)", x, y, f, g, h);
    }
}
