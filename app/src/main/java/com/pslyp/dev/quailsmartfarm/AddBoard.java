package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.encrypt.MD5;
import com.pslyp.dev.quailsmartfarm.models.Board;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBoard extends AppCompatActivity implements View.OnClickListener {

    Button addBtn, scanQrCodeBtn;
    TextInputLayout token, name;

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
                addBoard();
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

        addBtn = findViewById(R.id.button_add_board);
        scanQrCodeBtn = findViewById(R.id.button_scan_qr_code);
        token = findViewById(R.id.text_input_board_token);
        name = findViewById(R.id.text_input_board_name);

        addBtn.setOnClickListener(this);
        scanQrCodeBtn.setOnClickListener(this);

        mqtt.connect();
    }

    private void addBoard() {
        SharedPreferences sp = getSharedPreferences("LoginPreferences", MODE_PRIVATE);

        String id = sp.getString("ID", "");
//        final String t = md5.create(token.getEditText().getText().toString());
        String t = token.getEditText().getText().toString();
        String n = name.getEditText().getText().toString();

//        mqtt.publish("user/data/token/insert", (id + "-" + t + "-" + n));
        Log.e("Add", id);

        Call<Board> call = restAPI.getQsfService().updateUser(id, new Board(t, n, 120, 28, String.valueOf(1600), String.valueOf(2230)));
        call.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(Call<Board> call, Response<Board> response) {
//                mqtt.subscribe(t + "/brightness", 1);
//                mqtt.subscribe(t + "/temperature", 1);
//                mqtt.subscribe(t + "/fanStatus", 1);
//                mqtt.subscribe(t + "/lampStatus", 1);

                startActivity(new Intent(AddBoard.this, MainActivity.class));
                finish();
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
