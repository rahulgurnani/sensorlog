package com.curefit.sensorsdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rahul on 28/07/17.
 */

public class DataStoreHelper extends SQLiteOpenHelper {

    // SQL queries for creating tables
    public static final int DATABASE_VERSION = 13;
    public static final String DATABASE_NAME = "datastorer.db";

    // Table names
    private static final String TABLE_ACC = "AccData";
    private static final String TABLE_SCREEN = "ScreenData";
    private static final String TABLE_LIGHT = "LightData";
    private static final String TABLE_USER = "UserData";
    private static final String TABLE_STATS = "StatsData";
    private static final String TABLE_SLEEP= "SleepData";
    private static final String TABLE_MESSAGE= "MessageData";

    // create table queries
    private static final String SQL_CREATE_ACC = "CREATE TABLE " + TABLE_ACC + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, ACCX REAL, ACCY REAL, ACCZ REAL)";
    private static final String SQL_CREATE_SCREEN= "CREATE TABLE " + TABLE_SCREEN + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, STATE INTEGER)";
    private static final String SQL_CREATE_LIGHT = "CREATE TABLE " + TABLE_LIGHT + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURTIME INTEGER, LIGHT FLOAT)";
    private static final String SQL_CREATE_USER = "CREATE TABLE " + TABLE_USER + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURTIME INTEGER, NAME TEXT, EMAIL TEXT)";
    private static final String SQL_CREATE_STATS = "CREATE TABLE " + TABLE_STATS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, SENSORNAME TEXT, LASTTIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, NUMBERVALS TEXT)";
    private static final String SQL_CREATE_SLEEP = "CREATE TABLE " + TABLE_SLEEP + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, HOUR INTEGER, MINUTE INTEGER, TYPE TEXT)";
    private static final String SQL_CREATE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CURTIME INTEGER, MESSAGE TEXT)";


    // Singelton class's object
    private static DataStoreHelper dsh = null;

    static private SQLiteDatabase db = null;
    // Constructor
    private DataStoreHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // onCreate, create all the tables.
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACC);
        db.execSQL(SQL_CREATE_LIGHT);
        db.execSQL(SQL_CREATE_SCREEN);
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_STATS);
        db.execSQL(SQL_CREATE_SLEEP);
        db.execSQL(SQL_CREATE_MESSAGE);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    // Singelton
    public static DataStoreHelper getInstance(Context context) {
        if(dsh == null) {
            dsh = new DataStoreHelper(context);
            dsh.open();
        }
        return dsh;
    }

    // open and
    private void open() {
        db = this.getWritableDatabase();
    }

    // whenever you update db number, this function is called
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO oldVersion to newVersion
        if (newVersion == 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACC);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIGHT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLEEP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
            onCreate(db);
        }
    }


}