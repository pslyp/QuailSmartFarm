package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    Button two, bluetooth, mqtt1, mqtt2;
    TextView temp, bright, fan, light;

    String MQTTHOST = "tcp://35.240.137.230:1883";
    String USERNAME = "pslyp";
    String PASSWORD = "1475369";

    String clientId;
    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstance();
    }

    private void initInstance() {
        temp = findViewById(R.id.textView_temp);
        bright = findViewById(R.id.textView_bright);
        two = findViewById(R.id.bt_two);
        fan = findViewById(R.id.textView_fan);
        light = findViewById(R.id.textView_light);
        bluetooth = findViewById(R.id.btnBlue);
        mqtt1 = findViewById(R.id.btnMQTT1);
        mqtt2 = findViewById(R.id.btnMQTT2);

        connectMQTT();

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent two = new Intent(MainActivity.this, configNetwork.class);
                startActivity(two);
                finish();
            }
        });

        fan.setOnClickListener(new View.OnClickListener() {
            String status = "0";
            @Override
            public void onClick(View v) {
                if(status.equals("0")) {
                    publish("fan", "1");
                    Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                    status = "1";
                } else {
                    publish("fan", "0");
                    Toast.makeText(MainActivity.this, "0", Toast.LENGTH_SHORT).show();
                    status = "0";
                }
            }
        });

        light.setOnClickListener(new View.OnClickListener() {
            String status = "0";
            @Override
            public void onClick(View v) {
                if(status.equals("0")) {
                    publish("lamp", "1");
                    Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                    status = "1";
                } else {
                    publish("lamp", "0");
                    Toast.makeText(MainActivity.this, "0", Toast.LENGTH_SHORT).show();
                    status = "0";
                }
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Bluetooth.class);
                startActivity(intent);
                finish();
            }
        });

        mqtt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish("presence", "Hiiiiiiiii");
            }
        });

        mqtt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish("presence2", "MQTT2");
            }
        });
    }

    private void connectMQTT() {
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
                    Toast.makeText(MainActivity.this, "Connected MQTT", Toast.LENGTH_SHORT).show();
                    subscribe("brightness", 1);
                    subscribe("temp", 2);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Not Connected MQTT", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        callBack();
    }

    private void callBack() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals("temp")) {
                    temp.setText(new String(message.getPayload()));
                } else {
                    bright.setText(new String(message.getPayload()));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void publish(String topic, String message) {
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(String topic, int qos) {
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Subscribe: Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Subscribe: Fail", Toast.LENGTH_SHORT).show();
                }
            });
        } catch(MqttException e) {
            e.printStackTrace();
        }
    }
}
