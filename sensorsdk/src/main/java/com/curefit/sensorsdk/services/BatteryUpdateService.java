package com.curefit.sensorsdk.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.curefit.sensorsdk.db.DataStoreHelper;
import com.google.firebase.FirebaseApp;

/**
 * Created by rahul on 31/07/17.
 */

/*
    Service to update battery state change
 */
public class BatteryUpdateService extends Service {

    private DataStoreHelper dsh;
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dsh = DataStoreHelper.getInstance(this);
        boolean chargingState = intent.getBooleanExtra("charging_state", false);
        FirebaseApp.initializeApp(this);
        if (!chargingState) {
            // write code to store the data to database
            dsh.addEntryCharging(0);
        }
        else {
            // write code to store the data to database
            dsh.addEntryCharging(1);
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
