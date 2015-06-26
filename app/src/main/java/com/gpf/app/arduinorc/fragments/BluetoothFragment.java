package com.gpf.app.arduinorc.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private ImageButton btn_bluetooth, btn_search, btn_bonded;
    private TextView txt_bluetooth, txt_search, txt_bonded;
    private BluetoothDeviceAdapter adapter;

    private OnBluetoothInteractionListener mListener;

    public static BluetoothFragment newInstance() {
        BluetoothFragment fragment = new BluetoothFragment();
        Bundle args = new Bundle();
        //args.putBoolean(ADAPTER_STATE, param1);
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
            //adapterState = getArguments().getBoolean(ADAPTER_STATE);
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
        adapter = new BluetoothDeviceAdapter(getActivity(),(MainActivity) getActivity());
        recyclerView.setAdapter(adapter);

        btn_bluetooth.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_bonded.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshButtons(((MainActivity) getActivity()).getBluetoothState());
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d("Debug", "Atached");
        super.onAttach(activity);
        try {
            mListener = (OnBluetoothInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d("Debug", "Detached");
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
        if(isVisible()){
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
    }

    public void setDevices(ArrayList<BluetoothDevice> devices){
        if(adapter!=null){
            adapter.setDevices(devices);
        }
    }
}
