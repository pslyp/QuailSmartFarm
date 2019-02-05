package com.pslyp.dev.quailsmartfarm;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT;

public class Bluetooth extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;

    Button btnOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initInstance();
    }

    private void initInstance() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnOnOff = findViewById(R.id.btnOnOff);
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupBT();
            }
        });
    }

    public void setupBT() {
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, "Device not Bluetooth", Toast.LENGTH_SHORT).show();
        } else if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK)
            Toast.makeText(this, "Enable Bluetooth", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Not Bluetooth", Toast.LENGTH_SHORT).show();
    }
}
