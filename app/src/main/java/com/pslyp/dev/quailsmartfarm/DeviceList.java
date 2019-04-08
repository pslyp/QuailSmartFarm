package com.pslyp.dev.quailsmartfarm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.support.constraint.Constraints.TAG;

public class DeviceList extends AppCompatActivity implements View.OnClickListener {

    private DeviceData deviceData;

    private Handler handler;

    private BluetoothAdapter bluetoothAdapter;
    private ConnectedThread mConnectedThread;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_find:
                findDevices();
                break;
        }
    }

    private void initInstance() {
        deviceData = DeviceData.getInstance();
        deviceData.deviceArrayList = new ArrayList<>();

        mButton = findViewById(R.id.button_find);
        mButton.setOnClickListener(this);
        //mLinearLayout = findViewById(R.id.linear_layout);
        mListView = findViewById(R.id.list_view);
        mListView.setOnItemClickListener(mDeviceClick);

        mTextView = findViewById(R.id.text_view);
        mTextView.setVisibility(View.GONE);
        //mListView.setVisibility(View.GONE);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //getPairDevices();

//        bluetoothAdapter.startDiscovery();
//
//        // Register for broadcasts when a device is discovered.
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(receiver, filter);
    }

    private void getPairDevices() {
        if (bluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDeivce = bluetoothAdapter.getBondedDevices();

            if (pairedDeivce.size() > 0) {
                mTextView.setVisibility(View.GONE);
                for (BluetoothDevice device : pairedDeivce) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();

                    deviceData.deviceArrayList.add(device);
                }

                DeviceListAdapter adapter = new DeviceListAdapter(this, R.layout.device_item, deviceData.deviceArrayList);
                mListView.setAdapter(adapter);
            }
        }
    }

    private void findDevices() {
        if (bluetoothAdapter.isEnabled()) {
            //if(bluetoothAdapter.isDiscovering()) {
            mTextView.setVisibility(View.GONE);
            //mListView.setVisibility(View.GONE);

            // bluetoothAdapter.cancelDiscovery();
            //bluetoothAdapter.startDiscovery();

            // Register for broadcasts when a device is discovered.
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);

            //mListView.refreshDrawableState();
            //}
        }
    }

    private OnItemClickListener mDeviceClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ConnectThread connectThread = new ConnectThread(deviceData.deviceArrayList.get(i));
            connectThread.start();
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

                if (device != null) {
                    mListView.setVisibility(View.VISIBLE);

                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address

                    deviceData.deviceArrayList.add(device);

                    //BluetoothDevice device1 = deviceData.deviceArrayList.get(0);
                    //Log.e("Device name", device1.getName());

                    DeviceListAdapter adapter = new DeviceListAdapter(DeviceList.this, R.layout.device_item, deviceData.deviceArrayList);
                    mListView.setAdapter(adapter);

                    Toast.makeText(context, (device.getName() + "\n" + device.getAddress()), Toast.LENGTH_SHORT).show();
                } else {
                    mTextView.setVisibility(View.VISIBLE);
                }


                //mListView.refreshDrawableState();
            }
        }
    };

    private void manageMyConnectedSocket(BluetoothSocket mSocket) {
        mConnectedThread = new ConnectedThread(mSocket);
        mConnectedThread.start();
    }

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        public ConnectThread(BluetoothDevice device) {
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
//                try {
//                    mSocket.close();
//                } catch (IOException closeException) {
//                    Log.e(TAG, "Could not close the client socket", closeException);
//                }
                return;
            }
            manageMyConnectedSocket(mSocket);
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;
        private byte[] mBuffer;

        public ConnectedThread(BluetoothSocket socket) {
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        public void run() {
            mBuffer = new byte[1024];
            int numBytes;

            while (true) {
                try {
                    numBytes = mInStream.read(mBuffer);

                    String text = new String(mBuffer, 0);

                    Toast.makeText(DeviceList.this, text, Toast.LENGTH_SHORT).show();

                    Message readMsg = handler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1, mBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(MessageConstants.MESSAGE_WRITE, -1, -1, mBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

}
