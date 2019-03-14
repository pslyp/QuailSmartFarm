package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class addBoard extends AppCompatActivity {

    Button addBtn;

    //MQTT
    String clientId;
    MqttAndroidClient client;

    String MQTTHOST = "tcp://35.240.137.230:1883";
    String USERNAME = "pslyp";
    String PASSWORD = "1475369";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);

        initInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(addBoard.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initInstance() {
        addBtn = findViewById(R.id.button_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
