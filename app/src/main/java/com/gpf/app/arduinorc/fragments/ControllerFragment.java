package com.gpf.app.arduinorc.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.services.BluetoothService;
import com.zerokol.views.JoystickView;

public class ControllerFragment extends Fragment {

    private static final String SOCKET = "socket";
    private static final String ARG_PARAM2 = "param2";

    private BluetoothSocket clientSocket;
    private String mParam1;
    private String mParam2;

    private OnBControllerInteractionListener mListener;

    public static ControllerFragment newInstance() {
        return new ControllerFragment();
    }

    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller, container, false);
        JoystickView joystick = (JoystickView) view.findViewById(R.id.joystickView);

        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int angle, int power, int direction) {
                sendJoystickData(direction);
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnBControllerInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnBControllerInteractionListener {
        void onControllerInteraction(int direction);
    }

    private void sendJoystickData(int direction) {
        Log.d("DEBUGG", "Direction: " + direction);
        switch (direction) {
            case JoystickView.FRONT:
                BluetoothService.write("1".getBytes());
                break;
            case JoystickView.FRONT_RIGHT:
                break;
            case JoystickView.RIGHT:
                break;
            case JoystickView.RIGHT_BOTTOM:
                break;
            case JoystickView.BOTTOM:
                break;
            case JoystickView.BOTTOM_LEFT:
                break;
            case JoystickView.LEFT:
                break;
            case JoystickView.LEFT_FRONT:
                break;
            default:
        }

    }

//    private void sendData(String data){
//        Log.d("DEBUGG", "Sending: " + data);
//
//        clientSocket = ((MainActivity)getActivity()).getSocket();
//        OutputStream mmOutStream;
//        try {
//            if (clientSocket != null && clientSocket.isConnected()){
//                mmOutStream = clientSocket.getOutputStream();
//                mmOutStream.write(data.getBytes());
//                Log.d("DEBUGG", "Data sended");
//            }else{
//                Toast.makeText(getActivity(), "Sin Conexion", Toast.LENGTH_SHORT).show();
//            }
//        } catch (IOException e) {
//            Log.d("DEBUGG",e.getMessage());
//        }
//    }
}
