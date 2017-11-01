package com.curefit.sensorsdk.data;

import com.curefit.sensorsdk.Utility;
import com.google.firebase.database.PropertyName;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rahul on 06/09/17.
 */

public class AggregatedAccelerometerData {
    // we will serialize the names into small strings and so that the amount of data tranfered is less
    @PropertyName("n")
    public int n = 0;      // number of readings
    @PropertyName("a")
    public float meanAbs = 0;
    @PropertyName("b")
    public float mdAbs = 0;
    @PropertyName("c")
    public float maxAbs = Float.NEGATIVE_INFINITY;
    @PropertyName("d")
    public float minAbs = Float.POSITIVE_INFINITY;
    @PropertyName("e")
    public float stdAbs = 0;

    @PropertyName("f")
    public float meanChg = 0;
    @PropertyName("g")
    public float mdChg = 0;
    @PropertyName("h")
    public float maxChg = Float.NEGATIVE_INFINITY;
    @PropertyName("i")
    public float minChg = Float.POSITIVE_INFINITY;
    @PropertyName("j")
    public float stdChg = 0;

    public long timestamp;

    /**
     * Computes aggregates value from the given list of data of accelerometer
     * @param alldata list of accelerometer data
     * @param timestamp the minute for which data is aggregated etc.
     */
    public AggregatedAccelerometerData(List<AccelerometerData> alldata, long timestamp) {
        n = alldata.size();
        float magnitudes[] = new float[n];
        float changes[] = new float[n-1];
        this.timestamp = timestamp;

        for(int i=0; i<alldata.size(); i++) {
            float current = Utility.magnitude(alldata.get(i).accelerometerValue);
            magnitudes[i] = current;
            meanAbs += current;
            maxAbs = Math.max(maxAbs, current);
            minAbs = Math.min(minAbs, current);
            if(i > 0) {
                float diff = Utility.vectorialDifference(alldata.get(i).accelerometerValue, alldata.get(i-1).accelerometerValue);
                changes[i-1] = diff;
                meanChg += diff;
                maxChg = Math.max(maxChg, diff);
                minChg = Math.min(minChg, diff);
            }
        }

        meanAbs = meanAbs / n;
        stdAbs = Utility.computeStd(magnitudes, n, meanAbs);
        mdAbs = Utility.computeMedian(magnitudes, n);

        if (n > 1) {
            meanChg = meanChg/ (n -1 );
            Utility.computeStd(changes, n-1, meanChg);
            Arrays.sort(changes);
            mdChg= Utility.computeMedian(changes, n-1);
            stdChg= Utility.computeStd(changes, n-1, meanChg);
        }
        else {
            // if there is only one reading, then set the value to 0, infinity is not encodable
            meanChg = maxChg = minChg = stdChg = mdChg = 0;
        }
    }
}

