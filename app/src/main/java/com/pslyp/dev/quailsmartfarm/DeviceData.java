package com.pslyp.dev.quailsmartfarm;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

public class DeviceData {

    private static DeviceData sInstance;

    public ArrayList<BluetoothDevice> deviceArrayList;

    public static DeviceData getInstance() {
        if(sInstance == null) {
            sInstance = new DeviceData();
        }
        return sInstance;
    }
}
