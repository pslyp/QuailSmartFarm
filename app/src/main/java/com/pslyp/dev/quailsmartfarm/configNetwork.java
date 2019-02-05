package com.pslyp.dev.quailsmartfarm;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class configNetwork extends AppCompatActivity {

    Button btnSend, btnBluetooth;
    BluetoothSPP bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_network);

        initInstance();
    }

    private void initInstance() {
        btnSend = findViewById(R.id.btnSend);
        btnBluetooth = findViewById(R.id.btnBluetooth);
        bt = new BluetoothSPP(configNetwork.this);

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to " + name + "\n" + address, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopAutoConnect();
    }

    public void onStart() {
        super.onStart();
        if(!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            bt.setupService();
            bt.startService(BluetoothState.DEVICE_OTHER);
            setup();
        }
    }

    public void setup() {
        //other code
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.send("Text", true);
            }
        });

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(configNetwork.this, DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
