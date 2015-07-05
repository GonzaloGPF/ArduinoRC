package com.gpf.app.arduinorc.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.adapters.BluetoothDeviceAdapter;

import java.util.ArrayList;

public class BluetoothFragment extends Fragment implements View.OnClickListener, BluetoothDeviceAdapter.ClickListener {

    public static final String TAG = "BT_Fragment";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE = 2;
    private static final String DEVICES = "bluetooth_devices";
    private ImageButton btn_bluetooth, btn_search, btn_bonded;
    private TextView txt_bluetooth, txt_search, txt_bonded;
    private BluetoothDeviceAdapter adapter;
    private BluetoothAdapter bAdapter;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private OnBluetoothInteractionListener mListener;

    public static BluetoothFragment newInstance() {
        //Bundle args = new Bundle();
        //args.putBoolean(ADAPTER_STATE, param1);
        //fragment.setArguments(args);
        return new BluetoothFragment();
    }

    public BluetoothFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            devices = savedInstanceState.getParcelableArrayList(DEVICES);
        }
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        btn_bluetooth = (ImageButton) view.findViewById(R.id.btn_bluetooth);
        btn_search = (ImageButton) view.findViewById(R.id.btn_search);
        btn_bonded = (ImageButton) view.findViewById(R.id.btn_bonded);
        txt_bluetooth = (TextView) view.findViewById(R.id.txt_bluetooth);
        txt_search = (TextView) view.findViewById(R.id.txt_search);
        txt_bonded = (TextView) view.findViewById(R.id.txt_bonded);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.devices_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BluetoothDeviceAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);

        if(devices!=null){
            setDevices();
        }

        btn_bluetooth.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_bonded.setOnClickListener(this);
        refreshButtons();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DEVICES, devices);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnBluetoothInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bAdapter != null) {
            bAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bluetooth:
                if(bAdapter.isEnabled()) {
                    bAdapter.disable();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                break;
            case R.id.btn_search:
                devices.clear();
                //setProgressBarIndeterminateVisibility(true);
                //setTitle(R.string.scanning);
                if (bAdapter.isDiscovering()) {
                    bAdapter.cancelDiscovery();
                }
                if (bAdapter.startDiscovery()) {
                    Toast.makeText(getActivity(), "Discovering", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "Discovering Error", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_bonded:
                devices = new ArrayList<>(bAdapter.getBondedDevices());
                setDevices();
                break;
        }
        refreshButtons();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Bluetooth: the user accepts");
                refreshButtons();
            } else {
                Log.d(TAG, "Bluetooth: the user does not accept");
            }
        }
        if (requestCode == REQUEST_DISCOVERABLE){

            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Bluetooth: discovery finished");
            } else {
                Log.d(TAG, "Bluetooth: discovery error");
            }
        }
    }

    @Override
    public void deviceClick(View view, int position) {
        if (mListener != null) {
            bAdapter.cancelDiscovery();
            BluetoothDevice device = devices.get(position);
            mListener.onDeviceClick(device);
        }
    }

    public interface OnBluetoothInteractionListener {
        void onDeviceClick(BluetoothDevice device);
    }

    public void refreshButtons(){
        Boolean state = bAdapter.isEnabled();
        if(state){
            btn_bluetooth.setImageResource(R.drawable.ic_btn_bluetooth_disable);
            txt_bluetooth.setText("Disable");
        }else{
            btn_bluetooth.setImageResource(R.drawable.ic_btn_bluetooth);
            txt_bluetooth.setText("Enable");
        }
        btn_search.setEnabled(state);
        txt_search.setEnabled(state);
        btn_bonded.setEnabled(state);
        txt_bonded.setEnabled(state);

    }

    @Override
    public void onPause() {
        super.onPause();
        bAdapter.cancelDiscovery();
    }

    public void addDevice(BluetoothDevice device){
        devices.add(device);
        adapter.setDevices(devices);
    }

    public void setDevices(){
        adapter.setDevices(devices);
    }
}
