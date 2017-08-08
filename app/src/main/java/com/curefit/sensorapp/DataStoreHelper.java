package com.curefit.sensorapp;

import android.app.job.JobScheduler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by rahul on 28/07/17.
 */

public class DataStoreHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "datastorer.db";
    private static final String SQL_CREATE_ACC = "CREATE TABLE AccData(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ACCX REAL, ACCY REAL, ACCZ REAL)";
    private static final String SQL_CREATE_SCREEN= "CREATE TABLE ScreenData(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, STATE INTEGER)";
    private static final String SQL_CREATE_LIGHT = "CREATE TABLE LightData(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, LIGHT FLOAT)";
    private static final String SQL_CREATE_USER = "CREATE TABLE UserData(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, NAME TEXT, EMAIL TEXT)";

    private static final String TABLE_ACC = "AccData";
    private static final String TABLE_SCREEN = "ScreenData";
    private static final String TABLE_LIGHT= "LightData";
    private static final String TABLE_USER= "UserData";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public DataStoreHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACC);
        db.execSQL(SQL_CREATE_LIGHT);
        db.execSQL(SQL_CREATE_SCREEN);
        db.execSQL(SQL_CREATE_USER);
    }


    private PayLoad createPayLoadUtil(SensorData data) {
        User user = GlobalVariable.getInstance().getUser();
        PayLoad payLoad = new PayLoad(user, data);
        return payLoad;
    }

    private void postPayLoad(PayLoad payLoad) {
        GlobalVariable.getInstance().getFirebaseStoreHelper().sendData(payLoad);
    }
    /*
    This function adds accelerometer entries to the database.
     */
    public void addEntry(float[] accValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String timestamp = getDateTime();
        values.put("CURTIME", timestamp);
        values.put("ACCX", accValues[0]);
        values.put("ACCY", accValues[1]);
        values.put("ACCZ", accValues[2]);
        long newRowId = db.insert(TABLE_ACC, null, values);
        // post data to firebase database
        SensorData data = new SensorData(timestamp, accValues);
        PayLoad payLoad = createPayLoadUtil(data);
        postPayLoad(payLoad);
        System.out.println("Stored values1");
        db.close();
    }

    /*
        This function adds the screen state to the database, i.e. whether the screen is on or off.
     */
    public void addEntry(int state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String timestamp = getDateTime();
        values.put("CURTIME", timestamp);
        values.put("STATE", state);
        System.out.println("State : " + Integer.toString(state));

        long newRowId = db.insert(TABLE_SCREEN, null, values);
        System.out.println("---Stored values2");
        SensorData data = new SensorData(timestamp, state);
        PayLoad payLoad = createPayLoadUtil(data);
        postPayLoad(payLoad);

        db.close();
    }

    /*
        This function adds the value of light intensity to the database.
     */
    public void addEntry(float lightValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String timestamp = getDateTime();
        values.put("CURTIME", timestamp);
        values.put("LIGHT", lightValue);
        long newRowId = db.insert(TABLE_LIGHT, null, values);
        System.out.println("Stored values3");
        SensorData data = new SensorData(timestamp, lightValue);
        PayLoad payLoad = createPayLoadUtil(data);
        postPayLoad(payLoad);

        db.close();
    }

    public void addUser(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CURTIME", getDateTime());
        values.put("NAME", name);
        values.put("EMAIL", email);

        long newRowId = db.insert(TABLE_USER, null, values);

        System.out.println("added user");
        db.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIGHT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void printAllDataAcc() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ACC;


        Cursor cursor = db.rawQuery(selectQuery, null);
        List<SensorData> entries = new ArrayList<SensorData>();
        if (cursor.moveToFirst()) {
            do {
                System.out.println(cursor.getString(1));
                System.out.println(cursor.getString(2));
                System.out.println(cursor.getString(3));
                System.out.println(cursor.getString(4));

            } while (cursor.moveToNext());
        }
        db.close();

        return;
    }



    public List<SensorData> getAllDataAcc() {
        Log.d("In function", "getAllData");
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("Before executing the query");
        String selectQuery = "SELECT * FROM " + TABLE_ACC + " LIMIT 10";

        Cursor cursor = db.rawQuery(selectQuery, null);
        System.out.println("Query executed");
        List<SensorData> entries = new ArrayList<SensorData>();
        if (cursor.moveToFirst()) {
            do {
                float accValues[] = new float[3];
                accValues[0] = Float.parseFloat(cursor.getString(2));
                accValues[1] = Float.parseFloat(cursor.getString(3));
                accValues[2] = Float.parseFloat(cursor.getString(4));

                SensorData entry = new SensorData(cursor.getString(1), accValues);
                entries.add(entry);
            } while (cursor.moveToNext());
        }

        db.close();
        return entries;
    }


    public List<SensorData> getAllDataScreen() {

        Log.d("In function", "getAllData");
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_SCREEN;

        Cursor cursor = db.rawQuery(selectQuery, null);
        List<SensorData> entries = new ArrayList<SensorData>();
        if (cursor.moveToFirst()) {
            do {
                int screen = Integer.parseInt(cursor.getString(2));
                System.out.println(" Sensor data " + cursor.getString(2));
                SensorData entry = new SensorData(cursor.getString(1), screen);
                entries.add(entry);
            } while (cursor.moveToNext());
        }

        db.close();
        for (int i = 0; i < entries.size(); i++) {
            System.out.println(entries.get(i).getScreenValue());
            System.out.println(entries.get(i).getTimestamp());
        }
        return entries;
    }

    /*
        Function returns all the light sensor data
     */
    public List<SensorData> getAllDataLight() {

        Log.d("In function", "getAllData");
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_LIGHT;

        Cursor cursor = db.rawQuery(selectQuery, null);
        List<SensorData> entries = new ArrayList<SensorData>();
        if (cursor.moveToFirst()) {
            do {
                float light = Float.parseFloat(cursor.getString(2));

                SensorData entry = new SensorData(cursor.getString(1), light);
                entries.add(entry);
            } while (cursor.moveToNext());
        }

        db.close();
        return entries;
    }

    /*
        Get user name and emailid
     */
    public User getUser() {
        String selectQuery = "SELECT  * FROM " + TABLE_USER;     // Get the latest usename entered.
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String name, email;
        name = null;
        email = null;
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(2);
                email = cursor.getString(3);
            }while (cursor.moveToNext());
        }
        User user = null;

        if (name != null)       // if name gets initialized
            user = new User(name, email);

        return user;
    }
    /*
        Utility function for getting the current time, it's used for time stamps stored
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                DATE_FORMAT, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}