package com.curefit.sensorsdk.data;

/**
 * Created by rahul on 30/08/17.
 */

public class SleepData {
    private int hs;     // hour start
    private int ms;     // minute start
    private int he;     // hour end
    private int me;     // minute end
    private boolean su;     // start updated
    private boolean eu;     // end updated

    public SleepData() {
        hs = 20;
        ms = 0;
        he = 9;
        me = 0;
        su = false;
        eu = false;
    }

    public int getHe() {
        return he;
    }

    public int getHs() {
        return hs;
    }

    public int getMe() {
        return me;
    }

    public int getMs() {
        return ms;
    }

    public void setHe(int he) {
        this.he = he;
        su = true;
    }

    public void setHs(int hs) {
        this.hs = hs;
        su = true;
    }

    public void setMe(int me) {
        this.me = me;
        eu = true;
    }

    public void setMs(int ms) {
        this.ms = ms;
        eu = true;
    }

    public void setEu(boolean eu) {
        this.eu = eu;
    }

    public void setSu(boolean su) {
        this.su = su;
    }
    public boolean getSu() {
        return this.eu;
    }

    public boolean getEu() {
        return this.su;
    }

}
