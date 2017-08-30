package com.curefit.sensorapp.data;

import io.realm.RealmObject;

/**
 * Created by rahul on 25/08/17.
 */

public class BatteryData {

    long time;      // epoch time, Primary key

    int state;

    // Constructors
    public BatteryData() {

    }

    public BatteryData(long time, int state) {
        this.time = time;
        this.state = state;
    }

    // getters and setters

    public long getTimestamp() {
        return time;
    }

    public int getBatteryState() {
        return state;
    }

    public void setTimestamp(long time) {
        this.time = time;
    }

    public void setBatteryState(int state) {
        this.state = state;
    }
}

