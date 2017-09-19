package com.curefit.sensorsdk.data;

import com.curefit.sensorsdk.db.DataStoreHelper;

/**
 * Created by rahul on 08/08/17.
 */
// TODO: 18/09/17 remove it 
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
