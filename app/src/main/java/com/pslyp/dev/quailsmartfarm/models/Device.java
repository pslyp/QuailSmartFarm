package com.pslyp.dev.quailsmartfarm.models;

public class Device {

    private String _id;
    private String token;
    private String name;
    private int brightness;
    private int tempMin;
    private int tempMax;
    private Temp temp;
    private int temperature;
    private String start;
    private String end;
    private String timeUp;

    public Device(int brightness, int temperature, String timeUp) {
        this.brightness = brightness;
        this.temperature = temperature;
        this.timeUp = timeUp;
    }

    public Device(int brightness, int tempMin, int tempMax, String timeUp) {
        this.brightness = brightness;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.timeUp = timeUp;
    }

    //    public Device(String token, String name, int brightness, int temperature, String start, String end) {
//        this.token = token;
//        this.name = name;
//        this.brightness = brightness;
//        this.temperature = temperature;
//        this.start = start;
//        this.end = end;
//    }

    public Device(String token, String name, int brightness, int temperature, String timeUp, String start, String end) {
        this.token = token;
        this.name = name;
        this.brightness = brightness;
        this.temperature = temperature;
        this.timeUp = timeUp;
        this.start = start;
        this.end = end;
    }
//
//    public Device(String _id, int brightness, String token, String name, int temperature, String start, String end, String timeUp) {
//        this._id = _id;
//        this.brightness = brightness;
//        this.token = token;
//        this.name = name;
//        this.temperature = temperature;
//        this.start = start;
//        this.end = end;
//        this.timeUp = timeUp;
//    }

    public Device(String token, String name, int brightness, int tempMin, int tempMax, String timeUp, String start, String end) {
        this.token = token;
        this.name = name;
        this.brightness = brightness;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.start = start;
        this.end = end;
        this.timeUp = timeUp;
    }

    public Device(String token, String name, int brightness, Temp temp, String start, String end, String timeUp) {
        this.token = token;
        this.name = name;
        this.brightness = brightness;
        this.temp = temp;
        this.start = start;
        this.end = end;
        this.timeUp = timeUp;
    }

    public String get_id() {
        return _id;
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

    public Temp getTemp() {
        return temp;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getTempMin() {
        return tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getTimeUp() {
        return timeUp;
    }

}
