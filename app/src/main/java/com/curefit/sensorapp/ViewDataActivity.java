package com.curefit.sensorapp;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.curefit.sensorsdk.services.SensorUpdateService;

/*
This is for the screen that appears after login, where there are buttons for setting sleep time etc.
 */
public class ViewDataActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allactions);

        // creating buttons
        final Button lightButton = (Button) findViewById(R.id.light_button);
        lightButton.setOnClickListener(lightButtonListener);
        final Button screenButton = (Button) findViewById(R.id.screen_button);
        screenButton.setOnClickListener(screenButtonListener);
        final Button accButton = (Button) findViewById(R.id.acc_button);
        accButton.setOnClickListener(accButtonListener);
        final Button sleeptimeButton = (Button) findViewById(R.id.sleeptime);
        sleeptimeButton.setOnClickListener(sleepTimeButtonListener);

        // Set textView
        TextView welcomeUser = (TextView) findViewById(R.id.welcomeText);
        welcomeUser.setText("Welcome " + this.getIntent().getStringExtra("name"));
    }

    private void setAlarmToStartService() {
        AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), SensorUpdateService.class);
        PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, scheduledIntent);
    }

    final View.OnClickListener lightButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ViewDataActivity.this, StatsActivity.class);
            intent.putExtra("SENSOR_TYPE", "Light");
            startActivity(intent);
        }
    };
    final View.OnClickListener screenButtonListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent(ViewDataActivity.this, StatsActivity.class);
            intent.putExtra("SENSOR_TYPE", "Screen");
            startActivity(intent);
        }
    };
    final View.OnClickListener accButtonListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent(ViewDataActivity.this, StatsActivity.class);
            intent.putExtra("SENSOR_TYPE", "Accelerometer");
            startActivity(intent);

        }
    };
    // charging data button
    final View.OnClickListener batteryButtonListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent(ViewDataActivity.this, StatsActivity.class);
            intent.putExtra("SENSOR_TYPE", "Charging");
            startActivity(intent);
        }
    };


    final View.OnClickListener sleepTimeButtonListener = new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent(ViewDataActivity.this, SleepTimeActivity.class);
            startActivity(intent);

        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SensorUpdateService.class.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
