package com.curefit.sensorapp.data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rahul on 06/09/17.
 */

public class AccDataContracted {
    private int n = 0;
    private float mean = 0;
    private float median = 0;
    private float max = Float.NEGATIVE_INFINITY;
    private float min = Float.POSITIVE_INFINITY;
    private float meanDiff = 0;
    private float maxDiff = Float.NEGATIVE_INFINITY;
    private float minDiff = Float.POSITIVE_INFINITY;
    private float std = 0;
    long timestamp;
    private float vectorialDifference(List<Float> a1, List<Float> a2) {
        float difference = (float) Math.pow(a1.get(0) - a2.get(0), 2) + (float) Math.pow(a1.get(1) - a2.get(1), 2) + (float) Math.pow(a1.get(2) - a2.get(2), 2);
        return (float)Math.sqrt(difference);
    }
    private float magnitude(List<Float> val) {
        return (float) (Math.pow(val.get(0), 2) + Math.pow(val.get(0), 2) + Math.pow(val.get(0), 2));
    }
    public AccDataContracted(List<AccelerometerData> alldata, long timestamp) {
        n = alldata.size();
        float magnitudes[] = new float[n];
        for(int i=0; i<alldata.size(); i++) {
            float current =magnitude(alldata.get(i).aV);
            magnitudes[i] = current;
            mean += current;
            this.max = Math.max(max, current);
            this.min = Math.min(min, current);
            if(i > 0) {
                float diff = vectorialDifference(alldata.get(i).aV, alldata.get(i-1).aV);
                meanDiff += diff;
                maxDiff = Math.max(maxDiff, diff);
                minDiff = Math.min(minDiff, diff);
            }
        }
        mean = mean / n;
        // if there is only one reading, then set the value to 0, infinity is not encodable
        if (n > 1)
            meanDiff = meanDiff/ (n -1 );
        else
            meanDiff = maxDiff = minDiff = 0;
        for(int i=0; i < alldata.size(); i++) {
            std = std + (float)Math.pow((float)magnitude(alldata.get(i).aV) - mean, 2);
        }
        std = (float) Math.sqrt(std);
        Arrays.sort(magnitudes);
        median = magnitudes[n/2];

        this.timestamp = timestamp;
    }

    public float getMax() {
        return max;
    }

    public float getMean() {
        return mean;
    }

    public float getMeanDiff() {
        return meanDiff;
    }

    public float getMedian() {
        return median;
    }

    public float getMin() {
        return min;
    }

    public float getStd() {
        return std;
    }

    public int getN() {
        return n;
    }

    public float getMaxDiff() {
        return maxDiff;
    }

    public float getMinDiff() {
        return minDiff;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

