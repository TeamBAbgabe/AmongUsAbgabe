package com.example.BackendAmongUs.Player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Role {
    protected String roleName;
    protected boolean canKill;
    protected boolean canTeleport;

    public Role(String roleName, boolean canKill, boolean canTeleport) {
        this.roleName = roleName;
        this.canKill = canKill;
        this.canTeleport = canTeleport;
    }

}