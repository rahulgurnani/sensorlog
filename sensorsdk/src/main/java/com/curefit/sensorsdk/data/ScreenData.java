package com.curefit.sensorsdk.data;


import com.google.firebase.database.PropertyName;

/**
 * Created by rahul on 25/08/17.
 */

public class ScreenData {

    @PropertyName("st")
    private int state;
    @PropertyName("ts")
    long timestamp;

    public ScreenData() {

    }

    public ScreenData(int state, long timestamp) {
        this.state = state;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getState() {
        return state;
    }
}
