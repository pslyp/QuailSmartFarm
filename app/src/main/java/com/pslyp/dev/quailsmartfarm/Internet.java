package com.pslyp.dev.quailsmartfarm;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

public class Internet extends AppCompatActivity {

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfos;

    public Internet() {
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfos = connectivityManager.getActiveNetworkInfo();
    }

    public boolean isConnected() {
        return networkInfos != null && networkInfos.isConnected();
    }
}
