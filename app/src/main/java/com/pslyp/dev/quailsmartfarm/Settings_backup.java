package com.pslyp.dev.quailsmartfarm;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.pslyp.dev.quailsmartfarm.adapter.DeviceListAdapter;

import java.util.ArrayList;

public class Settings_backup extends AppCompatActivity implements View.OnClickListener, MessageConstants {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;

    private BluetoothConnectionService mBluetoothConnection;

    private DeviceData deviceData;
    private DeviceListAdapter deviceListAdapter;

    private Button mButton, btConfirm;
    private ListView mListView;
    private TextInputLayout textInputSSID, textInputPASS;

    private String TAG = "Settings_backup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mBluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT.");

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }

        registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(scanReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(bluetoothReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_confirm :
                send();
                break;
            case R.id.button_find_device :
                discovery();
                break;
        }
    }

    private void initInstance() {
        deviceData = new DeviceData();
        deviceData.deviceArrayList = new ArrayList<>();

        mButton = findViewById(R.id.button_find_device);
        mButton.setOnClickListener(this);
        btConfirm = findViewById(R.id.button_confirm);
        btConfirm.setOnClickListener(this);
        mListView = findViewById(R.id.list_view_device);
        mListView.setOnItemClickListener(mDeviceClickListener);
        textInputSSID = findViewById(R.id.text_input_ssid);
        textInputPASS = findViewById(R.id.text_input_password);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mBluetoothAdapter.cancelDiscovery();

            Log.d(TAG, "onItemClick: You Clicked on a device.");

            String deviceName = deviceData.deviceArrayList.get(i).getName();
            String deviceAddress = deviceData.deviceArrayList.get(i).getAddress();

            Log.d(TAG, "onItemClick: deviceName = " + deviceName);
            Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

            //create the bond.
            //NOTE: Requires API 17+? I think this is JellyBean
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                Log.d(TAG, "Trying to pair with " + deviceName);

                deviceData.deviceArrayList.get(i).createBond();

                mBluetoothDevice = deviceData.deviceArrayList.get(i);
                mBluetoothConnection = new BluetoothConnectionService(Settings_backup.this);
            }
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String text = new String(readBuff, 0 , msg.arg1);
                    textInputPASS.getEditText().setText(text);
                    break;
            }

            return true;
        }
    });

    private void discovery() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(!mBluetoothAdapter.isDiscovering()) {
            //checkPermissions();

            mBluetoothAdapter.startDiscovery();

            IntentFilter discoverFilter = new IntentFilter();
            discoverFilter.addAction(BluetoothDevice.ACTION_FOUND);
            discoverFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            discoverFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(scanReceiver, discoverFilter);
        }
    }

    private void send() {
        String text = textInputSSID.getEditText().getText().toString();

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();


        //mBluetoothConnection.write(text.getBytes());
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastScannn = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    Log.d(TAG, "onReceive: ACTION FOUND.");

                    if (action.equals(BluetoothDevice.ACTION_FOUND)){
                        BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                        deviceData.deviceArrayList.add(device);

                        Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                        deviceListAdapter = new DeviceListAdapter(context, R.layout.device_item, deviceData.deviceArrayList);
                        mListView.setAdapter(deviceListAdapter);
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    mButton.setEnabled(false);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    mButton.setEnabled(true);
                    break;

            }
        }
    };

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

}
