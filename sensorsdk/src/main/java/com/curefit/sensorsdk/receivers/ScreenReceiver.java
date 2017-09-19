package com.curefit.sensorsdk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.curefit.sensorsdk.services.ScreenUpdateService;

/**
 * Created by rahul on 31/07/17.
 */

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean screenOff = true;
    private static long lastUpdate = 1;
    private static boolean lastState = false;       // off
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = false;
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = true;
        }
        else if (intent.getAction().equals("com.curefit.sensorApp.ServiceStopped")) {
            System.out.println("Service was stopped");
        }
        boolean flag = true;
        if(( (System.currentTimeMillis() - lastUpdate ) < 300) && (lastState  == screenOff) ){
            flag = false;
        }

        lastUpdate = System.currentTimeMillis();
        lastState = screenOff;
        if (flag) {
            Intent i = new Intent(context, ScreenUpdateService.class);        // why do we do it in separate service ?
            i.putExtra("screen_state", screenOff);
            context.startService(i);
        }
    }
}