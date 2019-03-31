package com.pslyp.dev.quailsmartfarm.models;

public class Board {

    private String token;
    private String name;

    public Board(String token, String name) {
        this.token = token;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }
}
