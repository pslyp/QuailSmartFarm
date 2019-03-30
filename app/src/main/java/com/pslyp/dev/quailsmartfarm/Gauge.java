package com.pslyp.dev.quailsmartfarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class Gauge extends AppCompatActivity {

    CustomGauge gauge;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gauge);

        gauge = findViewById(R.id.gauge1);
        button = findViewById(R.id.button_add_value);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gauge.setPointSize(10);
            }
        });
    }
}
