package com.pslyp.dev.quailsmartfarm;

import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class Gauge extends AppCompatActivity {

    CustomGauge gauge;
    Button button;
    LinearLayout layout1, layout2;

    private ObjectAnimator animator = new ObjectAnimator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gauge);

        gauge = findViewById(R.id.gauge1);
        button = findViewById(R.id.button_add_value);
        layout1 = findViewById(R.id.linear_layout_1);
        layout2 = findViewById(R.id.linear_layout_2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gauge.setPointSize(100);

                animator.setTarget(layout2);
                animator.setPropertyName("scaleX");
                animator.setFloatValues((float) layout2.getScaleX(), (float) layout1.getScaleX());
                animator.start();

                showNotification();
            }
        });
    }

    private void showNotification() {
        Intent intent = new Intent(this, Gauge.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.com_facebook_button_icon)
                .setContentTitle("Quail Smart Farm")
                .setContentText("Fireeeee")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //Intent intent = new Intent(this, Alert)

        int notificationId = 1010;
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(notificationId, builder.build());
    }
}
