package com.curefit.sensorsdk.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import com.curefit.sensorsdk.sync.SensorDataContract;

/**
 * Created by rahul on 31/07/17.
 */

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean screenState = true;
    private static long lastUpdate = 1;
    private static boolean lastState = false;       // off
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenState = false;
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenState = true;
        }

        boolean flag = true;
        if(( (System.currentTimeMillis() - lastUpdate ) < 300) && (lastState  == screenState) ){
            flag = false;
        }

        lastUpdate = System.currentTimeMillis();
        lastState = screenState;

        if (flag) {
            ContentValues values = new ContentValues();
            values.put("CURTIME", System.currentTimeMillis());
            if (screenState) {
                // screen off
                values.put("STATE", 1);

            }
            else {
                // screen on
                values.put("STATE", 0);
            }
            context.getContentResolver().insert(SensorDataContract.ScreenReadings.CONTENT_URI, values);
        }
    }
}