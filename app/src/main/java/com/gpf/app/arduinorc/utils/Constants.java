package com.gpf.app.arduinorc.utils;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.items.NavigationRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zalo on 19/06/2015.
 */
public class Constants {

    public static List<NavigationRow> getNavigationRows(){
        List<NavigationRow> data = new ArrayList<>();
        int icons[] = {R.drawable.ic_nav_bluetooth, R.drawable.ic_nav_remote};
        String titles[] = {"Bluetooth", "Remote Control"};
        for(int i=0; i<icons.length && i<titles.length; i++){
            NavigationRow row = new NavigationRow(icons[i], titles[i]);
            data.add(row);
        }
        return data;
    }
}
