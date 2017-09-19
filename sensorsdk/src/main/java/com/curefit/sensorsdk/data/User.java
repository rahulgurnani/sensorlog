package com.curefit.sensorsdk.data;

/**
 * Created by rahul on 08/08/17.
 */

/*
    Class to store user data.
 */
public class User {
    String name, email;

    public User() {

    }
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
