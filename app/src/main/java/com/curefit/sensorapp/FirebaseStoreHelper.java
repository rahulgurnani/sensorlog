package com.curefit.sensorapp;

import android.util.Log;

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

    public void sendData(PayLoad payLoad, String type) {
        // we will post the data like /NewData/< date >/ <userid> /< time in epoch >
        String email = payLoad.getUser().getEmail();
        long currentEpochTime = System.currentTimeMillis();
        String currentTimestamp = DataStoreHelper.getDateTime();
        String currentDate = currentTimestamp.split("\\s")[0];
        // these characters are not acceptible by firebase for node names
        char characters [] = {'.', '#', '$', '[', ']'};
        for (char c: characters) {
            email = email.replace(c, '-');
        }

        DatabaseReference newRef = mDatabase.child("NewData");
        newRef = newRef.child(currentDate);
        newRef = newRef.child(email);
        newRef = newRef.child(type);
        newRef = newRef.child(String.valueOf(currentEpochTime));
        newRef.setValue(payLoad.data);
    }

    public void sendData(HashMap<String, List> h, User user, long currentTime) {
        System.out.println("---- Send data called ----");
        DatabaseReference newRef = mDatabase.child("ContractedData");
        String email = user.getEmail();// these characters are not acceptible by firebase for node names
        char characters [] = {'.', '#', '$', '[', ']'};
        for (char c: characters){
            email = email.replace(c, '-');
        }
        newRef = newRef.child(DataStoreHelper.getDateTime().split("\\s")[0]);       // create a node with data time as current date time
        newRef = newRef.child(email);
        newRef = newRef.child(String.valueOf(currentTime));
        newRef.setValue(h);
    }

}
