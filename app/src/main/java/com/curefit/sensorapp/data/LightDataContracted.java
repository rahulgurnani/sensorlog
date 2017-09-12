package com.curefit.sensorapp.data;

import android.support.v4.math.MathUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rahul on 06/09/17.
 */

public class LightDataContracted {
    private int n = 0;
    private float mean = 0;
    private float median = 0;
    private float max = Float.NEGATIVE_INFINITY;
    private float min = Float.POSITIVE_INFINITY;
    private float std = 0;
    private long timestamp;
    private float lastLightIntensity = 0;

    public LightDataContracted(List<LightData> alldata, long timestamp) {
        n = alldata.size();
        float intensities[] = new float[n];
        for (int i = 0 ; i < alldata.size(); i++ ) {
            mean += alldata.get(i).getlV();
            max = Math.max(alldata.get(i).getlV(), max);
            min = Math.min(alldata.get(i).getlV(), min);
            intensities[i] = alldata.get(i).getlV();
            lastLightIntensity = alldata.get(i).getlV();
        }
        mean  = mean / n;
        for (int i = 0 ; i < alldata.size(); i++ ) {
            std += Math.pow(alldata.get(i).getlV() - mean, 2);
        }
        std = std/n;
        Arrays.sort(intensities);
        median = intensities[n/2];
        this.timestamp = timestamp;
    }

    public int getN() {
        return n;
    }

    public float getStd() {
        return std;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getMean() {
        return mean;
    }

    public float getMedian() {
        return median;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getLastLightIntensity() {
        return lastLightIntensity;
    }
}
