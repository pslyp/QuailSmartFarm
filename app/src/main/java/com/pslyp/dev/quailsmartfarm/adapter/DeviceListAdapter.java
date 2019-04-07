package com.pslyp.dev.quailsmartfarm.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pslyp.dev.quailsmartfarm.R;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private Context mContext;
    private ArrayList<BluetoothDevice> mDeviceArrayList;
    private int mLayoutResId;

    public DeviceListAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<BluetoothDevice> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mLayoutResId = resource;
        this.mDeviceArrayList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(mLayoutResId, null);

        BluetoothDevice device = mDeviceArrayList.get(position);

        if(device != null) {
            TextView deviceName = view.findViewById(R.id.text_view_name);
            TextView deviceAddress = view.findViewById(R.id.text_view_address);

            if(device.getName() != null) {
                deviceName.setText(device.getName());
            }
            if(device.getAddress() != null) {
                deviceAddress.setText(device.getAddress());
            }
        }

        return view;
    }
}
