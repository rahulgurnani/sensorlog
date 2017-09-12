package com.curefit.sensorapp.data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by rahul on 05/09/17.
 */

public class PayLoadJson {
    String username;
    String date;
    HashMap<String, List>  data;

    public PayLoadJson(String username, String date, HashMap<String, List> data) {
        this.username = username;
        this.date = date;
        this.data = data;
    }

    public HashMap<String, List> getData() {
        return data;
    }

    public String getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

}
