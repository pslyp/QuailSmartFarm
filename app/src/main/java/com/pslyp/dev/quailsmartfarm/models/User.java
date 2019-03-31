package com.pslyp.dev.quailsmartfarm.models;

import java.util.List;

public class User {

    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private List<Board> board;

    public User(String id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public List<Board> getBoard() {
        return board;
    }

}