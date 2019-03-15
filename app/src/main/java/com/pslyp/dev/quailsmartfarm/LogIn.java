package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setTitle("Log In");

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent authIntent = new Intent(LogIn.this, Authentication.class);
        startActivity(authIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toast.makeText(this, String.valueOf(id) + "-" + R.id.home, Toast.LENGTH_SHORT).show();


        switch (item.getItemId()) {
            case R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LogIn.this, Authentication.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
