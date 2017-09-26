package com.curefit.sensorsdk.data;

import com.google.firebase.database.PropertyName;

/**
 * Created by rahul on 22/09/17.
 */

public class MessageData {
    @PropertyName("m")
    public String msg;
    public long ts;
    public MessageData(String message, long timestamp) {
        msg = message;
        ts = timestamp;
    }
}
