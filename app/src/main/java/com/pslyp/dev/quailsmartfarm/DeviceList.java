package com.pslyp.dev.quailsmartfarm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pslyp.dev.quailsmartfarm.adapter.DeviceListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static android.support.constraint.Constraints.TAG;

public class DeviceList extends AppCompatActivity {

    private DeviceData deviceData;

    private BluetoothAdapter bluetoothAdapter;
    private UUID MY_UUID = UUID.fromString("df64cfc9-6b23-4eb0-8f99-e8ca0e06141a");

    private Button mButton;
    private LinearLayout mLinearLayout;
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
        deviceData = DeviceData.getInstance();
        deviceData.deviceArrayList = new ArrayList<>();

        mButton = findViewById(R.id.button_refresh);
        //mLinearLayout = findViewById(R.id.linear_layout);
        mListView = findViewById(R.id.list_view);
        mListView.setOnItemClickListener(mDeviceClick);

        mTextView = findViewById(R.id.text_view);
        mTextView.setVisibility(View.GONE);
        //mListView.setVisibility(View.GONE);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bluetoothAdapter.isDiscovering()) {
                    mTextView.setVisibility(View.GONE);
                    //mListView.setVisibility(View.GONE);

                    bluetoothAdapter.cancelDiscovery();
                    bluetoothAdapter.startDiscovery();

                    // Register for broadcasts when a device is discovered.
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver, filter);

                    mListView.refreshDrawableState();
                }
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

//        final DeviceData deviceData = DeviceData.getInstance();
//        deviceData.deviceArrayList = new ArrayList<>();

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

    private OnItemClickListener mDeviceClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ClientThread clientThread = new ClientThread(deviceData.deviceArrayList.get(i));
            clientThread.start();
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device != null) {
                    mListView.setVisibility(View.VISIBLE);

                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address

//                    DeviceData deviceData = DeviceData.getInstance();
//                    deviceData.deviceArrayList = new ArrayList<>();

                    deviceData.deviceArrayList.add(device);


                    //BluetoothDevice device1 = deviceData.deviceArrayList.get(0);
                    //Log.e("Device name", device1.getName());

                    DeviceListAdapter adapter = new DeviceListAdapter(DeviceList.this, R.layout.device_item, deviceData.deviceArrayList);
                    mListView.setAdapter(adapter);

                    Toast.makeText(context, (device.getName() + "\n" + device.getAddress()), Toast.LENGTH_SHORT).show();
                } else {
                    mTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private class ClientThread extends Thread {
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        public ClientThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mDevice = device;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mSocket = tmp;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
            } catch (IOException connectException) {
                try {
                    mSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }
            //manageMy
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

}
