package com.pslyp.dev.quailsmartfarm.models;

public class BTDevice {

    private String name = "";
    private String address = "";

    public BTDevice(String name) {
        this.name = name;
    }

    public BTDevice(String name, String address) {
        this(name);
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

}
