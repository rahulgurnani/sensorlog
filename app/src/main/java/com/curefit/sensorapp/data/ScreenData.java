package com.curefit.sensorapp.data;

import io.realm.RealmObject;

/**
 * Created by rahul on 25/08/17.
 */

public class ScreenData {
    private String ts;      // timestamp
    private int st;

    public ScreenData() {

    }

    public ScreenData(String ts, int state) {
        this.ts = ts;
        this.st = state;
    }

    // getters and setters
    public String getTs() {
        return ts;
    }

    public int getSt() {
        return st;
    }

    public void setTs(String ts) {
        this.ts= ts;
    }

    public void setSt(int state) {
        this.st = state;
    }
}
