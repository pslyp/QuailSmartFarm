package com.pslyp.dev.quailsmartfarm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pslyp.dev.quailsmartfarm.R;
import com.pslyp.dev.quailsmartfarm.models.Device;

import java.util.List;

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Device> mDeviceList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Device item);
    }

    public DeviceRecyclerAdapter(Context mContext, List<Device> mDeviceList) {
        this.mContext = mContext;
        this.mDeviceList = mDeviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_row_device, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device device = mDeviceList.get(position);

        holder.nameTextView.setText(device.getName());
        holder.bind(device, mListener);
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_text_view);
        }

        public void bind(final Device item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
