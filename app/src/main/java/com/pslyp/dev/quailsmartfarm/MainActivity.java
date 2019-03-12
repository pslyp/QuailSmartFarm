package com.pslyp.dev.quailsmartfarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button two, bluetooth, mqtt1, mqtt2, signOut_btn;
    TextView temp, bright, fanSta, lampSta;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    //Google Sign out
    private GoogleSignInClient mGoogleSignInClient;

    //MQTT
    String clientId;
    MqttAndroidClient client;

    String MQTTHOST = "tcp://35.240.137.230:1883";
    String USERNAME = "pslyp";
    String PASSWORD = "1475369";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        initInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_out:

                //Show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to log out?");
                builder.setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Sign out Google
                        signOut();
                    }
                })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu:
                Toast.makeText(this, "Add board", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.bluetooth_submenu:
                Toast.makeText(this, "Bluetooth", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.wifi_submenu:
                Toast.makeText(this, "WiFi", Toast.LENGTH_SHORT).show();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void initInstance() {
        temp = findViewById(R.id.text_view_temp);
        bright = findViewById(R.id.text_view_bright);
        two = findViewById(R.id.bt_two);
        fanSta = findViewById(R.id.text_view_fan_status);
        lampSta = findViewById(R.id.text_view_lamp_status);
        bluetooth = findViewById(R.id.btnBlue);
        mqtt1 = findViewById(R.id.btnMQTT1);
        mqtt2 = findViewById(R.id.btnMQTT2);
        signOut_btn = findViewById(R.id.button_sign_out);
        findViewById(R.id.button_sign_out).setOnClickListener(this);

        //SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        String namePre = sharedPreferences.getString("name", "not found!");
        String emailPre = sharedPreferences.getString("email", "not found!");
        String idPre = sharedPreferences.getString("id", "not found!");

        if(idPre.equals("not found!")) {
            Intent intent = new Intent(MainActivity.this, Authentication.class);
            startActivity(intent);
            finish();
        }

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Check connected internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfos = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfos != null &&
                              networkInfos.isConnected();

        if(isConnected) {
            connectMQTT();
        } else {
            Toast.makeText(this, "Internet not connect", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(findViewById(R.id.Layout1), "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent two = new Intent(MainActivity.this, configNetwork.class);
                startActivity(two);
                finish();
            }
        });

        fanSta.setOnClickListener(new View.OnClickListener() {
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

        lampSta.setOnClickListener(new View.OnClickListener() {
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
                    //Toast.makeText(MQTT.this, "Connected MQTT", Toast.LENGTH_SHORT).show();
                    subscribe("brightness", 1);
                    subscribe("temperature", 2);
                    subscribe("fanStatus", 2);
                    subscribe("lampStatus", 2);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Toast.makeText(MainActivity.this, "Not Connected MQTT", Toast.LENGTH_SHORT).show();
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
            public void messageArrived(String topic, MqttMessage message) {
                if(topic.equals("temperature"))
                    temp.setText(new String(message.getPayload()));
                if(topic.equals("brightness"))
                    bright.setText(new String(message.getPayload()));
                if(topic.equals("fanStatus"))
                    fanSta.setText(new String(message.getPayload()));
                if(topic.equals("lampStatus"))
                    lampSta.setText(new String(message.getPayload()));

                /*
                switch (topic) {
                    case "temperature":
                        temp.setText(new String(message.getPayload()));
                        break;
                    case "brightness":
                        bright.setText(new String(message.getPayload()));
                        break;
                    case "fanStatus":
                        fanSta.setText(new String(message.getPayload()));
                        break;
                    case "lampStatus":
                        lampSta.setText(new String(message.getPayload()));
                        break;
                    default: break;
                }
                */
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void publish(String topic, String message) {
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

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

                        editor = sp.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(MainActivity.this, Authentication.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}
