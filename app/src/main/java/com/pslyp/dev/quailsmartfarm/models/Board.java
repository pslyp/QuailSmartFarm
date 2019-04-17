package com.pslyp.dev.quailsmartfarm.models;

public class Board {

    private String token;
    private String name;
    private int brightness;
    private int temperature;
    private String start;
    private String end;

    public Board(String token, String name) {
        this.token = token;
        this.name = name;
    }

    public Board(String token, String name, int brightness, int temperature, String start, String end) {
        this.token = token;
        this.name = name;
        this.brightness = brightness;
        this.temperature = temperature;
        this.start = start;
        this.end = end;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

}
