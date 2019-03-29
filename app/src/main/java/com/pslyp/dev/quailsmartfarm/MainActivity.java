package com.pslyp.dev.quailsmartfarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.models.Board;
import com.pslyp.dev.quailsmartfarm.models.User;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Button two, bluetooth, mqtt1, mqtt2, signOut_btn;
    TextView temp, bright, fanSta, lampSta;
    LinearLayout linearLayout1;

    RestAPI restAPI;
    ArrayList<String> tokenList;

    //MQTT
    MQTT mqtt;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    //Google Sign out
    private Google google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences
        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        //Check Login
        boolean isLogin = sp.getBoolean("log_in", false);
        if(!isLogin) {
            startActivity(new Intent(MainActivity.this, Authentication.class));
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
        getMenuInflater().inflate(R.menu.main, menu);
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
                //Toast.makeText(this, "Bluetooth", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, configNetwork.class));
                finish();
                return true;
            case R.id.wifi_submenu:
                Toast.makeText(this, "WiFi", Toast.LENGTH_SHORT).show();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_sign_out:

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

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initInstance() {
        mqtt = new MQTT(this);
        google = new Google(this);
        restAPI = new RestAPI();

        tokenList = new ArrayList<String>();

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
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        findViewById(R.id.button_bluetooth).setOnClickListener(this);
        findViewById(R.id.button_mqtt1).setOnClickListener(this);
        findViewById(R.id.button_mqtt2).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //Internet connected?
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfos = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfos != null &&
                              networkInfos.isConnected();

        if(isConnected) {
            mqtt.connected();

            sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String id = sp.getString("id", "");
            String firstName = sp.getString("first_name", "");
            String lastName = sp.getString("last_name", "");
            String email = sp.getString("email", "");

            //String user = (id + "-" + firstName + "-" + lastName + "-" + email);

            Call<User> call = restAPI.getQsfService().getBoard("117699091589038964647");
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();

                    List<Board> boards = user.getBoard();

                    if(boards != null) {
                        String b = "";
                        for (Board board : boards) {
                            b += board.getToken();
                            tokenList.add(board.getToken());
                        }

                        Toast.makeText(MainActivity.this, b, Toast.LENGTH_SHORT).show();
                        Log.e("Response", b);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });

            //Toast.makeText(this, token.toString(), Toast.LENGTH_SHORT).show();3

            Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), id, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();

            callBack();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
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
        google.mGoogleSignInClient().signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

                        editor = sp.edit();
                        editor.clear();
                        editor.commit();

                        startActivity(new Intent(MainActivity.this, Authentication.class));
                        finish();
                    }
                });
    }
}
