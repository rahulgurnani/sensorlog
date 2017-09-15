package com.curefit.sensorapp.extras;

import android.content.Context;

/**
 * Created by rahul on 02/08/17.
 */

public class BuilderSingelton {
    private static BuilderSingelton obj = null;
    private BuilderSingelton() { }

    public static BuilderSingelton getInstance() {
        if (obj == null) {
            obj = new BuilderSingelton();
        }
        return obj;
    }
    public void scheduleJob(Context context) {

    }
}
