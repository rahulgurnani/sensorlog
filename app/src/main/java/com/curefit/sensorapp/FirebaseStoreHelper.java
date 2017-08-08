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
        GlobalVariable globalVariable = GlobalVariable.getInstance();

        DatabaseReference newRef = mDatabase.child("DataNode").push();
        newRef.setValue(payLoad);
    }
}
