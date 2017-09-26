package com.curefit.sensorsdk.sync;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.curefit.sensorsdk.db.DataStoreHelper;

/**
 * Created by rahul on 24/08/17.
 */

public class SensorDataProvider extends ContentProvider {

    private static final UriMatcher uriMatcher;
    private static final int ACCELEROMETER = 1;
    private static final int LIGHT = 2;
    private static final int SCREEN = 3;
    private static final int USER = 4;
    private static final int MESSAGE = 5;



    static {
        // this block is used for setting the value of static variable uriMatcher.
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(SensorDataContract.CONTENT_AUTHORITY, SensorDataContract.AccReadings.PATH_ACC, ACCELEROMETER);
        uriMatcher.addURI(SensorDataContract.CONTENT_AUTHORITY, SensorDataContract.UserData.PATH_USER, USER);
        uriMatcher.addURI(SensorDataContract.CONTENT_AUTHORITY, SensorDataContract.LightReadings.PATH_LIGHT, LIGHT);
        uriMatcher.addURI(SensorDataContract.CONTENT_AUTHORITY, SensorDataContract.ScreenReadings.PATH_SCREEN, SCREEN);
        uriMatcher.addURI(SensorDataContract.CONTENT_AUTHORITY, SensorDataContract.MessageData.PATH_MESSAGE, MESSAGE);
    }
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        this.db = DataStoreHelper.getInstance(getContext()).getDb();
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ACCELEROMETER:
                return SensorDataContract.AccReadings.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Invalid URI!");
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {
            case ACCELEROMETER:
                db.insert(SensorDataContract.AccReadings.TABLE_NAME, null, contentValues);
                break;
            case LIGHT:
                Log.d("SensorApp", "Inserted into Light");
                db.insert(SensorDataContract.LightReadings.TABLE_NAME, null, contentValues);
                break;
            case SCREEN:
                db.insert(SensorDataContract.ScreenReadings.TABLE_NAME, null, contentValues);
                break;
            case MESSAGE:
                db.insert(SensorDataContract.MessageData.TABLE_NAME, null, contentValues);
                break;
            case USER:
                db.insert(SensorDataContract.UserData.TABLE_NAME, null, contentValues);
                break;
            default: throw new IllegalArgumentException("Invalid URI!");
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows;
        switch (uriMatcher.match(uri)) {
            case ACCELEROMETER:
                rows = db.delete(SensorDataContract.AccReadings.TABLE_NAME, selection, selectionArgs);
                break;
            case LIGHT:
                rows = db.delete(SensorDataContract.LightReadings.TABLE_NAME, selection, selectionArgs);
                break;
            case SCREEN:
                rows = db.delete(SensorDataContract.ScreenReadings.TABLE_NAME, selection, selectionArgs);
                break;
            case MESSAGE:
                rows = db.delete(SensorDataContract.MessageData.TABLE_NAME, selection, selectionArgs);
                break;
            case USER:
                rows = db.delete(SensorDataContract.UserData.TABLE_NAME, selection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Invalid URI!");
        }

        return rows;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @Nullable CancellationSignal cancellationSignal) {
        return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // TODO: 22/09/17 check this
        this.db = DataStoreHelper.getInstance(getContext()).getDb();
        Cursor c;
        switch (uriMatcher.match(uri)) {
            // Query for multiple article results
            case ACCELEROMETER:
                Log.d("SensorApp", "Accelerometer");
                c = db.query(SensorDataContract.AccReadings.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case USER:
                c = db.query(SensorDataContract.UserData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case LIGHT:
                c = db.query(SensorDataContract.LightReadings.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SCREEN:
                c = db.query(SensorDataContract.ScreenReadings.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MESSAGE:
                c = db.query(SensorDataContract.MessageData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default: throw new IllegalArgumentException("Invalid URI!");
        }

        // Tell the cursor to register a content observer to observe changes to the
        // URI or its descendants.
        assert getContext() != null;
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
}
