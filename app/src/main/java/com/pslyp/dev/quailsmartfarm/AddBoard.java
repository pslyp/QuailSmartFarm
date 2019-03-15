package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class AddBoard extends AppCompatActivity implements View.OnClickListener {

    Button addBtn;
    TextInputLayout token, name;

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add_board:
                addBoard();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(AddBoard.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initInstance() {
        addBtn = findViewById(R.id.button_add_board);
        token = findViewById(R.id.text_input_board_token);
        name = findViewById(R.id.text_input_board_name);

        findViewById(R.id.button_add_board).setOnClickListener(this);

        connectMQTT();
    }

    public void connectMQTT() {
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Toast.makeText(MainActivity.this, "Not Connected MQTT", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void addBoard() {
        SharedPreferences sp = getSharedPreferences("LoginPreferences", MODE_PRIVATE);

        String id = sp.getString("id", "");
        String t = token.getEditText().getText().toString();
        String n = name.getEditText().getText().toString();

        publish("user/data/token/insert", (id + "-" + t + "-" + n));

    }
}
