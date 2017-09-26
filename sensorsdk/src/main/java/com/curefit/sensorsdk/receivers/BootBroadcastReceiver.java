package com.curefit.sensorsdk.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.curefit.sensorsdk.db.DataStoreHelper;
import com.curefit.sensorsdk.db.FirebaseStoreHelper;
import com.curefit.sensorsdk.services.SensorUpdateService;
import com.curefit.sensorsdk.sync.SensorDataContract;

/**
 * Created by rahul on 04/08/17.
 */

/*
    BroadcastReceiver called when boot is completed.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // in case of power on, start the service and log this event
            ContentValues values = new ContentValues();
            values.put("CURTIME", System.currentTimeMillis());
            values.put("MESSAGE",  "poweron");
            context.getContentResolver().insert(SensorDataContract.MessageData.CONTENT_URI, values);
            // start service
            Intent serviceIntent = new Intent(context, SensorUpdateService.class);
            context.startService(serviceIntent);
        }
        if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN") ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWEROFF")) {
            // log the event of power off
            ContentValues values = new ContentValues();
            values.put("CURTIME", System.currentTimeMillis());
            values.put("MESSAGE",  "poweroff");
            context.getContentResolver().insert(SensorDataContract.MessageData.CONTENT_URI, values);
        }
    }
}
