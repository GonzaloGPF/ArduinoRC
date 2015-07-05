package com.gpf.app.arduinorc.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Zalo on 05/07/2015.
 */
public class Utils {

    private boolean isMyServiceRunning(Activity activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
