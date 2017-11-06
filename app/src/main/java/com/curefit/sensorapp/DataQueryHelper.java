package com.curefit.sensorapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.curefit.sensorsdk.data.User;
import com.curefit.sensorsdk.db.FirebaseStoreHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by rahul on 26/09/17.
 *
 */

/**
 * DataQueryHelper helps us in getting sleep data, getting stats etc.
 */
public class DataQueryHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sensordata.db";

    private static DataQueryHelper dqh = null;
    private static SQLiteDatabase db = null;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String TABLE_ACC = "AccData";
    private static final String TABLE_SCREEN = "ScreenData";
    private static final String TABLE_LIGHT = "LightData";
    private static final String TABLE_USER = "UserData";
    private static final String TABLE_STATS = "StatsData";
    private static final String TABLE_SLEEP= "SleepData";
    private static final String TABLE_MESSAGE= "MessageData";;

    private static final String SQL_CREATE_ACC = "CREATE TABLE " + TABLE_ACC + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, ACCX REAL, ACCY REAL, ACCZ REAL)";
    private static final String SQL_CREATE_SCREEN= "CREATE TABLE " + TABLE_SCREEN + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, STATE INTEGER)";
    private static final String SQL_CREATE_LIGHT = "CREATE TABLE " + TABLE_LIGHT + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURTIME INTEGER, LIGHT FLOAT)";
    private static final String SQL_CREATE_USER = "CREATE TABLE " + TABLE_USER + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURTIME INTEGER, NAME TEXT, EMAIL TEXT)";
    private static final String SQL_CREATE_STATS = "CREATE TABLE " + TABLE_STATS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, SENSORNAME TEXT, LASTTIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, NUMBERVALS TEXT)";
    private static final String SQL_CREATE_SLEEP = "CREATE TABLE " + TABLE_SLEEP + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, HOUR INTEGER, MINUTE INTEGER, TYPE TEXT)";
    private static final String SQL_CREATE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, MESSAGE TEXT)";

    private DataQueryHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db.execSQL(SQL_CREATE_ACC);
        db.execSQL(SQL_CREATE_LIGHT);
        db.execSQL(SQL_CREATE_SCREEN);
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_STATS);
        db.execSQL(SQL_CREATE_SLEEP);
        db.execSQL(SQL_CREATE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int olderVersion, int newerVersion) {

    }

    // Singelton
    public static DataQueryHelper getInstance(Context context) {
        if(dqh == null) {
            dqh = new DataQueryHelper(context);
            dqh.open();
        }
        return dqh;
    }

    // open and
    private void open() {
        db = this.getWritableDatabase();
    }

    /*
        Get user name and emailid
     */
    public User getUser() {
        String selectQuery = "SELECT  * FROM " + TABLE_USER;     // Get the latest username entered.

        Cursor cursor = db.rawQuery(selectQuery, null);

        String name, email;
        User user = null;
        name = null;
        email = null;
        try {
            if (cursor.moveToFirst()) {
                do {
                    name = cursor.getString(2);
                    email = cursor.getString(3);
                }while (cursor.moveToNext());
            }
        } catch (Exception e) {

        }
        finally {
            if (cursor!=null)
                cursor.close();
        }

        if (name != null)       // if name gets initialized
            user = new User(name, email);

        return user;
    }

    // utlility function to check same day
    private boolean sameDay(String timestamp1, String timestamp2) {
        String date1 = timestamp1.split("\\s")[0];
        String date2 = timestamp2.split("\\s")[0];

        if (date1.equals(date2)) {
            return true;
        }
        return  false;
    }


    // getsleepdata returns the sleep data of today.
    public SleepData getSleepData() {
        SleepData sleepData = new SleepData();

        String startQuery = "SELECT * FROM " + TABLE_SLEEP+ " WHERE TYPE ='" + "start" +"' ORDER BY CURTIME DESC LIMIT 1";
        Cursor cursor = db.rawQuery(startQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    long timestamp = cursor.getLong(1);
                    int hour = Integer.parseInt(cursor.getString(2));
                    int minute = Integer.parseInt(cursor.getString(3));
                    Log.d("Timestamp", String.valueOf(timestamp) + " " + String.valueOf(hour) + " " + String.valueOf(minute));
                    SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    String stringTimestamp = df.format(timestamp);
                    Log.d("StringTimestamp", stringTimestamp);
                    if (sameDay(stringTimestamp, this.getDateTime())) {
                        sleepData.setHs(hour);
                        sleepData.setMs(minute);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {

        }
        finally {
            if (cursor!=null)
                cursor.close();
        }

        String endQuery = "SELECT * FROM " + TABLE_SLEEP+ " WHERE TYPE ='" + "end" +"' ORDER BY CURTIME DESC LIMIT 1";
        cursor = db.rawQuery(endQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    long timestamp = cursor.getLong(1);
                    int hour = Integer.parseInt(cursor.getString(2));
                    int minute = Integer.parseInt(cursor.getString(3));
                    SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    String stringTimestamp = df.format(timestamp);
                    if (sameDay(stringTimestamp, this.getDateTime())) {
                        sleepData.setHe(hour);
                        sleepData.setMe(minute);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {

        }
        finally {
            if (cursor!=null)
                cursor.close();
        }


        return sleepData;
    }

    /*
        getStats is used to get statistics of number of updates made etc.
     */
    public HashMap<String, String> getStats(String sensorName) {
        String selectQuery = "SELECT * FROM " + TABLE_STATS + " WHERE SENSORNAME='" + sensorName + "'";
        HashMap<String, String> stats = new HashMap<String, String>();
        String timestamp = "0";
        String numUpdates = "0";

        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if(cursor.moveToFirst()) {
                timestamp = cursor.getString(2);
                numUpdates = cursor.getString(3);
            }
        } catch (Exception e) {

        } finally {
            if (cursor!=null)
                cursor.close();
        }

        stats.put("LastTimeStamp", timestamp);
        stats.put("NumUpdates", numUpdates);

        return stats;
    }

    /*
        Function used to add sleep time entry.
     */
    public void addEntrySleepTime(String name, int hour, int minute) {
        Log.d("SensorApp", "addEntrySleepTime");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("name", name);
        data.put("hour", Integer.toString(hour));
        data.put("minute", Integer.toString(minute));
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long currentEpochTime = System.currentTimeMillis();
        Log.d("CurrentTime", String.valueOf(currentEpochTime) + " " + String.valueOf(hour) + " " + String.valueOf(minute));
        values.put("CURTIME", currentEpochTime);
        values.put("HOUR", hour);
        values.put("MINUTE", minute);
        values.put("TYPE", name);
        FirebaseStoreHelper.getInstance().sendMessage(data, getUser().getEmail());
        db.insert(TABLE_SLEEP, null, values);
    }


    /*
        Utility function for getting the current time, it's used for time stamps stored
     */
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                DATE_FORMAT, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
