package com.curefit.sensorsdk.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.curefit.sensorsdk.db.DataStoreHelper;
import com.curefit.sensorsdk.sync.SensorDataContract;

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
        ContentValues values = new ContentValues();
        values.put("CURTIME", System.currentTimeMillis());
        if (!screenOn) {
            // screen off
            values.put("STATE", 0);

        }
        else {
            // screen on
            values.put("STATE", 1);
        }
        getContentResolver().insert(SensorDataContract.ScreenReadings.CONTENT_URI, values);
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
