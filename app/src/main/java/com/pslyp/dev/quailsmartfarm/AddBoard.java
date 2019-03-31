package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.pslyp.dev.quailsmartfarm.api.RestAPI;
import com.pslyp.dev.quailsmartfarm.models.Board;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBoard extends AppCompatActivity implements View.OnClickListener {

    Button addBtn;
    TextInputLayout token, name;

    RestAPI restAPI;

    //MQTT
    MQTT mqtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);

        initInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
        restAPI = new RestAPI();
        mqtt = new MQTT(this);

        addBtn = findViewById(R.id.button_add_board);
        token = findViewById(R.id.text_input_board_token);
        name = findViewById(R.id.text_input_board_name);

        findViewById(R.id.button_add_board).setOnClickListener(this);

        mqtt.connected();
    }

    private void addBoard() {
        SharedPreferences sp = getSharedPreferences("LoginPreferences", MODE_PRIVATE);

        String id = sp.getString("id", "");
        String t = token.getEditText().getText().toString();
        String n = name.getEditText().getText().toString();

//        mqtt.publish("user/data/token/insert", (id + "-" + t + "-" + n));

        Call<Board> call = restAPI.getQsfService().insertBoard(id, new Board(t, n));
        call.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(Call<Board> call, Response<Board> response) {

            }

            @Override
            public void onFailure(Call<Board> call, Throwable t) {

            }
        });

        startActivity(new Intent(AddBoard.this, MainActivity.class));
        finish();
    }

}
