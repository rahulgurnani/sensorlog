package com.curefit.sensorapp;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rahul on 15/09/17.
 */

public class Utility {
    public static float magnitude(List<Float> val) {
        return (float) (Math.pow(val.get(0), 2) + Math.pow(val.get(0), 2) + Math.pow(val.get(0), 2));
    }

    public static float computeStd(float vals[], int n, float mean) {
        float std = 0;
        for (int i = 0 ; i < n; i++) {
            float change = (vals[i] - mean);
            std += (change * change);
        }
        std = std / n;
        return (float) Math.sqrt(std);

    }

    public static float computeMedian(float vals[], int n) {
        Arrays.sort(vals);
        return vals[(n-1)/2];
    }

}
