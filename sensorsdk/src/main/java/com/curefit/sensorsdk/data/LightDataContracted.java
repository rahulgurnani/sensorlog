package com.curefit.sensorsdk.data;

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
    private long ts;
    private float lLI = 0;

    public LightDataContracted(List<LightData> alldata, long timestamp) {
        n = alldata.size();
        float intensities[] = new float[n];
        for (int i = 0 ; i < alldata.size(); i++ ) {
            mean += alldata.get(i).getlV();
            max = Math.max(alldata.get(i).getlV(), max);
            min = Math.min(alldata.get(i).getlV(), min);
            intensities[i] = alldata.get(i).getlV();
            lLI = alldata.get(i).getlV();
        }
        mean  = mean / n;

        std = Utility.computeStd(intensities, n, mean);
        median = Utility.computeMedian(intensities, n);

        this.ts = timestamp;
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

    public long getTs() {
        return ts;
    }

    public float getlLI() {
        return lLI;
    }
}
