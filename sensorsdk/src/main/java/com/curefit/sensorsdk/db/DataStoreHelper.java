package com.curefit.sensorsdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.curefit.sensorsdk.data.PayLoad;
import com.curefit.sensorsdk.data.SensorData;
import com.curefit.sensorsdk.data.SleepData;
import com.curefit.sensorsdk.data.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by rahul on 28/07/17.
 */

public class DataStoreHelper extends SQLiteOpenHelper {

    // SQL queries for creating tables
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "datastorer.db";
    private static final String SQL_CREATE_ACC = "CREATE TABLE AccData(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, ACCX REAL, ACCY REAL, ACCZ REAL)";
    private static final String SQL_CREATE_SCREEN= "CREATE TABLE ScreenData(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, STATE INTEGER)";
    private static final String SQL_CREATE_LIGHT = "CREATE TABLE LightData(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURTIME INTEGER, LIGHT FLOAT)";
    private static final String SQL_CREATE_USER = "CREATE TABLE UserData(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURTIME INTEGER, NAME TEXT, EMAIL TEXT)";
    private static final String SQL_CREATE_STATS = "CREATE TABLE StatsData(ID INTEGER PRIMARY KEY AUTOINCREMENT, SENSORNAME TEXT, LASTTIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, NUMBERVALS TEXT)";
    private static final String SQL_CREATE_SLEEP = "CREATE TABLE SleepData(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, HOUR INTEGER, MINUTE INTEGER, TYPE TEXT)";

    // Table names
    private static final String TABLE_ACC = "AccData";
    private static final String TABLE_SCREEN = "ScreenData";
    private static final String TABLE_LIGHT = "LightData";
    private static final String TABLE_USER = "UserData";
    private static final String TABLE_STATS = "StatsData";
    private static final String TABLE_SLEEP= "SleepData";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static DataStoreHelper dsh = null;          // Singelton class's object

    private DataStoreHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACC);
        db.execSQL(SQL_CREATE_LIGHT);
        db.execSQL(SQL_CREATE_SCREEN);
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_STATS);
        db.execSQL(SQL_CREATE_SLEEP);
    }

    public SQLiteDatabase getDb() {
        return this.getWritableDatabase();
    }

    public static DataStoreHelper getInstance(Context context) {
        if(dsh == null) {
            dsh = new DataStoreHelper(context);
        }
        return dsh;
    }

    /*
        TODO : This function is for debug only
        This function is called when a new data for a sensor is sent so as to update sensor stats for that sensorType. So the local count of updates is maintained using this function.
     */
    public void updateSensorStats(String sensorName) {
        String selectQuery = "SELECT * FROM " + TABLE_STATS + " WHERE SENSORNAME='" + sensorName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String timestamp = getDateTime();
        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put("SENSORNAME", sensorName);
            values.put("LASTTIME", timestamp);
            values.put("NUMBERVALS", "1");
            db.insert(TABLE_STATS, null, values);
        }
        else {
            HashMap<String, String> stats = getStats(sensorName);
            String numUpdates = stats.get("NumUpdates");
            int num = Integer.parseInt(numUpdates);
            num++;
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("SENSORNAME", sensorName);
            values.put("LASTTIME", timestamp);
            values.put("NUMBERVALS", Integer.toString(num));

            db.update(TABLE_STATS, values, "SENSORNAME='"+sensorName+"'", null);    
        }
        db.close();
    }

    /*
    getStats is used to get statistics of number of updates made etc.
     */
    public HashMap<String, String> getStats(String sensorName) {
        String selectQuery = "SELECT * FROM " + TABLE_STATS + " WHERE SENSORNAME='" + sensorName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        HashMap<String, String> stats = new HashMap<String, String>();
        String timestamp = "-1";
        String numUpdates = "-1";
        if(cursor.moveToFirst()) {
            timestamp = cursor.getString(2);
            numUpdates = cursor.getString(3);
        }
        stats.put("LastTimeStamp", timestamp);
        stats.put("NumUpdates", numUpdates);
        db.close();

        return stats;
    }

    /*
        Create payload for the data object
    */
    private PayLoad createPayLoadUtil(Object data) {
        User user = this.getUser();
        PayLoad payLoad = new PayLoad(user, data);
        return payLoad;
    }

    /*
        Send payload over firebase
     */
    private void postPayLoad(PayLoad payLoad, String type) {
        FirebaseStoreHelper.getInstance().sendData(payLoad, type);
    }

    /**
     * Safely compare two dates, null being considered "greater" than a Date
     * @return the earliest of the two
     */
    public static Date least(Date a, Date b) {
        return a == null ? b : (b == null ? a : (a.before(b) ? a : b));
    }

    /*
    This function adds accelerometer entries to the database.
     */
    public void addEntryAcc(float[] accValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long currentEpochTime = System.currentTimeMillis();
        values.put("CURTIME", currentEpochTime);

        values.put("ACCX", accValues[0]);
        values.put("ACCY", accValues[1]);
        values.put("ACCZ", accValues[2]);
        long newRowId = db.insert(TABLE_ACC, null, values);
        // post data to firebase database
//        AccelerometerData data = new AccelerometerData(accValues);

        // check for time if there is a difference of more than 30 minutes, then send the data etc.
//        PayLoad payLoad = createPayLoadUtil(data);

//        postPayLoad(payLoad, "acc");
        db.close();
        updateSensorStats("Accelerometer");
    }

    /*
    function used to add sleep time entry.
     */
    public void addEntrySleepTime(String name, int hour, int minute) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("name", name);
        data.put("hour", Integer.toString(hour));
        data.put("minute", Integer.toString(minute));
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        long currentEpochTime = System.currentTimeMillis();
        values.put("CURTIME", currentEpochTime);
        values.put("HOUR", hour);
        values.put("MINUTE", minute);
        values.put("TYPE", name);

        db.insert(TABLE_SLEEP, null, values);

        PayLoad payLoad = createPayLoadUtil(data);
        postPayLoad(payLoad, "sleeptime");
        db.close();
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

    public SleepData getSleepData() {
        SleepData sleepData = new SleepData();

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SLEEP+ " ORDER BY CURTIME DESC LIMIT 20";      // assuming we will find both start and end time within last 20 querires

        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean start_done = false;
        boolean end_done = false;
        if (cursor.moveToFirst()) {
            do {
                String timestamp = cursor.getString(1);
                int hour = Integer.parseInt(cursor.getString(2));
                int minute = Integer.parseInt(cursor.getString(3));
                String type = cursor.getString(4);
                if(sameDay(timestamp, this.getDateTime()) ) {
                    if (type.equals("start")) {
                        sleepData.setHs(hour);
                        sleepData.setMs(minute);
                        start_done = true;
                    } else {
                        sleepData.setHe(hour);
                        sleepData.setMe(minute);
                        end_done = true;
                    }
                }
                if (start_done && end_done)
                    break;
            } while (cursor.moveToNext());
        }

        db.close();

        return sleepData;
    }

    /*
        This function adds the screen state to the database, i.e. whether the screen is on or off.
     */
    public void addEntryScreen(int state) {

        Log.d("SensorApp", "adding screen entry to the database " + String.valueOf(state));
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long currentTimeMillis = System.currentTimeMillis();
        values.put("CURTIME", currentTimeMillis);
        values.put("STATE", state);

        db.insert(TABLE_SCREEN, null, values);

        db.close();
        updateSensorStats("Screen");
    }
    /*
        This function adds the screen state to the database, i.e. whether the screen is on or off.
     */
    public void addEntryCharging(int state) {
        /*
           This information will be sent with atleast a gap of 30 minutes
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long currentTimeMillis= System.currentTimeMillis();
        values.put("CURTIME", currentTimeMillis);
        values.put("STATE", state);
        db.close();
    }

    /*
        This function adds the value of light intensity to the database.
     */
    public void addEntryLight(float lightValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long currentEpochTime = System.currentTimeMillis();
        values.put("CURTIME", currentEpochTime);
        values.put("LIGHT", lightValue);
        db.insert(TABLE_LIGHT, null, values);
        db.close();
        updateSensorStats("Light");
    }

    /*
        addUser to db
     */
    public void addUser(String deviceId, String userId) {
        if (deviceId == null || userId == null) {
            Log.e("SensorApp", "userid or deviceid null");
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        // delete the previous entries from the table
        db.execSQL("delete from "+ TABLE_USER);
        ContentValues values = new ContentValues();
        long currentEpochTime = System.currentTimeMillis();
        values.put("CURTIME", currentEpochTime);
        values.put("NAME", deviceId);
        values.put("EMAIL", userId);

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    // whenever you update db number, this function is called
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIGHT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
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
        String selectQuery = "SELECT * FROM " + TABLE_ACC + " LIMIT 10";

        Cursor cursor = db.rawQuery(selectQuery, null);
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
        String selectQuery = "SELECT * FROM " + TABLE_SCREEN + " ORDER BY CURTIME DESC LIMIT 20";      // assuming we will find both start and end time within last 20 querires;

        Cursor cursor = db.rawQuery(selectQuery, null);
        List<SensorData> entries = new ArrayList<SensorData>();
        if (cursor.moveToFirst()) {
            do {
                int screen = Integer.parseInt(cursor.getString(2));
                SensorData entry = new SensorData(cursor.getString(1), screen);
                entries.add(entry);
            } while (cursor.moveToNext());
        }

        db.close();

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
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                DATE_FORMAT, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}