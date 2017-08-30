package com.curefit.sensorapp;

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
    public static final String URL = "https://sensorapp-4fb6f.firebaseio.com";     // Firebase storage URL
    private static Context context;

    /*
        Private contructor because the class is singelton.
     */
    private GlobalVariable() {
        System.out.println("Global variable constructor called");
        firebaseStoreHelper = new FirebaseStoreHelper(URL);
    }

    /*
        This is a singelton class, so to get the instance of this class.
     */
    public static GlobalVariable getInstance() {
        if (globalVariable == null) {
            globalVariable = new GlobalVariable();
        }

        return globalVariable;
    }

    /*
        must set the user.
     */
    public void setUser(User user) {
        this.user = user;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public FirebaseStoreHelper getFirebaseStoreHelper() {
        return firebaseStoreHelper;
    }

    public User getUser() {
        return user;
    }

}
