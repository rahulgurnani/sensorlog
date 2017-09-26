package com.curefit.sensorapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.curefit.sensorsdk.db.DataStoreHelper;

import java.util.HashMap;

/**
 * Created by rahul on 26/09/17.
 */

public class DataQueryHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 13;
    public static final String DATABASE_NAME = "datastorer.db";
    private static final String TABLE_SLEEP= "SleepData";
    private static DataQueryHelper dqh = null;
    private static SQLiteDatabase db = null;

    private DataQueryHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

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

        db.insert(TABLE_SLEEP, null, values);
    }


}
