package com.curefit.sensorapp.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rahul on 25/08/17.
 */

public class AccelerometerData {

    List<Float> aV;

    // Contructors
    public AccelerometerData() {

    }
    public AccelerometerData(float accValues[]) {
        this.aV = new ArrayList<Float>();
        this.aV.add(accValues[0]);
        this.aV.add(accValues[1]);
        this.aV.add(accValues[2]);
    }

    // getters and setters

    public List<Float> getaV() {
        return aV;
    }

    public void setaV(List<Float> accValues) {
        this.aV = accValues;
    }
}
