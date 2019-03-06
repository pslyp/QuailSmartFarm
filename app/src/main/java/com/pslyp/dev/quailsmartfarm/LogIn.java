package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent authIntent = new Intent(LogIn.this, Authentication.class);
        startActivity(authIntent);
        finish();
    }
}
