package com.curefit.sensorsdk.data;


/**
 * Created by rahul on 25/08/17.
 */

public class LightData {

    private float lightValue;
    long timestamp;

    public LightData() {

    }

    public LightData(float lightValue, long timestamp) {
        this.lightValue = lightValue;
        this.timestamp = timestamp;
    }

    // getters and setters for the class
    public float getLightValue() {
        return lightValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setLightValue(float lightValue) {
        this.lightValue = lightValue;
    }
}

