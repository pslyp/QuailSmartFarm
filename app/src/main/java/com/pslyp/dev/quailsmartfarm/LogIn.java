package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.encrypt.MD5;
import com.pslyp.dev.quailsmartfarm.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogIn extends AppCompatActivity implements View.OnClickListener {

    MD5 md5;
    RestAPI restAPI;

    Button login;
    TextView mTitle;
    TextInputLayout email_text, pass_text;

    //Shared Preferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "LoginPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setTitle("Log In");

        initInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Intent authIntent = new Intent(LogIn.this, Authentication.class);
        //startActivity(authIntent);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                //Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(LogIn.this, Authentication.class));
                //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_log_in :
                logIn();
                break;
            default:
        }
    }

    private void initInstance() {
        md5 = new MD5();
        restAPI = new RestAPI();

        mTitle = findViewById(R.id.toolbar_title);
        login = findViewById(R.id.button_log_in);
        email_text = findViewById(R.id.text_input_email);
        pass_text = findViewById(R.id.text_input_password);

        findViewById(R.id.button_log_in).setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        mTitle.setText("Log In");
        setTitle("");
    }

    private void logIn() {
        String email = email_text.getEditText().getText().toString();
        String pass = md5.create(pass_text.getEditText().getText().toString());

        email_text.setErrorEnabled(false);
        pass_text.setErrorEnabled(false);

        Call<User> call = restAPI.getQsfService().logIn(email, pass);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                int status = response.code();
                Log.e("Code", String.valueOf(status));
                if(status == 200) {
                    User user = response.body();

                    String id = user.getId();
                    String first_name = user.getFirstname();
                    String last_name = user.getLastname();
                    String email = user.getEmail();
                    String personToken = FirebaseInstanceId.getInstance().getToken();

                    sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

                    editor = sp.edit();
                    editor.putBoolean("LOG_IN", true);
                    editor.putString("ID", id);
                    editor.putString("FIRST_NAME", first_name);
                    editor.putString("LAST_NAME", last_name);
                    editor.putString("EMAIL", email);
                    editor.putString("PERSON_TOKEN", personToken);
                    editor.commit();

                    startActivity(new Intent(LogIn.this, MainActivity.class));
                    finish();
                }
                if(status == 204) {
                    pass_text.setError("Invalid password");
                    Log.e("Login", "password");
                }
                if(status == 400){
                    email_text.setError("Invalid email");
                    Log.e("Login", "email");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

    private void setLogin() {

    }

}
