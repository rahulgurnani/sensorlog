package com.curefit.sensorapp.data;

import io.realm.RealmObject;

/**
 * Created by rahul on 25/08/17.
 */

public class ScreenData {

    private int st;
    long ts;

    public ScreenData() {

    }

    public ScreenData(int state, long timestamp) {
        this.st = state;
        this.ts = timestamp;
    }

    public int getSt() {
        return st;
    }

    public void setSt(int state) {
        this.st = state;
    }

    public long getTs() {
        return ts;
    }
}
