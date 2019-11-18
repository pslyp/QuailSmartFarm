package com.pslyp.dev.quailsmartfarm.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceListResponse {

    @SerializedName("board")
    @Expose
    private List<Device> devices;

    public DeviceListResponse(List<Device> devices) {
        this.devices = devices;
    }

    public List<Device> getDevices() {
        return devices;
    }

}
