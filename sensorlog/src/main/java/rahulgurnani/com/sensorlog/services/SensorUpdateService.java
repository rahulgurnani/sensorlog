package rahulgurnani.com.sensorlog.services;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;


import rahulgurnani.com.sensorlog.db.DataStoreHelper;
import rahulgurnani.com.sensorlog.receivers.PowerConnectionReceiver;
import rahulgurnani.com.sensorlog.receivers.ScreenReceiver;

import static java.lang.Math.abs;

public class SensorUpdateService extends Service implements SensorEventListener {
    private DataStoreHelper dsh;
    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mLight;
    private long startTime;
    private long startTimeLightSensor;
    private float lastValues[];
    private float lastLightValue;
    private BroadcastReceiver screenReceiver;

    public SensorUpdateService() {
        System.out.println("Sensor update service activated");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dsh = DataStoreHelper.getInstance(this);
        lastValues = new float[3];
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        System.out.println("Registered listener");

        // Dealing with broadcast receiver for screen sensor update
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        screenReceiver= new ScreenReceiver();
        registerReceiver(screenReceiver, filter);
        startTime = startTimeLightSensor = 0;

        // Broadcast receiver for changes in battery state
        BroadcastReceiver batteryReceiver = new PowerConnectionReceiver();
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);      // TODO : Check if there we have to unregister.
//        restartService();
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
        return Math.sqrt(Math.pow( (values1[0] - values2[0]), 2) + Math.pow( (values1[0] - values2[0]), 2) + Math.pow( (values1[0] - values2[0]), 2));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(screenReceiver);
        System.out.println("-----Service Destroyed----");
//        restartService();
    }

    public void restartService() {
        System.out.println("------ Restart Service ------ ");

        AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), SensorUpdateService.class);
        PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        scheduler.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 20000, scheduledIntent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (vectorialDistance(sensorEvent.values, lastValues) > 2) {
                dsh.addEntryAcc(sensorEvent.values);
                lastValues[0] = sensorEvent.values[0];
                lastValues[1]= sensorEvent.values[1];
                lastValues[2]= sensorEvent.values[2];
                startTime = System.currentTimeMillis();

                System.out.println("Accelerometer Sensor changed");
            }
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (abs(lastLightValue - sensorEvent.values[0]) > 3) {
                dsh.addEntryLight(sensorEvent.values[0]);
                startTimeLightSensor = System.currentTimeMillis();
                System.out.println("Light sensor changed");
                lastLightValue = sensorEvent.values[0];
            }
        }
    }
}
