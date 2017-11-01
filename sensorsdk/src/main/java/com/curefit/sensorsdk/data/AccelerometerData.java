package com.curefit.sensorsdk.data;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rahul on 25/08/17.
 */

public class AccelerometerData {

    List<Float> accelerometerValue;
    long timestamp;

    // Contructors
    public AccelerometerData() {

    }

    public AccelerometerData(float accValues[], long timestamp) {
        this.accelerometerValue = new ArrayList<Float>();
        this.accelerometerValue.add(accValues[0]);
        this.accelerometerValue.add(accValues[1]);
        this.accelerometerValue.add(accValues[2]);
        this.timestamp = timestamp;
    }

    // getters and setters
    public List<Float> getAccelerometerValue() {
        return accelerometerValue;
    }

    public void setAccelerometerValue(List<Float> accValues) {
        this.accelerometerValue = accValues;
    }
}
