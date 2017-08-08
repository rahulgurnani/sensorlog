package com.curefit.sensorapp;

import java.util.List;

/**
 * Created by rahul on 28/07/17.
 */

public class SensorData {
    private String timestamp;
    private float accValues[];
    private float lightValue;
    private int screenValue;
    public SensorData() {}

    public SensorData(String timestamp, float accValues[]) {
        this.timestamp = timestamp;
        this.accValues = accValues;
    }

    public SensorData(String timestamp, float lightValue) {
        this.timestamp = timestamp;
        this.lightValue = lightValue;
    }

    public SensorData(String timestamp, int screenValue) {
        this.timestamp = timestamp;
        this.screenValue = screenValue;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public float getLightValue() {
        return lightValue;
    }

    public int getScreenValue() {
        return screenValue;
    }

    public float[] getAccValues() {
        return accValues;
    }
}
