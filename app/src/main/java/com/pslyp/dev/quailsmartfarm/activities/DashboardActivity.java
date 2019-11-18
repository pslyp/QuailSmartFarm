package com.pslyp.dev.quailsmartfarm.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pslyp.dev.quailsmartfarm.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class DashboardActivity extends AppCompatActivity {

    private LinearLayout lampStatus, fanStatus, feedStatus, waterStatus;
    private TextView mBright, mTemp;

    private String clientId;
    MqttAndroidClient client;
    IMqttToken token;

    String MQTTHOST = "tcp://test.mosquitto.org:1883";       //New Host
    //    String MQTTHOST = "tcp://35.240.245.133:1883";    //Old Host
    String USERNAME = "pslyp";
    String PASSWORD = "1475369";

    private String id = "";
    private String board_token = "";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String PREF_NAME = "LoginPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initInstance();
    }

    private void initInstance() {
        lampStatus = findViewById(R.id.linear_layout_lamp_status);
        fanStatus = findViewById(R.id.linear_layout_fan_status);
        feedStatus = findViewById(R.id.linear_layout_feed_status);
        waterStatus = findViewById(R.id.linear_layout_water_status);
        mBright = findViewById(R.id.text_view_bright);
        mTemp = findViewById(R.id.text_view_temp);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null &&
                networkInfo.isConnected();

        sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        id = sp.getString("ID", "");
//        board_token = sp.getString("BOARD_TOKEN", "");
//        board_token = "CA36842D556CDC6182F68FD244B63AD0";

        board_token = getIntent().getStringExtra("TOKEN");
        Log.e("Dashboard TOKEN", board_token);

        if(isConnected) {
            connectMQTT();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.config_menu :
                Intent intent = new Intent(DashboardActivity.this, ConfigActivity.class);
                intent.putExtra("TOKEN", board_token);
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void connectMQTT() {
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
//                    Toast.makeText(getContext(), "Connect Success", Toast.LENGTH_SHORT).show();

                    subscribe(board_token + "/brightness", 1);
                    subscribe(board_token + "/temperature", 1);
                    subscribe(board_token + "/fanStatus", 1);
                    subscribe(board_token + "/lampStatus", 1);
                    subscribe(board_token + "/feedStatus", 1);
                    subscribe(board_token + "/waterStatus", 1);

//                    if(personToken.length() > 0) {
//                        publish("/" + board_token + "/cloudMessage", personToken.substring(0, 70) + ">1");
//                        publish("/" + board_token + "/cloudMessage", personToken.substring(70, 140) + ">2");
//                        publish("/" + board_token + "/cloudMessage", personToken.substring(140) + ">3");
//                    }
                    publish(board_token + "/cloudMessage", id);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        callBack();
    }

    public void publish(String topic, String text) {
        try {
            client.publish(topic, text.getBytes(), 0 ,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic, int qos) {
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //Toast.makeText(MainActivity.this, "Subscribe: Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Toast.makeText(MainActivity.this, "Subscribe: Fail", Toast.LENGTH_SHORT).show();
                }
            });
        } catch(MqttException e) {
            e.printStackTrace();
        }
    }

    private void callBack() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals(board_token + "/lampStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        lampStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        lampStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals(board_token + "/fanStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        fanStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        fanStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals(board_token + "/feedStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        feedStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        feedStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals(board_token + "/waterStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        waterStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        waterStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals(board_token + "/brightness")) {
                    mBright.setText(new String(message.getPayload()));
                }
                if(topic.equals(board_token + "/temperature")) {
                    mTemp.setText(new String(message.getPayload()));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}
