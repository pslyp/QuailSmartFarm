package com.pslyp.dev.quailsmartfarm.services;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

public class Bluetooth_LE {

    private final BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Bluetooth_LE(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

//    public BluetoothAdapter

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            // Stops scanning after a pre-defined scan period.
//            handler.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    mScanning = false;
//                    bluetoothAdapter.stopLeScan(leScanCallback);
//                }
//            }, SCAN_PERIOD);
//
//            mScanning = true;
//            bluetoothAdapter.startLeScan(leScanCallback);
//        } else {
//            mScanning = false;
//            bluetoothAdapter.stopLeScan(leScanCallback);
//        }
//    }

//    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //leDeviceListAdapter.addDevice(device);
//                    if(device != null) {
//                        deviceArrayList.add(device);
//
//                        leDeviceListAdapter = new LeDeviceListAdapter(MainActivity.this, R.layout.device_item, deviceArrayList);
//                        mListView.setAdapter(leDeviceListAdapter);
//
//                        leDeviceListAdapter.notifyDataSetChanged();
//                    }
//                }
//            });
//        }
//    };

}
