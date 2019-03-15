package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setTitle("Create New Account");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(CreateAccount.this, Authentication.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
