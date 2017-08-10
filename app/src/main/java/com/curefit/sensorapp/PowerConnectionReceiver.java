package com.curefit.sensorapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by rahul on 10/08/17.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Power connection changed");
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;           // if the phone is getting charged or if the battery becomes full while charging
        if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            isCharging = false;
        }
        Intent i = new Intent(context, BatteryUpdateService.class);        // why do we do it in separate service ?
        i.putExtra("charging_state", isCharging);
        context.startService(i);
    }
}
