package com.pslyp.dev.quailsmartfarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class DeviceList extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        initInstance();
    }

    private void initInstance() {
        final DeviceData deviceData = DeviceData.getInstance();
        deviceData.deviceArrayList = new ArrayList<>();

        //Set<>

        DeviceListAdapter adapter = new DeviceListAdapter(this, R.layout.device_item, );

        mListView.setAdapter(adapter);
    }
}
