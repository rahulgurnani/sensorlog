package com.curefit.sensorapp;

import com.curefit.sensorapp.data.User;
import com.curefit.sensorapp.db.DataStoreHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

/**
 * Created by rahul on 08/08/17.
 */

public class FirebaseStoreHelper {
    private DatabaseReference mDatabase;

    public FirebaseStoreHelper(String url) {
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(url);
    }
    public void sendData(PayLoad payLoad) {
        DatabaseReference newRef = mDatabase.child("DataNode");
        String email = payLoad.getUser().getEmail();

        // these characters are not acceptible by firebase for node names
        char characters [] = {'.', '#', '$', '[', ']'};
        for (char c: characters){
            email = email.replace(c, '-');
        }

        newRef = newRef.child(email);
        newRef = newRef.child(payLoad.timestamp);
        newRef.setValue(payLoad);
    }

    public void sendData(HashMap<String, List> h, User user) {
        DatabaseReference newRef = mDatabase.child("batchwiseData");
        String email = user.getEmail();// these characters are not acceptible by firebase for node names
        char characters [] = {'.', '#', '$', '[', ']'};
        for (char c: characters){
            email = email.replace(c, '-');
        }
        newRef = newRef.child(email);
        newRef = newRef.child(DataStoreHelper.getDateTime());       // create a node with data time as current date time
        newRef.setValue(h);
    }
}
