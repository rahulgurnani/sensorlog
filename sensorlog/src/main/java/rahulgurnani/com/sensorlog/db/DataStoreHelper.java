package rahulgurnani.com.sensorlog.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import rahulgurnani.com.sensorlog.SensorData;

/**
 * Created by rahul on 28/07/17.
 */

public class DataStoreHelper extends SQLiteOpenHelper {
    private static Context applicationContext;
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
        DataStoreHelper.applicationContext = context.getApplicationContext();
        return dsh;
    }

    /*
        This function is called when a new data for a sensor is sent so as to update sensor stats for that sensorType. So the local count of updates is maintained using this function.
     */
    public void updateSensorStats(String sensorName) {
        String selectQuery = "SELECT * FROM " + TABLE_STATS + " WHERE SENSORNAME='" + sensorName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String timestamp = getDateTime();
        if (!cursor.moveToFirst()) {
            System.out.println("num initializing");
            ContentValues values = new ContentValues();
            values.put("SENSORNAME", sensorName);
            values.put("LASTTIME", timestamp);
            values.put("NUMBERVALS", "1");
            db.insert(TABLE_STATS, null, values);
        }
        else {
            System.out.println("num increment");
            HashMap<String, String> stats = getStats(sensorName);
            String numUpdates = stats.get("NumUpdates");
            int num = Integer.parseInt(numUpdates);
            System.out.println("Num = " + Integer.toString(num));
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
        System.out.println("--Get stats: "+ sensorName);
        System.out.println("Timestamp : " + timestamp);
        System.out.println("Numupdates : " + numUpdates);
        db.close();

        return stats;
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
//        System.out.println("Stored values1");
        db.close();
        updateSensorStats("Accelerometer");
    }

    /*
    function used to add sleep time entry.
     */
    public void addEntrySleepTime(String name, int hour, int minute) {
        HashMap<String, String> data = new HashMap<String, String>();
//        data.put("sensorType", "User input");
        data.put("name", name);
        data.put("hour", Integer.toString(hour));
        data.put("minute", Integer.toString(minute));
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
//        String timestamp = getDateTime();
        long currentEpochTime = System.currentTimeMillis();
        values.put("CURTIME", currentEpochTime);
        values.put("HOUR", hour);
        values.put("MINUTE", minute);
        values.put("TYPE", name);

        db.insert(TABLE_SLEEP, null, values);
        db.close();
    }

    // utlility function to check same day
    private boolean sameDay(String timestamp1, String timestamp2) {
        String date1 = timestamp1.split("\\s")[0];
        String date2 = timestamp2.split("\\s")[0];
        System.out.println("Dates : ");
        System.out.println(date1);
        System.out.println(date2);
        if (date1.equals(date2)) {
            return true;
        }
        return  false;
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
        System.out.println("State : " + Integer.toString(state));

        db.insert(TABLE_SCREEN, null, values);
        db.close();
        updateSensorStats("Screen");
    }
    /*
        This function adds the screen state to the database, i.e. whether the screen is on or off.
     */
    public void addEntryCharging(int state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long currentTimeMillis= System.currentTimeMillis();
        values.put("CURTIME", currentTimeMillis);
        values.put("STATE", state);
        System.out.println("State : " + Integer.toString(state));

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
    public void addUser(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long currentEpochTime = System.currentTimeMillis();
        values.put("CURTIME", currentEpochTime);
        values.put("NAME", name);
        values.put("EMAIL", email);

        db.insert(TABLE_USER, null, values);

        System.out.println("added user");
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
        String selectQuery = "SELECT * FROM " + TABLE_SCREEN + " ORDER BY CURTIME DESC LIMIT 20";      // assuming we will find both start and end time within last 20 querires;

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
        Utility function for getting the current time, it's used for time stamps stored
     */
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                DATE_FORMAT, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}