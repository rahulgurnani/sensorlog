package com.curefit.sensorapp;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rahul on 08/08/17.
 */

public class FirebaseStoreHelper {
    private DatabaseReference mDatabase;

    public FirebaseStoreHelper(String url) {
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(url);
    }
    public void sendData(PayLoad payLoad) {
        DatabaseReference newRef = mDatabase.child("DataNode ");
        String email = payLoad.getUser().email;

        // these characters are not acceptible by firebase for node names
        char characters [] = {'.', '#', '$', '[', ']'};
        for (char c: characters){
            email = email.replace(c, '-');
        }

        newRef = newRef.child(email);
        newRef = newRef.child(payLoad.timestamp);
        newRef.setValue(payLoad);
    }
}
