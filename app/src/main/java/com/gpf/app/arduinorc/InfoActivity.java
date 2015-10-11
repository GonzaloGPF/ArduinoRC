package com.gpf.app.arduinorc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

public class InfoActivity extends AppCompatActivity {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        image = (ImageView) findViewById(R.id.info_image);

        Intent intent = getIntent();
        Integer currentFragmentID = intent.getIntExtra(MainActivity.EXTRA_INFO, 0);
        setUp(currentFragmentID);
    }

    private void setUp(Integer id){
        switch (id){
            case 0: //bluetooth fragment
                image.setImageResource(R.drawable.bluetooth_info);
                break;
            case 1: //controller fragment
                image.setImageResource(R.drawable.controller_info);
                break;
            case 2: //console fragment
                image.setImageResource(R.drawable.console_info);
                break;
        }
    }
}
