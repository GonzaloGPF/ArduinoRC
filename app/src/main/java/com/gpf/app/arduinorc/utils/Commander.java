package com.gpf.app.arduinorc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.gpf.app.arduinorc.R;
import com.zerokol.views.JoystickView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zalo on 15/07/2015.
 */
public class Commander {

    private static final String COMMANDS_VALUES = "command_values";
    private static final String DEFAULT_COMMANDS = "o/O/m/p/a/b/x/y/u/l/r/d/i/g/h/j/k/0/1/2/3/4/5/6/7/8/9";
    private static Commander sCommander = null;
    private CommanderListener commanderListener;
    private Activity activity;

    public Map<Integer, String> commandsMap = new HashMap<>();
//    public String commands[] = {"o", "O", // On/Off
//            "m", "p",                     // Minus/Plus
//            "a", "b", "x", "y",           // A, B, X, Y
//            "u", "l", "r", "d",           // D-Pad directions
//            "i",                          // Input Refresh
//            "g", "h", "j", "k",           // Joystick directions
//            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}; // Speeds values

    public static boolean onValue = false;
    private int i=0;

    private Commander(Activity activity){
        this.activity = activity;
        readCommandsMap();
    }

    public static Commander getInstance(Activity activity){
        if(sCommander==null){
            sCommander = new Commander(activity);
        }
        return sCommander;
    }

    public String getCommand(View v){
        String command = "";
        switch (v.getId()){
            case R.id.btn_on:
                if(!onValue){
                    command = commandsMap.get(0);
                }else{
                    command = commandsMap.get(1);
                }
                onValue = !onValue;
                break;
            case R.id.btn_minus:
                command = commandsMap.get(2);
                break;
            case R.id.btn_plus:
                command = commandsMap.get(3);
                break;
            case R.id.btn_a:
                command = commandsMap.get(4);
                break;
            case R.id.btn_b:
                command = commandsMap.get(5);
                break;
            case R.id.btn_x:
                command = commandsMap.get(6);
                break;
            case R.id.btn_y:
                command = commandsMap.get(7);
                break;
            case R.id.btn_up:
                command = commandsMap.get(8);
                break;
            case R.id.btn_left:
                command = commandsMap.get(9);
                break;
            case R.id.btn_right:
                command = commandsMap.get(10);
                break;
            case R.id.btn_down:
                command = commandsMap.get(11);
                break;
            case R.id.btn_refresh:
                command = commandsMap.get(12);
                break;
        }
        sendCommand(command);
        return command;
    }

    public String getJoystickCommand(int direction){
        String command = "";
        switch (direction) {
            case JoystickView.FRONT:
                command = commandsMap.get(8);
                break;
            case JoystickView.FRONT_RIGHT:
                command = commandsMap.get(13);
                break;
            case JoystickView.RIGHT:
                command = commandsMap.get(10);
                break;
            case JoystickView.RIGHT_BOTTOM:
                command = commandsMap.get(14);
                break;
            case JoystickView.BOTTOM:
                command = commandsMap.get(11);
                break;
            case JoystickView.BOTTOM_LEFT:
                command = commandsMap.get(15);
                break;
            case JoystickView.LEFT:
                command = commandsMap.get(9);
                break;
            case JoystickView.LEFT_FRONT:
                command = commandsMap.get(16);
                break;
            default:
        }
        sendCommand(command);
        return command;
    }

    public String getSpeedCommand(int speed){ //speed => from seekBar with values between 0 - 9
        int BASE = commandsMap.size()-10;
        String command = commandsMap.get(BASE + speed);
        sendCommand(command);
        return command;
    }

    public void setCommand(View v){
        String command = ((TextView) v).getText().toString();
        switch (v.getId()){
            case R.id.btn_on:
                commandsMap.put(0, command);
                break;
            case R.id.btn_off:
                commandsMap.put(1, command);
                break;
            case R.id.btn_minus:
                commandsMap.put(2, command);
                break;
            case R.id.btn_plus:
                commandsMap.put(3, command);
                break;
            case R.id.btn_a:
                commandsMap.put(4, command);
                break;
            case R.id.btn_b:
                commandsMap.put(5, command);
                break;
            case R.id.btn_x:
                commandsMap.put(6, command);
                break;
            case R.id.btn_y:
                commandsMap.put(7, command);
                break;
            case R.id.arrow_up:
                commandsMap.put(8, command);
                break;
            case R.id.arrow_left:
                commandsMap.put(9, command);
                break;
            case R.id.arrow_right:
                commandsMap.put(10, command);
                break;
            case R.id.arrow_down:
                commandsMap.put(11, command);
                break;
            case R.id.btn_refresh:
                commandsMap.put(12, command);
                break;
            case R.id.arrow_up_right:
                commandsMap.put(13, command);
                break;
            case R.id.arrow_right_down:
                commandsMap.put(14, command);
                break;
            case R.id.arrow_down_left:
                commandsMap.put(15, command);
                break;
            case R.id.arrow_up_left:
                commandsMap.put(16, command);
                break;
            case R.id.speed_0:
                commandsMap.put(17, command);
                break;
            case R.id.speed_1:
                commandsMap.put(18, command);
                break;
            case R.id.speed_2:
                commandsMap.put(19, command);
                break;
            case R.id.speed_3:
                commandsMap.put(20, command);
                break;
            case R.id.speed_4:
                commandsMap.put(21, command);
                break;
            case R.id.speed_5:
                commandsMap.put(22, command);
                break;
            case R.id.speed_6:
                commandsMap.put(23, command);
                break;
            case R.id.speed_7:
                commandsMap.put(24, command);
                break;
            case R.id.speed_8:
                commandsMap.put(25, command);
                break;
            case R.id.speed_9:
                commandsMap.put(26, command);
                break;
        }
    }

    public String getCommandValue(View v){
        String commandValue = "";
        switch (v.getId()){
            case R.id.btn_on:
                commandValue = commandsMap.get(0);
                break;
            case R.id.btn_off:
                commandValue = commandsMap.get(1);
                break;
            case R.id.btn_minus:
                commandValue = commandsMap.get(2);
                break;
            case R.id.btn_plus:
                commandValue = commandsMap.get(3);
                break;
            case R.id.btn_a:
                commandValue = commandsMap.get(4);
                break;
            case R.id.btn_b:
                commandValue = commandsMap.get(5);
                break;
            case R.id.btn_x:
                commandValue = commandsMap.get(6);
                break;
            case R.id.btn_y:
                commandValue = commandsMap.get(7);
                break;
            case R.id.arrow_up:
                commandValue = commandsMap.get(8);
                break;
            case R.id.arrow_left:
                commandValue = commandsMap.get(9);
                break;
            case R.id.arrow_right:
                commandValue = commandsMap.get(10);
                break;
            case R.id.arrow_down:
                commandValue = commandsMap.get(11);
                break;
            case R.id.btn_refresh:
                commandValue = commandsMap.get(12);
                break;
            case R.id.arrow_up_right:
                commandValue = commandsMap.get(13);
                break;
            case R.id.arrow_right_down:
                commandValue = commandsMap.get(14);
                break;
            case R.id.arrow_down_left:
                commandValue = commandsMap.get(15);
                break;
            case R.id.arrow_up_left:
                commandValue = commandsMap.get(16);
                break;
            case R.id.speed_0:
                commandValue = commandsMap.get(17);
                break;
            case R.id.speed_1:
                commandValue = commandsMap.get(18);
                break;
            case R.id.speed_2:
                commandValue = commandsMap.get(19);
                break;
            case R.id.speed_3:
                commandValue = commandsMap.get(20);
                break;
            case R.id.speed_4:
                commandValue = commandsMap.get(21);
                break;
            case R.id.speed_5:
                commandValue = commandsMap.get(22);
                break;
            case R.id.speed_6:
                commandValue = commandsMap.get(23);
                break;
            case R.id.speed_7:
                commandValue = commandsMap.get(24);
                break;
            case R.id.speed_8:
                commandValue = commandsMap.get(25);
                break;
            case R.id.speed_9:
                commandValue = commandsMap.get(26);
                break;
        }
        return commandValue;
    }

    private void sendCommand(String command){
        if(commanderListener !=null){
            commanderListener.onCommandSend(command);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            commanderListener.onCommandSent();
                        }
                    }, 100
            );
        }
    }

    public void saveCommandsMap(){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String values = "";
        for(i = 0; i<commandsMap.keySet().size(); i++){
            values += commandsMap.get(i) + "/";
        }
        editor.putString(COMMANDS_VALUES, values);
        editor.apply();
    }
    private void readCommandsMap(){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String commandsValues = sharedPref.getString(COMMANDS_VALUES, DEFAULT_COMMANDS);
        if(commandsValues!=null) {
            String[] commandsValuesArray = commandsValues.split("/");
            for (i = 0; i < commandsValuesArray.length; i++) {
                commandsMap.put(i, commandsValuesArray[i]);
            }
        }
    }


    public void setCommanderListener(CommanderListener cl){
        this.commanderListener = cl;
    }

    public interface CommanderListener{
        void onCommandSend(String commandSend);
        void onCommandSent();
    }
}
