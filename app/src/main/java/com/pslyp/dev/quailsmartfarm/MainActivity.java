package com.pslyp.dev.quailsmartfarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTitle;
    private Fragment dashBoard, config, me;

    //Google
    private Google google;

    //SharedPreferences
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private final String PREF_NAME = "LoginPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstance();

//        BottomNavigationView navigation = findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        Toast.makeText(this, FirebaseInstanceId.getInstance().getMsgToken(), Toast.LENGTH_SHORT).show();

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check Login
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isLogIn = sp.getBoolean("LOG_IN", false);

        if(!isLogIn) {
            startActivity(new Intent(MainActivity.this, Authentication.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu:
                //Toast.makeText(this, "Add board", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, AddBoard.class));
                finish();
//                AddBoardDialog addBoardDialog = new AddBoardDialog();
//                addBoardDialog.show(getSupportFragmentManager(), "add board");
                return true;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initInstance() {
        google = new Google(this);

        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_dash_board);
        dashBoard = new DashBoardFragment();
        config = new ConfigFragment();

        mTitle.setText("Dashboard");
        setTitle("");
        loadFragment(dashBoard);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
//        Fragment fragment = null;
//
//        if(item.isCheckable()) {
//            item.setChecked(true);
//        } else {
//            item.setChecked(false);
//        }

        int id = item.getItemId();

        if(id == R.id.nav_dash_board) {
            mTitle.setText("Dashboard");
//            setTitle("Dashboard");
            loadFragment(dashBoard);
        }
        if(id == R.id.nav_configs) {
            mTitle.setText("Configs");
//            setTitle("Configs");
            loadFragment(config);
        }
        if(id == R.id.nav_sign_out) {
            item.setChecked(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to log out?")
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            item.setChecked(false);
                        }
                    })
                    .setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            signOut();
                        }
                    });
            builder.show();
        }

//        loadFragment(fragment);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//            Fragment fragment;
//            switch (menuItem.getItemId()) {
//                case R.id.navigation_home:
//                    setTitle("Home");
////                    fragment = new HomeFragment();
//                    loadFragment(home);
//                    return true;
//                case R.id.navigation_configs:
//                    setTitle("Config");
////                    fragment = new ConfigFragment();
//                    loadFragment(config);
//                    return true;
//                case R.id.navigation_me:
//                    setTitle("Me");
////                    fragment = new MeFragment();
//                    loadFragment(me);
//                    return true;
//            }
//            return false;
//        }
//    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        for (Fragment fragments:getSupportFragmentManager().getFragments()) {
            if (fragments instanceof DashBoardFragment) {
                continue;
            }
            else if (fragments != null) {
                getSupportFragmentManager().beginTransaction().remove(fragments).commit();
            }
        }
    }

    private void signOut() {
        google.mGoogleSignInClient().signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editor = sp.edit();
                        editor.clear();
                        editor.commit();

                        startActivity(new Intent(MainActivity.this, Authentication.class));
                        finish();
                    }
                });
    }
}
