package com.pslyp.dev.quailsmartfarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pslyp.dev.quailsmartfarm.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NameDeviceListAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> mNameDeviceArrayList;
    private int mLayoutResId;

    public NameDeviceListAdapter(@NonNull Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mLayoutResId = resource;
        this.mNameDeviceArrayList= objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(mLayoutResId, null);

        String nameDevice = mNameDeviceArrayList.get(position);

        if(nameDevice != null) {
            TextView deviceName = view.findViewById(R.id.text_view_name);
            deviceName.setText(nameDevice);
        }

        return view;
    }
}
