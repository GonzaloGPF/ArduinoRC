package com.gpf.app.arduinorc.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpf.app.arduinorc.R;

import java.util.ArrayList;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BluetoothHolder> {
    private LayoutInflater inflater;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private ClickListener clickListener;

    public BluetoothDeviceAdapter(Context context, ClickListener cl){
        this.inflater = LayoutInflater.from(context);
        this.clickListener = cl;
    }

    @Override
    public BluetoothHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_bluetooth_device, parent, false);
        return new BluetoothHolder(view);
    }

    @Override
    public void onBindViewHolder(BluetoothHolder holder, int position) {
        BluetoothDevice current = devices.get(position);
        holder.setName(current.getName());
        holder.setAddress(current.getAddress());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void setDevices(ArrayList<BluetoothDevice> devices){
        this.devices = devices;
        notifyDataSetChanged();
    }

    public interface ClickListener{
        void deviceClick(View view, int position);
    }

    public void setClickListener(ClickListener cl){
        clickListener = cl;
    }

    public class BluetoothHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView icon;
        TextView name, address;
        public BluetoothHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.device_icon);
            name = (TextView) itemView.findViewById(R.id.device_name);
            address = (TextView) itemView.findViewById(R.id.device_address);
            itemView.setOnClickListener(this);
        }
        public void setIcon(int icon_id){
            this.icon.setImageResource(icon_id);
        }
        public void setName(String name){
            this.name.setText(name);
        }
        public void setAddress(String address){
            this.address.setText(address);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null){
                clickListener.deviceClick(v, getAdapterPosition());
            }
        }
    }
}
