package com.curefit.sensorapp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

/**
 * Created by rahul on 31/07/17.
 */

public class ScreenUpdateService extends Service {

    private DataStoreHelper dsh;
    @Override
    public void onCreate() {

        super.onCreate();
        dsh = new DataStoreHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service started");
        boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
            System.out.println("Screen Off");
            // write code to store the data to database
            dsh.addEntry(0);
        }
        else {
            System.out.println("Screen On");
            // write code to store the data to database
            dsh.addEntry(1);
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
