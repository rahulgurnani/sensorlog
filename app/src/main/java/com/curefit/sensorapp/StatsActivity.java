package com.curefit.sensorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;

public class StatsActivity extends AppCompatActivity {

    private String sensorName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        sensorName = getIntent().getStringExtra("SENSOR_TYPE");
        TextView sensorNameTextView = (TextView) findViewById(R.id.SensorName);
        sensorNameTextView.setText(sensorName + " Data");
        TextView valueUpdates = (TextView) findViewById(R.id.value1);
        TextView valueTimestamp= (TextView) findViewById(R.id.value2);
        DataStoreHelper dsh = new DataStoreHelper(this);
        HashMap<String, String> stats = dsh.getStats(sensorName);       // pending < continue >
        valueUpdates.setText(stats.get("NumUpdates"));
        valueTimestamp.setText(stats.get("LastTimeStamp"));

    }

}
