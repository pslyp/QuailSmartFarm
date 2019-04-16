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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //Permission request code
    private final int CAMERA_PERMISSION   = 1001;
    private final int LOCATION_PERMISSION = 1002;

    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 2000;

    //Rest API
    private RestAPI restAPI;
    private ArrayList<String> tokenList;

    //MQTT
    MQTT mqtt;
    private int indexToken = 0;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Button two, bluetooth;
    ImageView acc_pic, imageView;
    LinearLayout linearLayout1, dashboard;
    ProgressBar progressBar;
    RelativeLayout no_dashboard;
    TextView temp, bright, fanSta, lampSta, emailAcc;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    //Google Sign out
    private Google google;

    //private String id;
    private final String TAG = "MainActivity";

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initInstance(view);

        return view;
    }

//    @Override
//    public void onClick(View view) {
////        switch (view.getId()) {
////
////        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.add_menu:
//                //Toast.makeText(this, "Add board", Toast.LENGTH_SHORT).show();
////                Intent intent = new Intent(MainActivity.this, AddBoard.class);
////                startActivity(intent);
////                finish();
////                AddBoardDialog addBoardDialog = new AddBoardDialog();
////                addBoardDialog.show(getSupportFragmentManager(), "add board");
//                return true;
//            case R.id.bluetooth_submenu:
//                //Toast.makeText(this, "Bluetooth", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MainActivity.this, Setting.class));
//                finish();
//                return true;
//            case R.id.wifi_submenu:
//                Toast.makeText(this, "WiFi", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MainActivity.this, SmartConfigWiFI.class));
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.nav_gauge :
//                startActivity(new Intent(MainActivity.this, Gauge.class));
//                finish();
//                break;
//            case R.id.nav_sign_out :
//
//                //Show dialog
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("Are you sure you want to log out?");
//                builder.setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //Sign out Google
//                        signOut();
//                    }
//                })
//                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//                builder.show();
//                break;
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
    private void initInstance(View view) {
        mqtt = new MQTT(getContext());
        google = new Google(getContext());
        restAPI = new RestAPI();

        tokenList = new ArrayList<>();

        progressBar = view.findViewById(R.id.progress_bar);
        linearLayout1 = view.findViewById(R.id.linear_layout_1);
        dashboard = view.findViewById(R.id.linear_layout_dashboard);
        no_dashboard = view.findViewById(R.id.relative_layout_no_dashboard);
        temp = view.findViewById(R.id.text_view_temp);
        bright = view.findViewById(R.id.text_view_bright);
        fanSta = view.findViewById(R.id.text_view_fan_status);
        lampSta = view.findViewById(R.id.text_view_lamp_status);
        drawerLayout = view.findViewById(R.id.drawer_layout);
//        navigationView = view.findViewById(R.id.nav_view);

//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        //drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();

//        navigationView.setNavigationItemSelectedListener(this);

        //Internet connected?
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfos = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfos != null &&
                networkInfos.isConnected();

        sp = getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String id = sp.getString("ID", "");
        String firstName = sp.getString("FIRST_NAME", "");
        String lastName = sp.getString("LAST_NAME", "");
        String email = sp.getString("EMAIL", "");
        String photo_url = sp.getString("URL_PHOTO", "");

        if(isConnected) {
            mqtt.connected();

            Log.e("Photo", photo_url);

            //emailAcc.setText(email);

            //acc_pic.setImageResource(R.drawable.com_facebook_button_icon);

            //if(photo_url != null)
            //Glide.with(MainActivity.this).load("http://goo.gl/gEgYUd").into(acc_pic);
            //
            getBoardList(id);

//            if(!tokenList.isEmpty()) {
//                setDashBoard(tokenList);
//                Toast.makeText(this, "Token not empty", Toast.LENGTH_SHORT).show();
//
//                for(String token : tokenList) {
//                    Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
//                }
//            }

//            Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), id, Snackbar.LENGTH_INDEFINITE);
//            snackbar.show();
//
            callBack();
        } else {
//            Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
//            snackbar.show();
        }

    }

    private void getBoardList(String id) {
        Log.e("ID", id);

        Call<User> call = restAPI.getQsfService().getBoard(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                int status = response.code();
                if(status == 200) {
                    User user = response.body();
                    List<Board> boards = user.getBoard();

                    String b = "";
                    for (Board board : boards) {
                        b += board.getToken();
                        tokenList.add(board.getToken());
                    }

                    setDashBoard(tokenList);

                    Toast.makeText(getContext(), b, Toast.LENGTH_SHORT).show();
                    Log.e("Set Dashboard", b);
                } else if(status == 204) {
                    tokenList.clear();

                    setDashBoard(tokenList);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Set Dashboard", t.toString());
            }
        });
    }

    private void setDashBoard(List<String> tokenList) {
        if(!tokenList.isEmpty()) {
            Log.e("Board", "not empty");

            dashboard.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            no_dashboard.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void callBack() {
        mqtt.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                if(topic.equals(tokenList.get(indexToken) + "/temperature"))
                    temp.setText(new String(message.getPayload()));
                if(topic.equals(tokenList.get(indexToken) + "/brightness"))
                    bright.setText(new String(message.getPayload()));
                if(topic.equals(tokenList.get(indexToken) + "/fanStatus"))
                    fanSta.setText(new String(message.getPayload()));
                if(topic.equals(tokenList.get(indexToken) + "/lampStatus"))
                    lampSta.setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
//
//    private void signOut() {
//        google.mGoogleSignInClient().signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//
//                        editor = sp.edit();
//                        editor.clear();
//                        editor.commit();
//
//                        startActivity(new Intent(MainActivity.this, Authentication.class));
//                        finish();
//                    }
//                });
//    }


}
