package com.curefit.sensorapp.data;

import io.realm.RealmObject;

/**
 * Created by rahul on 25/08/17.
 */

public class ScreenData {

    private int st;

    public ScreenData() {

    }

    public ScreenData(int state) {
        this.st = state;
    }

    public int getSt() {
        return st;
    }

    public void setSt(int state) {
        this.st = state;
    }
}
