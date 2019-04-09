package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import me.aflak.bluetooth.Bluetooth;

public class Settings_2 extends AppCompatActivity implements View.OnClickListener {

    private Bluetooth bluetooth = new Bluetooth(this);

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_2);

        initInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        bluetooth.onStart();
        bluetooth.showEnableDialog(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        bluetooth.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_device :
                Intent deviceListIntent = new Intent(Settings_2.this, DeviceList_2.class);
                startActivity(deviceListIntent);
                break;
        }
    }

    private void initInstance() {
        mButton = findViewById(R.id.button_device);
        findViewById(R.id.button_device).setOnClickListener(this);
    }

}
