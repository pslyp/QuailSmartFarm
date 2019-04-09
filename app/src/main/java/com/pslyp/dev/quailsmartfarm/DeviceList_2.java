package com.pslyp.dev.quailsmartfarm;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DiscoveryCallback;

public class DeviceList_2 extends AppCompatActivity implements View.OnClickListener {

    private Bluetooth mBluetooth = new Bluetooth(this);

    private Button mButton;
    private ListView mListView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list_2);

        initInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_find :
                mBluetooth.startScanning();
                break;
        }
    }

    private void initInstance() {
        mButton = findViewById(R.id.button_find);
        findViewById(R.id.button_find).setOnClickListener(this);
        mListView = findViewById(R.id.list_view);
        mTextView = findViewById(R.id.text_view);
//        mTextView.setVisibility(View.GONE);

        getPairedDevices();

        mBluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override
            public void onDiscoveryStarted() {

            }

            @Override
            public void onDiscoveryFinished() {

            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {
//                List<BluetoothDevice> devices = device;
//
//                mListView.setAdapter(new ArrayAdapter<BluetoothDevice>(this, R.layout.device_item, device));
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {

            }

            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void getPairedDevices() {
        if(mBluetooth.isEnabled()) {
            List<BluetoothDevice> devices = mBluetooth.getPairedDevices();

            if(devices != null) {
                mListView.setAdapter(new ArrayAdapter<BluetoothDevice>(this, R.layout.device_item, devices));
                mTextView.setVisibility(View.GONE);
            } else {
                mListView.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}
