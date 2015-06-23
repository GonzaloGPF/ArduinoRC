package com.gpf.app.arduinorc.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gpf.app.arduinorc.MainActivity;
import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.adapters.BluetoothDeviceAdapter;

import java.util.ArrayList;

public class BluetoothFragment extends Fragment implements View.OnClickListener {

    private static final String ADAPTER_STATE = "bluetooth_adapter_state";
    private static final String DEVICES_STATE = "devices_adapter_state";

    private Boolean adapterState;

    private ImageButton btn_bluetooth, btn_search, btn_bonded;
    private TextView txt_bluetooth;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private BluetoothDeviceAdapter adapter;

    private OnBluetoothInteractionListener mListener;

    public static BluetoothFragment newInstance(Boolean param1) {
        BluetoothFragment fragment = new BluetoothFragment();
        Bundle args = new Bundle();
        args.putBoolean(ADAPTER_STATE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public BluetoothFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            adapterState = getArguments().getBoolean(ADAPTER_STATE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState); //If the user rotates screen, dont lose your devices!
        outState.putParcelableArrayList(DEVICES_STATE, devices);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        btn_bluetooth = (ImageButton) view.findViewById(R.id.btn_bluetooth);
        btn_search = (ImageButton) view.findViewById(R.id.btn_search);
        btn_bonded = (ImageButton) view.findViewById(R.id.btn_bonded);
        txt_bluetooth = (TextView) view.findViewById(R.id.txt_bluetooth);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.devices_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BluetoothDeviceAdapter(getActivity(),(MainActivity) getActivity());
        recyclerView.setAdapter(adapter);

        btn_bluetooth.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_bonded.setOnClickListener(this);

        if(savedInstanceState!=null){
            devices =  savedInstanceState.getParcelableArrayList(DEVICES_STATE);
            adapter.setDevices(devices);
        }

        refreshButtons(adapterState);
        return view;
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
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onBluetoothInteraction(v.getId());
        }
    }

    public interface OnBluetoothInteractionListener {
        void onBluetoothInteraction(int btn_id);
    }

    public void refreshButtons(Boolean state){
        if(state){
            btn_bluetooth.setImageResource(R.drawable.ic_btn_bluetooth_disable);
            txt_bluetooth.setText("Disable");
        }else{
            btn_bluetooth.setImageResource(R.drawable.ic_btn_bluetooth);
            txt_bluetooth.setText("Enable");
        }
        btn_search.setEnabled(state);
        btn_bonded.setEnabled(state);
    }

    public void setArrayDevices(ArrayList<BluetoothDevice> devices){
        this.devices = devices;
        adapter.setDevices(this.devices);
    }
}
