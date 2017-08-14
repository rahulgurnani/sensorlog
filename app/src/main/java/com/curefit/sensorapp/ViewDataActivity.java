package com.curefit.sensorapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewDataActivity extends AppCompatActivity {
    private DataStoreHelper dsh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allactions);
        dsh = new DataStoreHelper(this);

        GlobalVariable globalVariable = GlobalVariable.getInstance();

        // creating buttons
//        final Button button1 = (Button) findViewById(R.id.button1);
//        button1.setOnClickListener(myButton_Listener1);
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(myButton_Listener2);
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(myButton_Listener3);
        final Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(myButton_Listener4);
        TextView welcomeUser = (TextView) findViewById(R.id.welcomeText);
        welcomeUser.setText("Welcome " + globalVariable.getUser().name);
//        final Button button5 = (Button) findViewById(R.id.button1);
        final Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(myButton_Listener5);
        final Button sleeptimeButton = (Button) findViewById(R.id.sleeptime);
        sleeptimeButton.setOnClickListener(myButton_sleeptime);
        // starting service
        Intent i = new Intent(this, SensorUpdateService.class);
        getApplicationContext().startService(i);
    }

    final View.OnClickListener myButton_Listener1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.println("Button 1 pressed");
            AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), SensorUpdateService.class);
            PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, scheduledIntent);
        }
    };
    final View.OnClickListener myButton_Listener2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.println("Button 2 pressed");
            Intent intent = new Intent(ViewDataActivity.this, StatsActivity.class);
            intent.putExtra("SENSOR_TYPE", "Light");
            startActivity(intent);
        }
    };
    final View.OnClickListener myButton_Listener3 = new View.OnClickListener() {
        public void onClick(View view) {
            System.out.println("Button 3 pressed");
            Intent intent = new Intent(ViewDataActivity.this, StatsActivity.class);
            intent.putExtra("SENSOR_TYPE", "Screen");
            startActivity(intent);
        }
    };
    final View.OnClickListener myButton_Listener4 = new View.OnClickListener() {
        public void onClick(View view) {
            System.out.println("Button 4 pressed");
            Intent intent = new Intent(ViewDataActivity.this, StatsActivity.class);
            intent.putExtra("SENSOR_TYPE", "Accelerometer");
            startActivity(intent);

        }
    };
    final View.OnClickListener myButton_Listener5 = new View.OnClickListener() {
        public void onClick(View view) {
            System.out.println("Button 5 pressed");
            Intent intent = new Intent(ViewDataActivity.this, StatsActivity.class);
            intent.putExtra("SENSOR_TYPE", "Charging");
            startActivity(intent);
        }
    };

    @Override
    public void onBackPressed() {

    }

    final View.OnClickListener myButton_sleeptime = new View.OnClickListener() {
        public void onClick(View view) {
            System.out.println("sleeptime button");
            Intent intent = new Intent(ViewDataActivity.this, SleepTime.class);
            startActivity(intent);
        }
    };

}
