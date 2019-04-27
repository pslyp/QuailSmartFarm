package com.pslyp.dev.quailsmartfarm;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends Fragment {

    private LinearLayout lampStatus, fanStatus, feedStatus, waterStatus;

    private MQTT mqtt;

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
        mqtt = new MQTT(getContext());

        lampStatus = view.findViewById(R.id.linear_layout_lamp_status);
        fanStatus = view.findViewById(R.id.linear_layout_fan_status);
        feedStatus = view.findViewById(R.id.linear_layout_feed_status);
        waterStatus = view.findViewById(R.id.linear_layout_water_status);

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnectec = networkInfo != null &&
                networkInfo.isConnected();

        sp = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if(isConnectec) {
            mqtt.connect();

            String token = sp.getString("BOARD_TOKEN", "");
            callBack(token);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    private void callBack(final String token) {
        mqtt.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals(token + "/lampStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        lampStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        lampStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals(token + "/fanStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        fanStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        fanStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals(token + "/feedStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        feedStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        feedStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals(token + "/waterStatus")) {
                    if(new String(message.getPayload()).equals("ON")) {
                        waterStatus.setBackgroundResource(R.drawable.shape_status_red);
                    } else {
                        waterStatus.setBackgroundResource(R.drawable.shape_status_default);
                    }
                }
                if(topic.equals(token + "/brightness")) {

                }
                if(topic.equals(token + "/temperature")) {

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}
