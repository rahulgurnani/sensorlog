package com.curefit.sensorsdk.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.curefit.sensorsdk.db.DataStoreHelper;

/**
 * Created by rahul on 31/07/17.
 */

public class ScreenUpdateService extends Service {

    private DataStoreHelper dsh;
    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dsh = DataStoreHelper.getInstance(this);
        boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
            // write code to store the data to database
            dsh.addEntryScreen(0);
        }
        else {
            // write code to store the data to database
            dsh.addEntryScreen(1);
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
