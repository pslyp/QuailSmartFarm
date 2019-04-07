package com.pslyp.dev.quailsmartfarm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pslyp.dev.quailsmartfarm.adapter.DeviceListAdapter;

import java.util.ArrayList;

public class DeviceList extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    private ListView mListView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        initInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    private void initInstance() {
        mListView = findViewById(R.id.list_view);
        mTextView = findViewById(R.id.text_view);
        mTextView.setVisibility(View.GONE);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        final DeviceData deviceData = DeviceData.getInstance();
        deviceData.deviceArrayList = new ArrayList<>();

//        try {
//            Set<BluetoothDevice> pairedDeivce = bluetoothAdapter.getBondedDevices();
//
//            if (pairedDeivce.size() > 0) {
//                mTextView.setVisibility(View.GONE);
//                for (BluetoothDevice device : pairedDeivce) {
//                    String deviceName = device.getName();
//                    String deviceHardwareAddress = device.getAddress();
//
//                    deviceData.deviceArrayList.add(new BTDevice(deviceName, deviceHardwareAddress));
//                }
//
//                DeviceListAdapterEX adapter = new DeviceListAdapterEX(this, R.layout.device_item, deviceData.deviceArrayList);
//                mListView.setAdapter(adapter);
//            }
//        } catch (NullPointerException e) {
//            mListView.setVisibility(View.GONE);
//            Log.e("Bluetooth device", e.toString());
//        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                DeviceData deviceData = DeviceData.getInstance();
                deviceData.deviceArrayList = new ArrayList<>();

                deviceData.deviceArrayList.add(device);

                DeviceListAdapter adapter = new DeviceListAdapter(DeviceList.this, R.layout.device_item, deviceData.deviceArrayList);
                mListView.setAdapter(adapter);

                Toast.makeText(context, (device.getName() + "\n" + device.getAddress()), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
