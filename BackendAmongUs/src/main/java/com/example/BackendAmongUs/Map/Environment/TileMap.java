package com.example.BackendAmongUs.Map.Environment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TileMap {
    private List<Layer> layers;

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Layer {
        private List<Integer> data;

        public List<Integer> getData() {
            return data;
        }

        public void setData(List<Integer> data) {
            this.data = data;
        }
    }
}
