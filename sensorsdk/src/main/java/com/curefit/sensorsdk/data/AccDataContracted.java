package com.curefit.sensorsdk.data;

import com.google.firebase.database.PropertyName;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rahul on 06/09/17.
 */

public class AccDataContracted {
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

    public long ts;

    /**
     * Computes vectorial difference between accelerometer values
     * @param a1
     * @param a2
     * @return
     */
    private float vectorialDifference(List<Float> a1, List<Float> a2) {
        float deltaX = a1.get(0) - a2.get(0);
        float deltaY = a1.get(1) - a2.get(1);
        float deltaZ = a1.get(2) - a2.get(2);

        float difference = deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ;
        return (float)Math.sqrt(difference);
    }

    public AccDataContracted(List<AccelerometerData> alldata, long timestamp) {
        n = alldata.size();
        float magnitudes[] = new float[n];
        float changes[] = new float[n-1];
        this.ts = timestamp;

        for(int i=0; i<alldata.size(); i++) {
            float current = Utility.magnitude(alldata.get(i).aV);
            magnitudes[i] = current;
            meanAbs += current;
            maxAbs = Math.max(maxAbs, current);
            minAbs = Math.min(minAbs, current);
            if(i > 0) {
                float diff = vectorialDifference(alldata.get(i).aV, alldata.get(i-1).aV);
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

//    public long getTs() {
//        return ts;
//    }
//
//    public float getStdAbs() {
//        return stdAbs;
//    }
//
//    public float getMinAbs() {
//        return minAbs;
//    }
//
//    public float getMaxAbs() {
//        return maxAbs;
//    }
//
//    public float getMaxChg() {
//        return maxChg;
//    }
//
//    public float getMdAbs() {
//        return mdAbs;
//    }
//
//    public float getMdChg() {
//        return mdChg;
//    }
//
//    public float getMeanAbs() {
//        return meanAbs;
//    }
//
//    public float getMeanChg() {
//        return meanChg;
//    }
//
//    public float getMinChg() {
//        return minChg;
//    }
//
//    public float getStdChg() {
//        return stdChg;
//    }
//
//    public int getN() {
//        return n;
//    }
}

