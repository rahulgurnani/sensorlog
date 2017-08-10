package com.curefit.sensorapp;

/**
 * Created by rahul on 08/08/17.
 */

public class PayLoad {
    User user;
    Object data;
    String timestamp;
    String sensorType;
    public PayLoad(User user, Object data) {
        this.user = user;
        this.data = data;
        this.timestamp = DataStoreHelper.getDateTime();
    }

    public User getUser() {
        return user;
    }

    public Object getData() {
        return data;
    }
}
