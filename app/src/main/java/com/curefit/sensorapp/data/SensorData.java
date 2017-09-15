package com.curefit.sensorapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 28/07/17.
 */

public class SensorData {
    private String timestamp;
    private List<Float> accValues;
    private float lightValue;
    private int screenValue;
    private String sensorType;
    private int batteryState;

    public SensorData() {
        accValues = new ArrayList<Float>(3);
    }
    private float[] listToArray(List<Float> l) {
        float[] accValues = new float[l.size()];
        for (int i = 0; i < l.size(); i++) {
            accValues[i] = l.get(i).floatValue();
        }
        return accValues;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    private List<Float> arrayToList(float[] array) {

        List<Float> list = new ArrayList<Float>();
        for (int i=0; i < array.length; i++) {
            Float f = (Float) array[i];
            list.add(f);
        }
        return list;
    }

    public SensorData(String timestamp, float accValues[]) {
        this.timestamp = timestamp;
        this.accValues = arrayToList(accValues);
    }

    public SensorData(String timestamp, float lightValue) {
        this.timestamp = timestamp;
        this.lightValue = lightValue;
    }

    public SensorData(String timestamp, int screenValue) {
        this.timestamp = timestamp;
        this.screenValue = screenValue;
    }

    public SensorData(String timestamp, String sensorType, int batteryState) {
        this.batteryState = batteryState;
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

    public List<Float> getAccValues() {
        return accValues;
    }

    public String getSensorType() {
        return sensorType;
    }

    public int getBatteryState() {
        return batteryState;
    }
}
