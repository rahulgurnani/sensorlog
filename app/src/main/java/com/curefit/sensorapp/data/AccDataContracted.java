package com.curefit.sensorapp.data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rahul on 06/09/17.
 */

public class AccDataContracted {
    private int n = 0;
    private float meanAbs = 0;
    private float medianAbs = 0;
    private float maxAbs = Float.NEGATIVE_INFINITY;
    private float minAbs = Float.POSITIVE_INFINITY;
    private float stdAbs = 0;

    private float meanChange = 0;
    private float maxChange = Float.NEGATIVE_INFINITY;
    private float minChange = Float.POSITIVE_INFINITY;
    private float stdChange = 0;
    private float medianChange = 0;
    long timestamp;

    private float vectorialDifference(List<Float> a1, List<Float> a2) {
        float difference = (float) Math.pow(a1.get(0) - a2.get(0), 2) + (float) Math.pow(a1.get(1) - a2.get(1), 2) + (float) Math.pow(a1.get(2) - a2.get(2), 2);
        return (float)Math.sqrt(difference);
    }



    public AccDataContracted(List<AccelerometerData> alldata, long timestamp) {
        float magnitudes[] = new float[n];
        float changes[] = new float[n-1];

        this.timestamp = timestamp;
        n = alldata.size();

        for(int i=0; i<alldata.size(); i++) {
            float current = Utility.magnitude(alldata.get(i).aV);
            magnitudes[i] = current;
            meanAbs += current;
            maxAbs = Math.max(maxAbs, current);
            minAbs = Math.min(minAbs, current);
            if(i > 0) {
                float diff = vectorialDifference(alldata.get(i).aV, alldata.get(i-1).aV);
                changes[i] = diff;
                meanChange += diff;
                maxChange = Math.max(maxChange, diff);
                minChange = Math.min(minChange, diff);
            }
        }

        meanAbs = meanAbs / n;
        stdAbs = Utility.computeStd(magnitudes, n, meanAbs);
        medianAbs = Utility.computeMedian(magnitudes, n);

        if (n > 1) {
            meanChange = meanChange/ (n -1 );
            Utility.computeStd(changes, n-1, meanChange);
            Arrays.sort(changes);
            medianChange = Utility.computeMedian(changes, n-1);
            stdChange = Utility.computeStd(changes, n-1, meanChange);
        }
        else {
            // if there is only one reading, then set the value to 0, infinity is not encodable
            meanChange = maxChange = minChange = stdChange = medianChange = 0;
        }
    }


    public int getN() {
        return n;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getMaxAbs() {
        return maxAbs;
    }

    public float getMaxChange() {
        return maxChange;
    }

    public float getMeanAbs() {
        return meanAbs;
    }

    public float getMeanChange() {
        return meanChange;
    }

    public float getMedianAbs() {
        return medianAbs;
    }

    public float getMinAbs() {
        return minAbs;
    }

    public float getMinChange() {
        return minChange;
    }

    public float getStdAbs() {
        return stdAbs;
    }

    public float getStdChange() {
        return stdChange;
    }

    public float getMedianChange() {
        return medianChange;
    }
}

