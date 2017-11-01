package com.curefit.sensorsdk.db;

import android.util.Log;

import com.curefit.sensorsdk.Utility;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.List;

/**
 * Created by rahul on 08/08/17.
 */

public class FirebaseStoreHelper {
    private DatabaseReference mDatabase;
    private static final String URL = "https://sampleapp1-20c23.firebaseio.com/";     // Firebase storage URL
    private static FirebaseStoreHelper firebaseStoreHelper = null;

    public static FirebaseStoreHelper getInstance() {
        if (firebaseStoreHelper != null) {
            return firebaseStoreHelper;
        }
        firebaseStoreHelper = new FirebaseStoreHelper(URL);
        return firebaseStoreHelper;
    }

    public FirebaseStoreHelper(String url) {
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(url);
    }

    public void sendData(HashMap<String, List> h, String email, long currentTime) {
        Log.d("SensorApp", "---- Send data called ----");
        DatabaseReference newRef = mDatabase.child("NewContractedData");
        char characters [] = {'.', '#', '$', '[', ']'};
        for (char c: characters){
            email = email.replace(c, '-');
        }
        newRef = newRef.child(Utility.getDateTime().split("\\s")[0]);       // create a node with data time as current date time
        newRef = newRef.child(email);
        newRef = newRef.child(String.valueOf(currentTime));
        newRef.setValue(h);
    }


    public void sendMessage(HashMap<String, String> h, String email) {
        Log.d("SensorApp", "---- Send message called ----");
        DatabaseReference newRef = mDatabase.child("NewContractedData");
        char characters [] = {'.', '#', '$', '[', ']'};
        for (char c: characters){
            email = email.replace(c, '-');
        }
        newRef = newRef.child(Utility.getDateTime().split("\\s")[0]);       // create a node with data time as current date time
        newRef = newRef.child(email);
        newRef = newRef.child(String.valueOf(System.currentTimeMillis()));
        newRef.setValue(h);
    }
}
