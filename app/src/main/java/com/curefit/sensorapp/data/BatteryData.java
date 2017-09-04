package com.curefit.sensorapp.data;

import io.realm.RealmObject;

/**
 * Created by rahul on 25/08/17.
 */

public class BatteryData {
    int state;

    // Constructors
    public BatteryData() {

    }

    public BatteryData(int state) {
        this.state = state;
    }

    // getters and setters

    public int getBatteryState() {
        return state;
    }

    public void setBatteryState(int state) {
        this.state = state;
    }
}

