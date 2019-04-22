package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.encrypt.MD5;
import com.pslyp.dev.quailsmartfarm.models.Status;
import com.pslyp.dev.quailsmartfarm.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccount extends AppCompatActivity implements View.OnClickListener {

    MD5 md5;
    RestAPI restAPI;

    Button signUp;
    TextInputLayout email_text, pass_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setTitle("Create New Account");

        initInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //startActivity(new Intent(CreateAccount.this, Authentication.class));
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home :
                //startActivity(new Intent(CreateAccount.this, Authentication.class));
                //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_up :
                createAccount();
                break;
        }
    }

    private void initInstance() {
        md5 = new MD5();
        restAPI = new RestAPI();

        signUp = findViewById(R.id.button_sign_up);
        email_text = findViewById(R.id.text_input_email);
        pass_text = findViewById(R.id.text_input_password);

        findViewById(R.id.button_sign_up).setOnClickListener(this);
    }

    private void createAccount() {
        String id = String.valueOf((int)(Math.random() * 10000) + 1);
        String email = email_text.getEditText().getText().toString();
        String pass = md5.create(pass_text.getEditText().getText().toString());

        User user = new User(id, email, pass);

        Call<User> call = restAPI.getQsfService().createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int status = response.code();

                if(status == 200) {
                    Toast.makeText(CreateAccount.this, "Create Success", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(CreateAccount.this, Authentication.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(CreateAccount.this, "Fail", Toast.LENGTH_SHORT).show();
                Log.e("dasd", t.toString());
            }
        });
    }

}
