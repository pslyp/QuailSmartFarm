package com.pslyp.dev.quailsmartfarm.activities;

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

import com.pslyp.dev.quailsmartfarm.MQTT;
import com.pslyp.dev.quailsmartfarm.MainActivity;
import com.pslyp.dev.quailsmartfarm.R;
import com.pslyp.dev.quailsmartfarm.ScanActivity;
import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.encrypt.MD5;
import com.pslyp.dev.quailsmartfarm.models.Device;
import com.pslyp.dev.quailsmartfarm.models.DeviceListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBoardActivity extends AppCompatActivity implements View.OnClickListener {

    Button addBtn, scanQrCodeBtn;
    TextView mTitle;
    TextInputLayout boardTokenTextInput, nameTokenTextInput;

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
                startActivityForResult(new Intent(AddBoardActivity.this, ScanActivity.class), SCAN_QR_CODE);
                break;
            case R.id.button_add_board:
                checkBoard();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(AddBoardActivity.this, MainActivity.class));
        finish();
    }

    private void initInstance() {
        md5 = new MD5();
        restAPI = new RestAPI();
        mqtt = new MQTT(this);

        mTitle = findViewById(R.id.toolbar_title);
        addBtn = findViewById(R.id.button_add_board);
        scanQrCodeBtn = findViewById(R.id.button_scan_qr_code);
        boardTokenTextInput = findViewById(R.id.text_input_board_token);
        nameTokenTextInput = findViewById(R.id.text_input_board_name);

        addBtn.setOnClickListener(this);
        scanQrCodeBtn.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        mTitle.setText("Add Device");
        setTitle("");

        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        mqtt.connect();
    }

    private void checkBoard() {
        final String id = sp.getString("ID", "");
//        final String tokenString = sp.getString("BOARD_TOKEN", "");
//        final String id = "117699091589038964647";
//        final String board_token = "4C31A6DBCD72FF1171332936EFDBF273";
        String board_token = boardTokenTextInput.getEditText().getText().toString();
        String board_name = nameTokenTextInput.getEditText().getText().toString();

        if(checkInputEmpty(board_token, board_name)) {
            return;
        }

        final String board_token_md5 = md5.create(board_token);

//            Toast.makeText(this, "Lenght 0", Toast.LENGTH_SHORT).show();
//        String board_token = md5.create(boardTokenTextInput.getEditText().getText().toString());

        Log.e("Add", id);

        Call<DeviceListResponse> call = restAPI.getQsfService().getBoardByIdAndToken(id, board_token_md5);
        call.enqueue(new Callback<DeviceListResponse>() {
            @Override
            public void onResponse(Call<DeviceListResponse> call, Response<DeviceListResponse> response) {
                int status = response.code();

//                Toast.makeText(AddBoardActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();

                if(status == 200) {
                    Toast.makeText(AddBoardActivity.this, "Device ID is already used", Toast.LENGTH_SHORT).show();
                }
                if(status == 204){
                    addBoard(id, board_token_md5);
                }
            }

            @Override
            public void onFailure(Call<DeviceListResponse> call, Throwable t) {
                Log.e("Check Device Add Device", t.getMessage());
            }
        });

    }

    private void addBoard(final String id, final String tokenString) {
//        final String tokenString = md5.create(boardTokenTextInput.getEditText().getText().toString());
//        String t = token.getEditText().getText().toString();
        String n = nameTokenTextInput.getEditText().getText().toString();
        final String personToken = sp.getString("PERSON_TOKEN", "");

//        Toast.makeText(this, tokenString, Toast.LENGTH_SHORT).show();

        editor = sp.edit();
        editor.putString("BOARD_TOKEN", tokenString);
        editor.commit();

//        Call<Device> call = restAPI.getQsfService().insertBoard(id, new Device(tokenString, n, 3000, 36, "0800", "2200"));
        Call<Device> call = restAPI.getQsfService().insertBoard(id, new Device(tokenString, n, 3000, 36, "14", "0800", "2300"));
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                int status = response.code();

                if(status == 204) {
//                    if(mqtt.isConnected()) {
//                        mqtt.publish("/" + tokenString + "/cloudMessage", personToken.substring(0, 70) + ">1");
//                        mqtt.publish("/" + tokenString + "/cloudMessage", personToken.substring(70, 140) + ">2");
//                        mqtt.publish("/" + tokenString + "/cloudMessage", personToken.substring(140) + ">3");
//                    }

                    Toast.makeText(AddBoardActivity.this, "Add Device success", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AddBoardActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(AddBoardActivity.this, "Add Device fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {

            }
        });
    }

    private boolean checkInputEmpty(String board_token, String board_name) {
        if(board_token.isEmpty() || board_name.isEmpty()) {
            if(board_token.isEmpty()) {
                boardTokenTextInput.setError("Device ID can't be empty");
            } else {
                boardTokenTextInput.setError(null);
            }
            if(board_name.isEmpty()) {
                nameTokenTextInput.setError("Device Name can't be empty");
            } else{
                nameTokenTextInput.setError(null);
            }
            return true;
        } else {
            boardTokenTextInput.setError(null);
            nameTokenTextInput.setError(null);
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SCAN_QR_CODE) {
            if(resultCode == RESULT_OK) {
                String barcode = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(this, barcode, Toast.LENGTH_SHORT).show();

                boardTokenTextInput.getEditText().setText(barcode);
            }
        }
    }
}
