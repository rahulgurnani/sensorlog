package com.curefit.sensorsdk.data;


/**
 * Created by rahul on 25/08/17.
 */

public class BatteryData {
    int state;
    long ts;
    // Constructors
    public BatteryData() {

    }

    public BatteryData(int state) {
        this.state = state;
        this.ts = System.currentTimeMillis();
    }

    // getters and setters
    public int getBatteryState() {
        return state;
    }

    public void setBatteryState(int state) {
        this.state = state;
    }
}

