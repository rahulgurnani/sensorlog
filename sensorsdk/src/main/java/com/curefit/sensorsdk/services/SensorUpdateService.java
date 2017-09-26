package com.curefit.sensorsdk.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.curefit.sensorsdk.SensorSdk;
import com.curefit.sensorsdk.db.DataStoreHelper;
import com.curefit.sensorsdk.receivers.ScreenReceiver;
import com.curefit.sensorsdk.sync.SensorDataContract;
import com.google.firebase.FirebaseApp;

import static java.lang.Math.abs;

public class SensorUpdateService extends Service implements SensorEventListener {
    private DataStoreHelper dsh;
    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mLight;
    private float lastValues[];
    private float lastLightValue = 0;
    private BroadcastReceiver screenReceiver;

    public SensorUpdateService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dsh = DataStoreHelper.getInstance(this);
        lastValues = new float[3];
        FirebaseApp.initializeApp(this);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);

        // Dealing with broadcast receiver for screen sensor update
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        screenReceiver= new ScreenReceiver();
        registerReceiver(screenReceiver, filter);

        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private double vectorialDistance(float values1[], float values2[]) {
        float deltaX = (values1[0] - values2[0]);
        float deltaY = (values1[1] - values2[1]);
        float deltaZ = (values1[2] - values2[2]);
        return Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(screenReceiver);
        sensorManager.unregisterListener(this);
    }

    public void restartService() {
        AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), SensorUpdateService.class);
        PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        scheduler.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 20000, scheduledIntent);    // restart after 20 seconds
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (vectorialDistance(sensorEvent.values, lastValues) > 2) {
                // send only after there is significant change
//                dsh.addEntryAcc(sensorEvent.values);
                ContentValues values = new ContentValues();
                long currentEpochTime = System.currentTimeMillis();

                values.put("CURTIME", currentEpochTime);
                values.put("ACCX", sensorEvent.values[0]);
                values.put("ACCY", sensorEvent.values[1]);
                values.put("ACCZ", sensorEvent.values[2]);
                getContentResolver().insert(SensorDataContract.AccReadings.CONTENT_URI, values);

                lastValues[0] = sensorEvent.values[0];
                lastValues[1]= sensorEvent.values[1];
                lastValues[2]= sensorEvent.values[2];
            }
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (abs(lastLightValue - sensorEvent.values[0]) > 3) {
                // send only if there is significant change
//                dsh.addEntryLight(sensorEvent.values[0]);
                long currentEpochTime = System.currentTimeMillis();

                ContentValues values = new ContentValues();
                values.put("CURTIME", currentEpochTime);
                values.put("LIGHT", sensorEvent.values[0]);
                getContentResolver().insert(SensorDataContract.LightReadings.CONTENT_URI, values);
                lastLightValue = sensorEvent.values[0];
            }
        }
    }
}
