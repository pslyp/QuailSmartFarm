package com.pslyp.dev.quailsmartfarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pslyp.dev.quailsmartfarm.R;
import com.pslyp.dev.quailsmartfarm.models.Board;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DeviceListAdapter extends ArrayAdapter<Board> {

    private Context mContext;
    private List<Board> mDeviceArrayList;
    private int mLayoutResId;

    public DeviceListAdapter(@NonNull Context context, int resource, List<Board> objects) {
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

        Board board = mDeviceArrayList.get(position);

        if(board != null) {
            TextView deviceName = view.findViewById(R.id.text_view_name);

            if(board.getName() != null) {
                deviceName.setText(board.getName());
            }
        }

        return view;
    }
}
