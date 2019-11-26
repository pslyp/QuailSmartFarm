package com.pslyp.dev.quailsmartfarm.models;

import java.util.List;

public class User {

    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String msgToken;
    private List<Device> devices;

    public User(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public User(String id, String firstname, String email, String password) {
        this.id = id;
        this.firstname = firstname;
        this.email = email;
        this.msgToken = msgToken;
    }

    public User(String id, String firstname, String email, String password, String msgToken) {
        this(id, email, password);
        this.firstname = firstname;
        this.msgToken = msgToken;
    }

    public User(String id, String firstname, String lastname, String email, String password, String msgToken, List<Device> devices) {
        this(id, email, password);
        this.firstname = firstname;
        this.lastname = lastname;
        this.msgToken = msgToken;
        this.devices = devices;
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

    public String getPassword() {
        return password;
    }

    public List<Device> getDevice() {
        return devices;
    }

    public String getMsgToken() {
        return msgToken;
    }

}