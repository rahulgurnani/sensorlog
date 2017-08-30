package com.curefit.sensorapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.curefit.sensorapp.services.SensorUpdateService;

/**
 * Created by rahul on 04/08/17.
 */

/*
    BroadcastReceiver called when boot is completed.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            System.out.println("Boot Completed");
            Intent serviceIntent = new Intent(context, SensorUpdateService.class);
            context.startService(serviceIntent);
        }
    }
}
