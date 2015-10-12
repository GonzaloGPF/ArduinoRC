package com.gpf.app.arduinorc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Integer currentFragmentID = intent.getIntExtra(MainActivity.EXTRA_INFO, 0);
        switch (currentFragmentID){
            case 0: //bluetooth fragment
                setContentView(R.layout.activity_info_bluetooth);
                break;
            case 1: //controller fragment
                setContentView(R.layout.activity_info_controller);
                break;
            case 2: //console fragment
                setContentView(R.layout.activity_info_console);
                break;
        }
    }
}
