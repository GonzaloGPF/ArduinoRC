package com.gpf.app.arduinorc.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.services.BluetoothService;
import com.gpf.app.arduinorc.utils.Commander;
import com.zerokol.views.JoystickView;

import java.util.ArrayList;

public class GamePadFragment extends Fragment implements View.OnClickListener, Commander.CommanderListener{

    private static final String SOCKET = "socket";
    private static final String COMMANDS = "commands";
    private static final String TAG = "GamePad";

    private BluetoothSocket clientSocket;
    private String mParam1;
    private String mParam2;

    private ImageButton btn_joystick;
    private Button btn_on, btn_plus, btn_minus, btn_a, btn_b, btn_x, btn_y, btn_up, btn_left, btn_right, btn_down;
    private TableLayout dPad;
    private JoystickView joystick;
    private ArrayList<Button> buttons = new ArrayList<>();
    private Boolean joystickMode = false;
    private SeekBar speedBar;
    private TextView speedValue, commandValue;
    private ImageView led;

    private OnBControllerInteractionListener mListener;

    public static GamePadFragment newInstance() {
        return new GamePadFragment();
    }

    public GamePadFragment() {
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
        View view = inflater.inflate(R.layout.fragment_gamepad, container, false);

        led = (ImageView) view.findViewById(R.id.led);
        joystick = (JoystickView) view.findViewById(R.id.joystickView);
        dPad = (TableLayout) view.findViewById(R.id.dPadView);
        speedBar = (SeekBar) view.findViewById(R.id.speedBar);
        speedValue = (TextView) view.findViewById(R.id.speed_value);
        speedValue.setText(String.valueOf(speedBar.getProgress()));
        commandValue = (TextView) view.findViewById(R.id.command_value);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedValue.setText(String.valueOf(speedBar.getProgress()));
                String command = Commander.getInstance(getActivity()).getSpeedCommand(progress);
                if (command != null) {
                    BluetoothService.write(command.getBytes());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int angle, int power, int direction) {
                String command = Commander.getInstance(getActivity()).getJoystickCommand(direction);
                BluetoothService.write(command.getBytes());
                speedBar.setProgress(power / speedBar.getMax());
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);

        referenceButtons(view);
        setListeners();
        return view;
    }

    private void referenceButtons(View view){
        btn_joystick = (ImageButton) view.findViewById(R.id.btn_joystick);
        btn_on = (Button) view.findViewById(R.id.btn_on);
        btn_plus = (Button) view.findViewById(R.id.btn_plus);
        btn_minus = (Button) view.findViewById(R.id.btn_minus);
        btn_a = (Button) view.findViewById(R.id.btn_a);
        btn_b = (Button) view.findViewById(R.id.btn_b);
        btn_x = (Button) view.findViewById(R.id.btn_x);
        btn_y = (Button) view.findViewById(R.id.btn_y);
        btn_up = (Button) view.findViewById(R.id.btn_up);
        btn_left = (Button) view.findViewById(R.id.btn_left);
        btn_right = (Button) view.findViewById(R.id.btn_right);
        btn_down = (Button) view.findViewById(R.id.btn_down);
        buttons.add(btn_plus);
        buttons.add(btn_minus);
        buttons.add(btn_a);
        buttons.add(btn_b);
        buttons.add(btn_x);
        buttons.add(btn_y);
        buttons.add(btn_up);
        buttons.add(btn_left);
        buttons.add(btn_right);
        buttons.add(btn_down);
    }

    private void setListeners(){
        for(Button button: buttons){
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    String command = Commander.getInstance(getActivity()).getCommand(v);
                    BluetoothService.write(command.getBytes());
                    return false;
                }
            });
        }
        btn_on.setOnClickListener(this);
        btn_joystick.setOnClickListener(this);
        Commander.getInstance(getActivity()).setCommanderListener(this);
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_on){
            String command = Commander.getInstance(getActivity()).getCommand(v);
            BluetoothService.write(command.getBytes());
            if(Commander.onValue){
                btn_on.setText("off");
            }else{
                btn_on.setText("on");
            }
        }else{
            if(joystickMode){
                joystick.setVisibility(View.GONE);
                dPad.setVisibility(View.VISIBLE);
                btn_joystick.setImageResource(R.drawable.ic_joystick);
            }else{
                dPad.setVisibility(View.GONE);
                joystick.setVisibility(View.VISIBLE);
                btn_joystick.setImageResource(R.drawable.ic_dpad);
            }
            joystickMode = !joystickMode;
        }
    }

    @Override
    public void onCommandSend(String command) {
        commandValue.setText(command);
        led.setImageResource(R.drawable.ic_led_green);
    }
    @Override
    public void onCommandSent(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                led.setImageResource(R.drawable.ic_led_red);
            }
        });
    }

    public interface OnBControllerInteractionListener {
        void onControllerInteraction(int direction);
    }
}
