package com.curefit.sensorapp;

import android.app.Application;
import android.content.Context;

import com.curefit.sensorapp.data.User;

/**
 * Created by rahul on 08/08/17.
 */

/*
    GlobalVariable is a singelton class where we maintain global variables.
 */
public class GlobalVariable {
    private FirebaseStoreHelper firebaseStoreHelper;
    private User user;
    private static GlobalVariable globalVariable = null;
    public static String URL;     // Firebase storage URL

    private GlobalVariable(Context context) {
        firebaseStoreHelper = new FirebaseStoreHelper(context.getString(R.string.FIREBASE_URL));
    }

    public static GlobalVariable getInstance(Context applicationContext) {
        if (globalVariable == null) {
            globalVariable = new GlobalVariable(applicationContext);
        }

        return globalVariable;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FirebaseStoreHelper getFirebaseStoreHelper() {
        return firebaseStoreHelper;
    }

    public User getUser() {
        return user;
    }

}
