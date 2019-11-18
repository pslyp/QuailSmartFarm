//package com.pslyp.dev.quailsmartfarm;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.support.design.widget.NavigationView;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.pslyp.dev.quailsmartfarm.api.RestAPI;
//import com.pslyp.dev.quailsmartfarm.models.Device;
//import com.pslyp.dev.quailsmartfarm.models.User;
//
//import org.eclipse.paho.android.service.MqttAndroidClient;
//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class DeviceDashBoard extends AppCompatActivity {
//
//    //Permission request code
//    private final int CAMERA_PERMISSION = 1001;
//    private final int LOCATION_PERMISSION = 1002;
//
//    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 2000;
//
//    //Rest API
//    private RestAPI restAPI;
//    private ArrayList<String> tokenList;
//
//    //MQTT
////    private MQTT mqtt;
//    private int indexToken = 0;
//    private String board_token = "";
//
//    DrawerLayout drawerLayout;
//    NavigationView navigationView;
//
//    Button two, bluetooth;
//    ImageView acc_pic, imageView, lampStatus, fanStatus;
//    LinearLayout linearLayout1, dashboard;
//    ProgressBar progressBar;
//    RelativeLayout no_dashboard;
//    TextView temp, bright, emailAcc;
//
//    //Shared Preferences
//    SharedPreferences sp;
//    SharedPreferences.Editor editor;
//    final String PREF_NAME = "LoginPreferences";
//
//    //Google Sign out
//    private Google google;
//
//    //private String id;
//    private final String TAG = "MainActivity";
//
//    private Button b1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_device_dash_board);
//
//        initInstance();
//
////        FragmentManager manager = getSupportFragmentManager();
////        FragmentTransaction transaction = manager.beginTransaction();
////
////        HomeFragment homeFragment = new HomeFragment();
////        transaction.add(R.id.frame1, homeFragment);
////        transaction.commit();
//
//
//    }
//
//    public void getHomeFragment(String token) {
//        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
//    }
//
//    //    @Override
////    public void onClick(View view) {
//////        switch (view.getId()) {
//////
//////        }
////    }
////
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.main, menu);
////        return super.onCreateOptionsMenu(menu);
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        switch (item.getItemId()) {
////            case R.id.add_menu:
////                //Toast.makeText(this, "Add board", Toast.LENGTH_SHORT).show();
//////                Intent intent = new Intent(MainActivity.this, AddBoardActivity.class);
//////                startActivity(intent);
//////                finish();
//////                AddBoardDialog addBoardDialog = new AddBoardDialog();
//////                addBoardDialog.show(getSupportFragmentManager(), "add board");
////                return true;
////            case R.id.bluetooth_submenu:
////                //Toast.makeText(this, "Bluetooth", Toast.LENGTH_SHORT).show();
////                startActivity(new Intent(MainActivity.this, Setting.class));
////                finish();
////                return true;
////            case R.id.wifi_submenu:
////                Toast.makeText(this, "WiFi", Toast.LENGTH_SHORT).show();
////                startActivity(new Intent(MainActivity.this, SmartConfigWiFI.class));
////                finish();
////                return true;
////            default:
////                return super.onOptionsItemSelected(item);
////        }
////    }
////
////    @Override
////    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
////        switch (menuItem.getItemId()) {
////            case R.id.nav_gauge :
////                startActivity(new Intent(MainActivity.this, Gauge.class));
////                finish();
////                break;
////            case R.id.nav_sign_out :
////
////                //Show dialog
////                AlertDialog.Builder builder = new AlertDialog.Builder(this);
////                builder.setMessage("Are you sure you want to log out?");
////                builder.setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                        //Sign out Google
////                        signOut();
////                    }
////                })
////                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialogInterface, int i) {
////
////                            }
////                        });
////                builder.show();
////                break;
////        }
////
////        drawerLayout.closeDrawer(GravityCompat.START);
////        return true;
////    }
////
//    private void initInstance() {
////        mqtt = new MQTT(this);
//        google = new Google(this);
//        restAPI = new RestAPI();
//
//        tokenList = new ArrayList<>();
//
//        progressBar = findViewById(R.id.progress_bar);
//        linearLayout1 = findViewById(R.id.linear_layout_1);
//        dashboard = findViewById(R.id.linear_layout_dashboard);
//        no_dashboard = findViewById(R.id.relative_layout_no_dashboard);
//        temp = findViewById(R.id.text_view_temp);
////        temp.setText("---");
//        bright = findViewById(R.id.text_view_bright);
////        bright.setText("---");
//        fanStatus = findViewById(R.id.image_view_fan_status);
////        fanStatus.setBackgroundResource(R.drawable.shape_status_default);
//        lampStatus = findViewById(R.id.image_view_lamp_status);
////        lampStatus.setBackgroundResource(R.drawable.shape_status_default);
//        b1 = findViewById(R.id.button1);
//
//
////        drawerLayout = findViewById(R.id.drawer_layout);
////        navigationView = view.findViewById(R.id.nav_view);
//
////        Toolbar toolbar = view.findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);
//
////        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
////        //drawerLayout.addDrawerListener(toggle);
////        toggle.syncState();
//
////        navigationView.setNavigationItemSelectedListener(this);
//
//        //Internet connected?
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfos = connectivityManager.getActiveNetworkInfo();
//
//        boolean isConnected = networkInfos != null &&
//                networkInfos.isConnected();
//
//        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        String id = sp.getString("ID", "");
//        String firstName = sp.getString("FIRST_NAME", "");
//        String lastName = sp.getString("LAST_NAME", "");
//        String email = sp.getString("EMAIL", "");
//        String personToken = sp.getString("PERSON_TOKEN", "");
//        String photo_url = sp.getString("URL_PHOTO", "");
//
//        if (isConnected) {
//            mqtt.connectMQTT();
//
//            Log.e("Photo", photo_url);
//
//            final String sender = getIntent().getExtras().getString("SENDER_KEY");
//            if (sender != null) {
//                Intent intent = getIntent();
//                board_token = intent.getStringExtra("TOKEN");
//                Toast.makeText(this, board_token, Toast.LENGTH_SHORT).show();
//            }
//
//            b1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mqtt.publish(board_token + "/cloudMessage", "dasd");
//                }
//            });
//
////            String personToken = "cwui9n92gqM:APA91bE5fYxbMAV_ZFAwmRdg7hoXvGcPobCXF_Pli93n80bEoNuEwIgO2csSqbXTVJvuJuVhpCQ7iiADUWQnLTU3y7mj0pWrzlQwXXqT6Oh_Oi98-6Dni0NcTP40gt_jlXXYbLWSoAih";
////            publish("/cloudMessage", "dasd");
////
//            //emailAcc.setText(email);
//
//            //acc_pic.setImageResource(R.drawable.com_facebook_button_icon);
//
//            //if(photo_url != null)
//            //Glide.with(MainActivity.this).load("http://goo.gl/gEgYUd").into(acc_pic);
//            //
////            getBoardList(id);
//
////            if(!tokenList.isEmpty()) {
////                setDashBoard(tokenList);
////                Toast.makeText(this, "Token not empty", Toast.LENGTH_SHORT).show();
////
////                for(String token : tokenList) {
////                    Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
////                }
////            }
//
////            Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), id, Snackbar.LENGTH_INDEFINITE);
////            snackbar.show();
////
//            callBack();
//        } else {
////            Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
////            snackbar.show();
//            lampStatus.setBackgroundResource(R.drawable.shape_status_default);
//        }
//
//    }
//
//    private void getBoardList(String id) {
//        Log.e("ID", id);
//
//        Call<User> call = restAPI.getQsfService().getDevice(id);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//
//                int status = response.code();
//                if (status == 200) {
//                    User user = response.body();
//                    List<Device> boards = user.getDevice();
//
//                    String b = "";
//                    for (Device board : boards) {
//                        b += board.getToken();
//                        tokenList.add(board.getToken());
//                    }
//
//                    setDashBoard(tokenList);
//
////                    Toast.makeText(DeviceDashBoard.this, b, Toast.LENGTH_SHORT).show();
//                    Log.e("Set Dashboard", b);
//                } else if (status == 204) {
//                    tokenList.clear();
//
//                    setDashBoard(tokenList);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Log.e("Set Dashboard", t.toString());
//            }
//        });
//    }
//
//    private void setDashBoard(List<String> tokenList) {
//        if (!tokenList.isEmpty()) {
//            Log.e("Device", "not empty");
//
//            dashboard.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
//        } else {
//            no_dashboard.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
//        }
//    }
//
//    private void callBack() {
//        mqtt.client.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) {
//                if (topic.equals(board_token + "/temperature"))
//                    temp.setText(new String(message.getPayload()));
//                if (topic.equals(board_token + "/brightness"))
//                    bright.setText(new String(message.getPayload()));
//                if (topic.equals(board_token + "/fanStatus")) {
//                    if (new String(message.getPayload()).equals("ON")) {
//                        fanStatus.setBackgroundResource(R.drawable.shape_status_green);
//                    } else {
//                        fanStatus.setBackgroundResource(R.drawable.shape_status_red);
//                    }
//                }
//                if (topic.equals(board_token + "/lampStatus")) {
//                    if (new String(message.getPayload()).equals("ON")) {
//                        lampStatus.setBackgroundResource(R.drawable.shape_status_green);
//                    } else {
//                        lampStatus.setBackgroundResource(R.drawable.shape_status_red);
//                    }
//                }
//
//                /*
//                switch (topic) {
//                    case "temperature":
//                        temp.setText(new String(message.getPayload()));
//                        break;
//                    case "brightness":
//                        bright.setText(new String(message.getPayload()));
//                        break;
//                    case "fanStatus":
//                        fanSta.setText(new String(message.getPayload()));
//                        break;
//                    case "lampStatus":
//                        lampSta.setText(new String(message.getPayload()));
//                        break;
//                    default: break;
//                }
//                */
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//    }
//
//}
