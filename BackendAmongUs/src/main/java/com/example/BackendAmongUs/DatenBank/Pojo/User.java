package com.example.BackendAmongUs.DatenBank.Pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.boot.autoconfigure.domain.EntityScan;




public class User {
    private String username;
    private String hashedpassword;


    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedpassword() {
        return this.hashedpassword;
    }

    public void setHashedpassword(String hashedpassword) {
        this.hashedpassword = hashedpassword;
    }
}
