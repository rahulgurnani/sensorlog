package com.curefit.sensorapp.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.curefit.sensorapp.FirebaseStoreHelper;
import com.curefit.sensorapp.GlobalVariable;
import com.curefit.sensorapp.SensorData;
import com.curefit.sensorapp.data.LightData;
import com.curefit.sensorapp.data.ScreenData;
import com.curefit.sensorapp.data.User;
import com.curefit.sensorapp.data.AccelerometerData;
import com.curefit.sensorapp.db.DataStoreHelper;
import com.google.firebase.FirebaseApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rahul on 23/08/17.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    ContentResolver contentResolver;
    Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        mContext = context;
    }


    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        // Data transfer code here.
        Log.d("SensorApp", "onPerformSync");

        try {
            syncSensorData(syncResult);
        }
        catch (IOException ex) {
            // io exception
        }
        catch (RemoteException | OperationApplicationException ex) {
            // auth exception
        }
    }

    private void syncSensorData(SyncResult syncResult) throws IOException, RemoteException, OperationApplicationException {

        // Accelerometer
        String projectionAcc[] = {SensorDataContract.AccReadings.TIMESTAMP, SensorDataContract.AccReadings.ACCX, SensorDataContract.AccReadings.ACCY, SensorDataContract.AccReadings.ACCZ};

        String selectionArgs[] = { DataStoreHelper.getDateTime() };

        Cursor c = contentResolver.query(SensorDataContract.AccReadings.CONTENT_URI, projectionAcc, SensorDataContract.AccReadings.TIMESTAMP + "<= ?", selectionArgs, null);
        List<AccelerometerData> accReadings = new ArrayList<>();

        if (c.moveToFirst()) {
            // traverse through c
            do {
                float accValues[] = new float[3];
                accValues[0] = Float.parseFloat(c.getString(1));
                accValues[1] = Float.parseFloat(c.getString(2));
                accValues[2] = Float.parseFloat(c.getString(3));
                String timestamp = c.getString(0);
                AccelerometerData data = new AccelerometerData(timestamp, accValues);
                accReadings.add(data);
            } while (c.moveToNext());
        }

        HashMap<String, List> h = new HashMap<>();
        h.put("acc", accReadings);

        // Light
        String projectionLight[] = { SensorDataContract.LightReadings.TIMESTAMP, SensorDataContract.LightReadings.LIGHT};

        c = contentResolver.query(SensorDataContract.LightReadings.CONTENT_URI, projectionLight, SensorDataContract.LightReadings.TIMESTAMP + "<= ?", selectionArgs, null);
        List<LightData> lightReadings = new ArrayList<>();

        if (c.moveToFirst()) {
            // traverse through c
            do {
                LightData data = new LightData(c.getString(0), Float.parseFloat(c.getString(1)));
                lightReadings.add(data);
            }while (c.moveToNext());
        }

        h.put("light", lightReadings);

        // Screen
        String projectionScreen[] = { SensorDataContract.ScreenReadings.TIMESTAMP, SensorDataContract.ScreenReadings.SCREEN };
        c = contentResolver.query(SensorDataContract.ScreenReadings.CONTENT_URI, projectionScreen, SensorDataContract.ScreenReadings.TIMESTAMP + "<= ?" , selectionArgs, null);
        List<ScreenData> screenReadings = new ArrayList<>();

        if (c.moveToFirst()) {
            // traverse through c
            do {
                ScreenData data = new ScreenData(c.getString(0), Integer.parseInt(c.getString(1)));
                screenReadings.add(data);
            }while (c.moveToNext());
        }

        h.put("screen", screenReadings);

        // user
        String projection2[] = {SensorDataContract.UserData.NAME, SensorDataContract.UserData.EMAIL };
        Cursor user_c = contentResolver.query(SensorDataContract.UserData.CONTENT_URI, projection2, null, null, null);
        String username = null;
        String email= null;
        if (user_c.moveToFirst()) {
            // traverse and take the last one
            do {
                username = user_c.getString(0);
                email = user_c.getString
                        (1);
            }while (user_c.moveToNext());
        }
        else {
            // user not present
        }

        User user = new User(username, email);
        // send it to firebase database
        FirebaseApp.initializeApp(mContext);
        FirebaseStoreHelper f = new FirebaseStoreHelper(GlobalVariable.URL);
        f.sendData(h, user);
    }

    /**
     * Manual force Android to perform a sync with our SyncAdapter.
     */
    public static void performSync() {
        Log.e("SensorApp", "PerformSync called");
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(AccountGeneral.getAccount(),
                SensorDataContract.CONTENT_AUTHORITY, b);
    }
}

