package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.encrypt.MD5;
import com.pslyp.dev.quailsmartfarm.models.Board;
import com.pslyp.dev.quailsmartfarm.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBoard extends AppCompatActivity implements View.OnClickListener {

    Button addBtn, scanQrCodeBtn;
    TextView mTitle;
    TextInputLayout token, name;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String PREF_NAME = "LoginPreferences";

    MD5 md5;
    RestAPI restAPI;

    //MQTT
    MQTT mqtt;

    private final int SCAN_QR_CODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);

        initInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_scan_qr_code:
                startActivityForResult(new Intent(AddBoard.this, ScanActivity.class), SCAN_QR_CODE);
                break;
            case R.id.button_add_board:
                checkBoard();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(AddBoard.this, MainActivity.class));
        finish();
    }

    private void initInstance() {
        md5 = new MD5();
        restAPI = new RestAPI();
        mqtt = new MQTT(this);

        mTitle = findViewById(R.id.toolbar_title);
        addBtn = findViewById(R.id.button_add_board);
        scanQrCodeBtn = findViewById(R.id.button_scan_qr_code);
        token = findViewById(R.id.text_input_board_token);
        name = findViewById(R.id.text_input_board_name);

        addBtn.setOnClickListener(this);
        scanQrCodeBtn.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        mTitle.setText("Add Board");
        setTitle("");

        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        mqtt.connect();
    }

    private void checkBoard() {
//        final String id = sp.getString("ID", "");
        final String tokenString = sp.getString("BOARD_TOKEN", "");
        final String id = "117699091589038964647";
//        final String board_token = "4C31A6DBCD72FF1171332936EFDBF273";

        Log.e("Add", id);

        Call<User> call = restAPI.getQsfService().getBoardByToken(id, tokenString);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                int status = response.code();

                Toast.makeText(AddBoard.this, String.valueOf(status), Toast.LENGTH_SHORT).show();

                if(status == 200) {
                    Toast.makeText(AddBoard.this, "Token sum", Toast.LENGTH_SHORT).show();
                }
                if(status == 204){
                    addBoard(id);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void addBoard(final String id) {
        final String tokenString = md5.create(token.getEditText().getText().toString());
//        String t = token.getEditText().getText().toString();
        String n = name.getEditText().getText().toString();
        final String personToken = sp.getString("PERSON_TOKEN", "");

        Toast.makeText(this, tokenString, Toast.LENGTH_SHORT).show();

        editor = sp.edit();
        editor.putString("BOARD_TOKEN", tokenString);
        editor.commit();

//        Call<Board> call = restAPI.getQsfService().insertBoard(id, new Board(tokenString, n, 3000, 36, "0800", "2200"));
        Call<Board> call = restAPI.getQsfService().insertBoard(id, new Board(tokenString, n, 3000, 36, "14", "0800", "2300"));
        call.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(Call<Board> call, Response<Board> response) {
                int status = response.code();

                if(status == 204) {
//                    if(mqtt.isConnected()) {
//                        mqtt.publish("/" + tokenString + "/cloudMessage", personToken.substring(0, 70) + ">1");
//                        mqtt.publish("/" + tokenString + "/cloudMessage", personToken.substring(70, 140) + ">2");
//                        mqtt.publish("/" + tokenString + "/cloudMessage", personToken.substring(140) + ">3");
//                    }

                    startActivity(new Intent(AddBoard.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(AddBoard.this, "Add board fail.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Board> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SCAN_QR_CODE) {
            if(resultCode == RESULT_OK) {
                String barcode = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(this, barcode, Toast.LENGTH_SHORT).show();

                token.getEditText().setText(barcode);
            }
        }
    }
}
