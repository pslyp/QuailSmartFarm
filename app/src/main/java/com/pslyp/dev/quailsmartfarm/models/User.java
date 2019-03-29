package com.pslyp.dev.quailsmartfarm.models;

import java.util.List;

public class User {

    private String id;
    private String firstname;
    private String lastname;
    private List<Board> board;

    public String getId() {
        return id;
    }
    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public List<Board> getBoard() {
        return board;
    }

}