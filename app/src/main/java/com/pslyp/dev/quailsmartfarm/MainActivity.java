package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String PREF_NAME = "LoginPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstance();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check Login
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isLogIn = sp.getBoolean("LOG_IN", false);

        if(!isLogIn) {
            startActivity(new Intent(MainActivity.this, Authentication.class));
            finish();
        } else {
            loadFragment(new HomeFragment());
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_configs:
                    fragment = new ConfigFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_me:
                    fragment = new MeFragment();
                    loadFragment(fragment);
                    return true;

            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
