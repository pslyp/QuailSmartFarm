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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button two, bluetooth, mqtt1, mqtt2, signOut_btn;
    TextView temp, bright, fanSta, lampSta;
    LinearLayout linearLayout1;

    //MQTT
    MQTT mqtt;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    //Google Sign out
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        //SharedPreferences
        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        //Check Login
        boolean isLogin = sp.getBoolean("log_in", false);
        if(!isLogin) {
            Intent intent = new Intent(MainActivity.this, Authentication.class);
            startActivity(intent);
            finish();
        }

        initInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_bluetooth:
                Intent intent = new Intent(MainActivity.this, Bluetooth.class);
                startActivity(intent);
                finish();
                break;
            case R.id.button_mqtt1:
                mqtt.publish("user/create", "432743278-PSlyp-Sali-phiphat.green@gmail.com");
                break;
            case R.id.button_mqtt2:
                mqtt.publish("presence2", "MQTT2");
                break;
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
                //Toast.makeText(this, "Add board", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddBoard.class);
                startActivity(intent);
                finish();
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
        linearLayout1 = findViewById(R.id.linear_layout_1);
        temp = findViewById(R.id.text_view_temp);
        bright = findViewById(R.id.text_view_bright);
        two = findViewById(R.id.button_two);
        fanSta = findViewById(R.id.text_view_fan_status);
        lampSta = findViewById(R.id.text_view_lamp_status);
        bluetooth = findViewById(R.id.button_bluetooth);
        mqtt1 = findViewById(R.id.button_mqtt1);
        mqtt2 = findViewById(R.id.button_mqtt2);
        signOut_btn = findViewById(R.id.button_sign_out);

        findViewById(R.id.button_bluetooth).setOnClickListener(this);
        findViewById(R.id.button_mqtt1).setOnClickListener(this);
        findViewById(R.id.button_mqtt2).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);

        linearLayout1.setVisibility(View.GONE);

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfos = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfos != null &&
                              networkInfos.isConnected();

        if(isConnected) {
            mqtt = new MQTT(this);
            mqtt.connectMQTT();

            sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String id = sp.getString("id", "");
            String firstName = sp.getString("first_name", "");
            String lastName = sp.getString("last_name", "");
            String email = sp.getString("email", "");

            String user = (id + "-" + firstName + "-" + lastName + "-" + email);

            //Toast.makeText(this, token.toString(), Toast.LENGTH_SHORT).show();

            Snackbar snackbar = Snackbar.make(findViewById(R.id.Layout1), id, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();

            callBack();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.Layout1), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
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
    }

    private void callBack() {
        mqtt.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                if(topic.equals("gh51f5hr55gdfcue684fs61s6v3d54v8/temperature"))
                    temp.setText(new String(message.getPayload()));
                if(topic.equals("gh51f5hr55gdfcue684fs61s6v3d54v8/brightness"))
                    bright.setText(new String(message.getPayload()));
                if(topic.equals("gh51f5hr55gdfcue684fs61s6v3d54v8/fanStatus"))
                    fanSta.setText(new String(message.getPayload()));
                if(topic.equals("gh51f5hr55gdfcue684fs61s6v3d54v8/lampStatus"))
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
