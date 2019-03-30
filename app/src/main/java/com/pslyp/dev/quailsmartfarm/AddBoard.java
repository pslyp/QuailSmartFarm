package com.pslyp.dev.quailsmartfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AddBoard extends AppCompatActivity implements View.OnClickListener {

    Button addBtn;
    TextInputLayout token, name;

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

        mqtt.publish("user/data/token/insert", (id + "-" + t + "-" + n));

        startActivity(new Intent(AddBoard.this, MainActivity.class));
        finish();
    }
}
