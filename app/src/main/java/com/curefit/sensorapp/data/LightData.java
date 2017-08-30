package com.curefit.sensorapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rahul on 25/08/17.
 */

public class LightData {

    private String ts;      // epoch time, Primary key

    private float lV;

    public LightData() {

    }
    public LightData(String timestamp, float lightValue) {
        this.ts= timestamp;
        this.lV = lightValue;
    }

    // getters and setters for the class
    public float getlV() {
        return lV;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String timestamp) {
        this.ts = timestamp;
    }

    public void setlV(float lightValue) {
        this.lV = lightValue;
    }
}

