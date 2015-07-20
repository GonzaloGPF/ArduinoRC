package com.gpf.app.arduinorc.utils;

import android.view.View;

import com.gpf.app.arduinorc.R;
import com.zerokol.views.JoystickView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zalo on 15/07/2015.
 */
public class Commander {

    private static Commander sCommander = null;
    private CommanderListener commanderListener;

    public Map<Integer, String> commandsMap = new HashMap<>();
    public String commands[] = {"o", "O", // On/Off
            "m", "p",                     // Minus/Plus
            "a", "b", "x", "y",           // A, B, X, Y
            "u", "l", "r", "d",           // D-Pad directions
            "i",                          // Input Refresh
            "g", "h", "j", "k",           // Joystick directions
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}; // Speeds values

    private boolean on = false;

    private Commander(){
        for(int i=0; i<commands.length; i++){
            commandsMap.put(i, commands[i]);
        }
    }
//    private Commander(HashMap<Integer, String> commandsMap){
//        this.commandsMap = commandsMap;
//    }

    public static Commander getInstance(){
        if(sCommander==null){
            sCommander = new Commander();
        }
        return sCommander;
    }

//    public static Commander getInstance(HashMap<Integer, String> commands){
//        if(sCommander==null){
//            sCommander = new Commander(commands);
//        }
//        return sCommander;
//    }

    public String getCommand(View v){
        String command = "";
        switch (v.getId()){
            case R.id.btn_on:
                if(!on){
                    command = commandsMap.get(0);
                }else{
                    command = commandsMap.get(1);
                }
                on = !on;
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
        int BASE = commands.length-10;
        String command = commandsMap.get(BASE + speed);
        sendCommand(command);
        return command;
    }

    public void setCommand(View v, String command){
        switch (v.getId()){ //Modificar los case!!
            case R.id.btn_on:
                commandsMap.put(0, command);
                break;
            case R.id.btn_minus:
                commandsMap.put(1, command);
                break;
            case R.id.btn_plus:
                commandsMap.put(2, command);
                break;
            case R.id.btn_a:
                commandsMap.put(2, command);
                break;
            case R.id.btn_b:
                commandsMap.put(3, command);
                break;
            case R.id.btn_x:
                commandsMap.put(4, command);
                break;
            case R.id.btn_y:
                commandsMap.put(5, command);
                break;
            case R.id.btn_up:
                commandsMap.put(6, command);
                break;
            case R.id.btn_left:
                commandsMap.put(7, command);
                break;
            case R.id.btn_right:
                commandsMap.put(8, command);
                break;
            case R.id.btn_down:
                commandsMap.put(9, command);
                break;
        }
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

    public void setCommanderListener(CommanderListener cl){
        this.commanderListener = cl;
    }

    public interface CommanderListener{
        void onCommandSend(String commandSend);
        void onCommandSent();
    }
}
