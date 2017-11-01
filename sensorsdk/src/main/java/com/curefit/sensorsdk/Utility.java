package com.curefit.sensorsdk;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by rahul on 15/09/17.
 */

public class Utility {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * magnitude returns the magnitude of the vector val
     * @param val
     * @return
     */
    public static float magnitude(List<Float> val) {
        float squaredSum = 0;
        for (int i = 0; i < val.size(); i++) {
            squaredSum += val.get(0)*val.get(0) + val.get(1)*val.get(1) + val.get(2)*val.get(2);
        }

        return (float) (Math.sqrt(squaredSum));
    }

    /**
     * Compute std computes standard deviation
     * @param vals
     * @param n
     * @param mean
     * @return
     */
    public static float computeStd(float vals[], int n, float mean) {
        float std = 0;
        for (int i = 0 ; i < n; i++) {
            float change = (vals[i] - mean);
            std += (change * change);
        }
        std = std / n;
        return (float) Math.sqrt(std);
    }

    /**
     * Computes median for the array vals
     * @param vals
     * @param n
     * @return
     */
    public static float computeMedian(float vals[], int n) {
        Arrays.sort(vals);
        return vals[(n-1)/2];
    }

    /**
     * Utility function for getting the current time
     * @return current date in DATE_FORMAT
     */
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                DATE_FORMAT, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Computes vectorial difference between accelerometer values
     * @param a1
     * @param a2
     * @return
     */
    public static float vectorialDifference(List<Float> a1, List<Float> a2) {
        float delta[] = new float[a1.size()];

        float difference = 0;
        for (int i=0; i < a1.size(); i++) {
            difference = difference + delta[i]*delta[i];
        }

        return (float)Math.sqrt(difference);
    }
}
