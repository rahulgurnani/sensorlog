package com.curefit.sensorapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rahul on 25/08/17.
 */

public class LightData {

    private float lV;
    long ts;

    public LightData() {

    }
    public LightData(float lightValue, long timestamp) {
        this.lV = lightValue;
        this.ts = timestamp;
    }

    // getters and setters for the class
    public float getlV() {
        return lV;
    }

    public long getTs() {
        return ts;
    }

    public void setlV(float lightValue) {
        this.lV = lightValue;
    }
}

