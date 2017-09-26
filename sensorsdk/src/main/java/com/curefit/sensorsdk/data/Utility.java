package com.curefit.sensorsdk.data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rahul on 15/09/17.
 */

public class Utility {
    public static float magnitude(List<Float> val) {
        float x = val.get(0);
        float y = val.get(1);
        float z = val.get(2);

        return (float) (Math.sqrt(x*x + y*y + z*z));
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
