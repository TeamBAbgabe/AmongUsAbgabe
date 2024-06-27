package com.example.BackendAmongUs.Websockets;

import java.security.Principal;

// Example of a simple Principal implementation
public class StompPrincipal implements Principal {
    private String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
