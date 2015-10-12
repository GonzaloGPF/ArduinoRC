package com.gpf.app.arduinorc.fragments;

import android.graphics.Color;
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
import java.util.Arrays;

public class GamePadFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "GamePadFragment";
    private ImageButton btn_joystick, speedImage;
    private Button btn_on;
    private TableLayout dPad;
    private JoystickView joystick;
    private ArrayList<Button> buttons = new ArrayList<>();
    private Boolean joystickMode = false;
    private Boolean speedBlock = true;
    private SeekBar speedBar;
    private TextView speedValue, commandValue;
    private ImageView led;

    public static GamePadFragment newInstance() {
        return new GamePadFragment();
    }

    public GamePadFragment() {
        // Required empty public constructor
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
                updateUI(command);
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
                if (!speedBlock) {
                    speedBar.setProgress(power / speedBar.getMax());
                }
                if(power == 0){
                    command = Commander.getInstance(getActivity()).getStopCommand();
                    BluetoothService.write(command.getBytes());
                }
                updateUI(command);
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);

        referenceButtons(view);
        setListeners();
        return view;
    }

    private void referenceButtons(View view){
        btn_joystick = (ImageButton) view.findViewById(R.id.btn_joystick);
        btn_on = (Button) view.findViewById(R.id.btn_on);
        speedImage = (ImageButton) view.findViewById(R.id.speed_image);
        Button btn_plus = (Button) view.findViewById(R.id.btn_plus);
        Button btn_minus = (Button) view.findViewById(R.id.btn_minus);
        Button btn_a = (Button) view.findViewById(R.id.btn_a);
        Button btn_b = (Button) view.findViewById(R.id.btn_b);
        Button btn_x = (Button) view.findViewById(R.id.btn_x);
        Button btn_y = (Button) view.findViewById(R.id.btn_y);
        Button btn_up = (Button) view.findViewById(R.id.btn_up);
        Button btn_left = (Button) view.findViewById(R.id.btn_left);
        Button btn_right = (Button) view.findViewById(R.id.btn_right);
        Button btn_down = (Button) view.findViewById(R.id.btn_down);
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
            button.setOnTouchListener(getTouchListener());
        }
        btn_on.setOnClickListener(this);
        btn_joystick.setOnClickListener(this);
        speedImage.setOnClickListener(this);
        //Commander.getInstance(getActivity()).setCommanderListener(this);
    }

    private View.OnTouchListener getTouchListener(){
        return new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    String command = Commander.getInstance(getActivity()).getCommand(v);
                    updateUI(command);
                    BluetoothService.write(command.getBytes());
                }
                if(event.getAction() == MotionEvent.ACTION_UP && isDirectionButton(v)){
                    String command = Commander.getInstance(getActivity()).getStopCommand();
                    updateUI(command);
                    BluetoothService.write(command.getBytes());
                }
                return false;
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_on:
                String command = Commander.getInstance(getActivity()).getCommand(v);
                updateUI(command);
                BluetoothService.write(command.getBytes());
                if(Commander.onValue){
                    btn_on.setText("off");
                }else{
                    btn_on.setText("on");
                }
                break;
            case R.id.btn_joystick:
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
                break;
            case R.id.speed_image:
                if(speedBlock){
                    speedImage.setBackgroundColor(getResources().getColor(R.color.primaryColorLight));
                }else{
                    speedImage.setBackgroundColor(Color.WHITE);
                }
                speedBlock = !speedBlock;
                break;
        }
    }

    private Boolean isDirectionButton(View v){
        Integer directionButtonId[] = {R.id.btn_up, R.id.btn_left, R.id.btn_right, R.id.btn_down};
        return Arrays.asList(directionButtonId).contains(v.getId());
    }

    private void updateUI(String command){
        commandValue.setText(command);
        blinkLed();
    }

    private void blinkLed(){
        led.setImageResource(R.drawable.ic_led_green);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                led.setImageResource(R.drawable.ic_led_red);
                            }
                        });
                    }
                }, 100
        );
    }
}
