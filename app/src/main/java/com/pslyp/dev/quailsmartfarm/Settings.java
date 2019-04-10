package com.pslyp.dev.quailsmartfarm;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.pslyp.dev.quailsmartfarm.adapter.DeviceListAdapter;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DiscoveryCallback;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    private Bluetooth mBluetooth = new Bluetooth(this);
    private DeviceData deviceData;
    private DeviceListAdapter deviceListAdapter;
    private DiscoveryCallback discoveryCallback;

    private Button mButton;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mBluetooth.onStart();
        mBluetooth.setCallbackOnUI(this);
        //mBluetooth.enable();
        mBluetooth.showEnableDialog(Settings.this);
        //mBluetooth.startScanning();

//        if(!mBluetooth.isEnabled()) {
//            mBluetooth.onStart();
//            mBluetooth.showEnableDialog(this);
//        }
//        if(mBluetooth.isEnabled()) {
//            mBluetooth.onStart();
//        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        mBluetooth.onStop();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetooth.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_find_device :
                mBluetooth.startScanning();
                //startActivity(new Intent(Settings.this, DeviceList.class));
                break;
        }
    }

    private void initInstance() {
        deviceData = new DeviceData();

        mButton = findViewById(R.id.button_find_device);
        findViewById(R.id.button_find_device).setOnClickListener(this);
        mListView = findViewById(R.id.list_view_device);

        mBluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override
            public void onDiscoveryStarted() {
                mButton.setEnabled(false);
                Toast.makeText(Settings.this, "Start discovery", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDiscoveryFinished() {
                mButton.setEnabled(true);
                Toast.makeText(Settings.this, "Stop discovery", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {
                discoveryCallback.onDeviceFound(device);

                Toast.makeText(Settings.this, "Find device", Toast.LENGTH_SHORT).show();


                deviceData.deviceArrayList.add(device);

                deviceListAdapter = new DeviceListAdapter(Settings.this, R.layout.device_item, deviceData.deviceArrayList);
                mListView.setAdapter(deviceListAdapter);
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {

            }

            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {

            }

            @Override
            public void onError(String message) {
                Toast.makeText(Settings.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
