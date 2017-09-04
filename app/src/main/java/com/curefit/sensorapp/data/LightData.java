package com.curefit.sensorapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rahul on 25/08/17.
 */

public class LightData {

    private float lV;

    public LightData() {

    }
    public LightData(float lightValue) {
        this.lV = lightValue;
    }

    // getters and setters for the class
    public float getlV() {
        return lV;
    }

    public void setlV(float lightValue) {
        this.lV = lightValue;
    }
}

