package com.gpf.app.arduinorc.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gpf.app.arduinorc.MainActivity;
import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.adapters.BluetoothDeviceAdapter;

import java.util.ArrayList;

public class BluetoothFragment extends Fragment implements View.OnClickListener, BluetoothDeviceAdapter.ClickListener {

    public static final String TAG = "BT_Fragment";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE = 2;
    private static final String DEVICES = "bluetooth_devices";
    private Button btn_bluetooth, btn_search, btn_bonded;
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
            Log.d(TAG, "Bluetooth is not available");
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            //getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        btn_bluetooth = (Button) view.findViewById(R.id.btn_bluetooth);
        btn_search = (Button) view.findViewById(R.id.btn_search);
        btn_bonded = (Button) view.findViewById(R.id.btn_bonded);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.devices_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BluetoothDeviceAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);

        if(devices!=null){
            setDevices();
        }
        setButtonsListeners();
        refreshButtons();
        return view;
    }
    private void setButtonsListeners(){
        if(bAdapter!=null){
            btn_bluetooth.setOnClickListener(this);
            btn_search.setOnClickListener(this);
            btn_bonded.setOnClickListener(this);
        }
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
            throw new ClassCastException(activity.toString() + " must implement OnBluetoothInteractionListener");
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
                ((MainActivity) getActivity()).showProgressBar(true);
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
        if (bAdapter != null && mListener != null) {
            bAdapter.cancelDiscovery();
            BluetoothDevice device = devices.get(position);
            connectDialog("Connection", "Do you want connect with " + device.getName() + " ?", device).show();
            //mListener.onDeviceClick(device);
        }
    }

    public interface OnBluetoothInteractionListener {
        void connectToDevice(BluetoothDevice device);
    }

    public void refreshButtons(){
        if(bAdapter!=null) {
            Boolean state = bAdapter.isEnabled();
            if (state) {
                btn_bluetooth.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bluetooth_disable, 0, 0);
                btn_bluetooth.setText("Disable");
            } else {
                btn_bluetooth.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bluetooth, 0, 0);
                btn_bluetooth.setText("Enable");
            }
            btn_search.setEnabled(state);
            btn_bonded.setEnabled(state);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(bAdapter!=null) {
            bAdapter.cancelDiscovery();
        }
    }

    public void addDevice(BluetoothDevice device){
        devices.add(device);
        adapter.setDevices(devices);
    }

    public void setDevices(){
        adapter.setDevices(devices);
    }

    private AlertDialog connectDialog(String title, String msg, final BluetoothDevice device) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);

        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.connectToDevice(device);
            }
        };
        DialogInterface.OnClickListener listenerCancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        };
        alertDialogBuilder.setPositiveButton(R.string.connect, listenerOk);
        alertDialogBuilder.setNegativeButton(R.string.cancel, listenerCancel);

        return alertDialogBuilder.create();
    }
}
