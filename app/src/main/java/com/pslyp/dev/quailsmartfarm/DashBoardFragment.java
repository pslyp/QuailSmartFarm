package com.pslyp.dev.quailsmartfarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends Fragment {

    private LinearLayout lampStatus, fanStatus, feedStatus, waterStatus;
    private TextView mBright, mTemp;

    private String clientId;
    MqttAndroidClient client;
    IMqttToken token;

    String MQTTHOST = "tcp://35.240.245.133:1883";
    String USERNAME = "pslyp";
    String PASSWORD = "1475369";

    private String id = "";
    private String board_token = "";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String PREF_NAME = "LoginPreferences";

    public DashBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);

        setHasOptionsMenu(true);

        initInsance(view);

        return view;
    }

    private void initInsance(View view) {
        lampStatus = view.findViewById(R.id.linear_layout_lamp_status);
        fanStatus = view.findViewById(R.id.linear_layout_fan_status);
        feedStatus = view.findViewById(R.id.linear_layout_feed_status);
        waterStatus = view.findViewById(R.id.linear_layout_water_status);
        mBright = view.findViewById(R.id.text_view_bright);
        mTemp = view.findViewById(R.id.text_view_temp);

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null &&
                networkInfo.isConnected();

        sp = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        id = sp.getString("ID", "");
        board_token = sp.getString("BOARD_TOKEN", "");
//        String tokenString = "4C31A6DBCD72FF1171332936EFDBF273";

        if(isConnected) {
            connectMQTT();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    public void connectMQTT() {
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getContext().getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
//                    Toast.makeText(getContext(), "Connect Success", Toast.LENGTH_SHORT).show();

                    subscribe("/" + board_token + "/brightness", 1);
                    subscribe("/" + board_token + "/temperature", 1);
                    subscribe("/" + board_token + "/fanStatus", 1);
                    subscribe("/" + board_token + "/lampStatus", 1);
                    subscribe("/" + board_token + "/feedStatus", 1);
                    subscribe("/" + board_token + "/waterStatus", 1);

//                    if(personToken.length() > 0) {
//                        publish("/" + board_token + "/cloudMessage", personToken.substring(0, 70) + ">1");
//                        publish("/" + board_token + "/cloudMessage", personToken.substring(70, 140) + ">2");
//                        publish("/" + board_token + "/cloudMessage", personToken.substring(140) + ">3");
//                    }
                    publish("/" + board_token + "/cloudMessage", id);
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
                if(topic.equals("/" + board_token + "/lampStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        lampStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        lampStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals("/" + board_token + "/fanStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        fanStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        fanStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals("/" + board_token + "/feedStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        feedStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        feedStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals("/" + board_token + "/waterStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        waterStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        waterStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals("/" + board_token + "/brightness")) {
                    mBright.setText(new String(message.getPayload()));
                }
                if(topic.equals("/" + board_token + "/temperature")) {
                    mTemp.setText(new String(message.getPayload()));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}
