package com.curefit.sensorsdk.sync;

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


import com.curefit.sensorsdk.data.MessageData;
import com.curefit.sensorsdk.data.SleepData;
import com.curefit.sensorsdk.db.DataStoreHelper;
import com.curefit.sensorsdk.db.FirebaseStoreHelper;

import com.curefit.sensorsdk.data.AccDataContracted;
import com.curefit.sensorsdk.data.LightData;
import com.curefit.sensorsdk.data.LightDataContracted;
import com.curefit.sensorsdk.data.PayLoadJson;
import com.curefit.sensorsdk.data.ScreenData;
import com.curefit.sensorsdk.data.User;
import com.curefit.sensorsdk.data.AccelerometerData;
import com.curefit.sensorsdk.network.SensorClient;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rahul on 23/08/17.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver contentResolver;
    Context mContext;
    String TAG = "SensorApp";
    public final int WINDOW = 60000;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        // Data transfer code here.
        Log.d(TAG, "onPerformSync");

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

    /**
     *
     * @param selectionArgs : the time upto  which we will aggregate the readings of the accelerometer.
     * @return : the aggregarted accelerometer readings
     */
    private List<AccDataContracted> getAggAccelerometerData(String selectionArgs[]) {
        String projectionAcc[] = { SensorDataContract.AccReadings.TIMESTAMP, SensorDataContract.AccReadings.ACCX,
                SensorDataContract.AccReadings.ACCY, SensorDataContract.AccReadings.ACCZ};      // The columns that we need in our query.
        long currentMinuteEnd = -1;         // Keeps the value of the second at which the currentMinute ends

        // Querying accelerometer readings and contracting them per WINDOW.
        Cursor c = contentResolver.query(SensorDataContract.AccReadings.CONTENT_URI, projectionAcc,
                SensorDataContract.AccReadings.TIMESTAMP + "<= ?", selectionArgs, null);    // Querying using the cursor

        List<AccelerometerData> accReadings = new ArrayList<>();        // the original accReadings, this array can be used for debugging purposes.
        List<AccDataContracted> accAggReadings = new ArrayList<>();     // the Aggregated accReadings, this array is the one that we send over the server.
        List<AccelerometerData> accReadingsWindow = new ArrayList<>();      // stores readings current window


        if (c.moveToFirst()) {
            // traverse through c
            do {
                float accValues[] = new float[3];
                accValues[0] = Float.parseFloat(c.getString(1));
                accValues[1] = Float.parseFloat(c.getString(2));
                accValues[2] = Float.parseFloat(c.getString(3));
                long timestamp = Long.parseLong(c.getString(0));

                if(currentMinuteEnd == -1)
                    currentMinuteEnd = timestamp - (timestamp % WINDOW) + WINDOW;       // gives the upper limit of the current minute

                AccelerometerData data = new AccelerometerData(accValues, timestamp);
                accReadings.add(data);

                if (timestamp < currentMinuteEnd) {
                    accReadingsWindow.add(data);
                }
                else {
                    if(accReadingsWindow.size() > 0) {
                        accAggReadings.add(new AccDataContracted(accReadingsWindow, currentMinuteEnd - WINDOW));
                    }
                    accReadingsWindow = new ArrayList<>();
                    currentMinuteEnd = timestamp - (timestamp % WINDOW) + WINDOW;
                }
            } while (c.moveToNext());
        }

        // The last window needs to be added explicitly
        if (accReadingsWindow.size() > 0) {
            accAggReadings.add(new AccDataContracted(accReadingsWindow, currentMinuteEnd - WINDOW));
        }

        return accAggReadings;
    }

    /**
     *
     * @param selectionArgs : The time until which the readings need to be queried.
     * @return : returns the aggregated array of the readings.
     */
    private List<LightDataContracted> getAggLightData(String selectionArgs[]) {
        String projectionLight[] = { SensorDataContract.LightReadings.TIMESTAMP, SensorDataContract.LightReadings.LIGHT};
        long currentMinuteEnd = -1;         // Keeps the value of the second at which the currentMinute ends

        // Querying Light readings and contracting (aggregating) them over WINDOW.
        Cursor c = contentResolver.query(SensorDataContract.LightReadings.CONTENT_URI, projectionLight, SensorDataContract.LightReadings.TIMESTAMP + "<= ?", selectionArgs, null);
        List<LightData> lightReadings = new ArrayList<>();
        List<LightDataContracted> lightContractedReadings = new ArrayList<>();
        List<LightData> lightReadingsWindow = new ArrayList<>();
        currentMinuteEnd = -1;
        if (c.moveToFirst()) {
            // traverse through c
            do {
                long timestamp = Long.parseLong(c.getString(0));
                LightData data = new LightData(Float.parseFloat(c.getString(1)), Long.parseLong(c.getString(0)));
                lightReadings.add(data);

                if (currentMinuteEnd == -1)
                    currentMinuteEnd = timestamp - (timestamp % WINDOW) + WINDOW;

                if (timestamp < currentMinuteEnd) {
                    lightReadingsWindow.add(data);
                }
                else {
                    if (lightReadingsWindow.size() > 0)
                        lightContractedReadings.add(new LightDataContracted(lightReadingsWindow, currentMinuteEnd - WINDOW));
                    lightReadingsWindow = new ArrayList<>();
                    lightReadings.add(data);
                    currentMinuteEnd = timestamp - (timestamp % WINDOW) + WINDOW;
                }
            }while (c.moveToNext());
        }

        if(lightReadingsWindow.size() > 0)
            lightContractedReadings.add(new LightDataContracted(lightReadingsWindow, currentMinuteEnd - WINDOW));

        return lightContractedReadings;
    }

    /**
     * For screen we send all the readings
     * @param selectionArgs
     * @return
     */
    private List<ScreenData> getScreenData(String selectionArgs[]) {
        // Querying Screen readings and putting them in the hashmap
        String projectionScreen[] = { SensorDataContract.ScreenReadings.TIMESTAMP, SensorDataContract.ScreenReadings.SCREEN };
        Cursor c = contentResolver.query(SensorDataContract.ScreenReadings.CONTENT_URI, projectionScreen,
                SensorDataContract.ScreenReadings.TIMESTAMP + "<= ?" , selectionArgs, null);
        List<ScreenData> screenReadings = new ArrayList<>();

        if (c.moveToFirst()) {
            // traverse through c
            do {
                ScreenData data = new ScreenData(Integer.parseInt(c.getString(1)), Long.parseLong(c.getString(0)));
                screenReadings.add(data);
            }while (c.moveToNext());
        }
        return screenReadings;
    }

    private List<MessageData> getMessageData(String selectionArgs[]) {
        String projectionMessage[] = { SensorDataContract.MessageData.TIMESTAMP, SensorDataContract.MessageData.MESSAGE};
        Cursor c = contentResolver.query(SensorDataContract.MessageData.CONTENT_URI, projectionMessage,
                SensorDataContract.MessageData.TIMESTAMP + "<= ?", selectionArgs, null);
        List<MessageData> messageReadings = new ArrayList<>();

        if (c.moveToFirst()) {
            // traverse through c
            do {
                MessageData data = new MessageData(c.getString(1), Long.parseLong(c.getString(0)));
                messageReadings.add(data);
            }while (c.moveToNext());
        }
        return messageReadings;
    }

    private List<SleepData> getSleepData(String selectionArgs[]) {
        String projectionSleepData[] = { SensorDataContract.SleepData.TIMESTAMP,
                SensorDataContract.SleepData.HOUR, SensorDataContract.SleepData.MINUTE, SensorDataContract.SleepData.TYPE };
        Cursor c = contentResolver.query(SensorDataContract.SleepData.CONTENT_URI, projectionSleepData,
                SensorDataContract.SleepData.TIMESTAMP + " <= ?", selectionArgs, null);
        List<SleepData> sleepDatas = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                // TODO Finish this
            }while (c.moveToNext());
        }
        return sleepDatas;
    }

    /**
     * This function takes all the sensor data accumlated till the current time and aggregates the data over the current window. Then it sends the aggregated data.
     * @param syncResult
     * @throws IOException
     * @throws RemoteException
     * @throws OperationApplicationException
     */
    private void syncSensorData(SyncResult syncResult) throws IOException, RemoteException, OperationApplicationException {
        Log.d("SensorApp", "SyncSensorData called");
        HashMap<String, List> alldata = new HashMap<>();      // This hasmap holds all the data that needs to be sent
        boolean flag_acc_absent = false;
        boolean flag_light_absent = false;
        boolean flag_screen_absent = false;

        long currentTime = System.currentTimeMillis();
        currentTime = currentTime - currentTime % WINDOW;      // rounded to last minute
        String selectionArgs[] = { String.valueOf(currentTime) };

        // Accelerometer
        List<AccDataContracted> accAggReadings = getAggAccelerometerData(selectionArgs);

        if(accAggReadings.size() > 0)
            alldata.put("acc_contracted", accAggReadings);
        else
            flag_acc_absent = true;

        // Light
        List<LightDataContracted> lightAggReadings = getAggLightData(selectionArgs);
        if (lightAggReadings.size() > 0)
            alldata.put("light_contracted", lightAggReadings);
        else
            flag_light_absent = true;

        // Screen
        List<ScreenData> screenReadings = getScreenData(selectionArgs);
        if (screenReadings.size() > 0)
            alldata.put("screen", screenReadings);
        else
            flag_screen_absent = true;

        // in case none of the readings are present, don't send anything.
        if (flag_acc_absent && flag_light_absent && flag_screen_absent)
            return;

        // Messages
        List<MessageData> messageDatas = getMessageData(selectionArgs);
        if (messageDatas.size() > 0 )
            alldata.put("message", messageDatas);
        else
            Log.d("message data", "size is 0");

        // sleep time


        // User
        String projectionUser[] = {SensorDataContract.UserData.NAME, SensorDataContract.UserData.EMAIL };
        Cursor user_c = contentResolver.query(SensorDataContract.UserData.CONTENT_URI, projectionUser, null, null, null);

        String username = null;
        String email= null;
        if (user_c.moveToFirst()) {
            // traverse and take the last one
            do {
                username = user_c.getString(0);
                email = user_c.getString(1);

            }while (user_c.moveToNext());
        }
        else {
            // user not present
            return;
        }

//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        String jsonObject = gson.toJson(alldata);

        User user = new User(username, email);
        // send it to firebase database
        FirebaseApp.initializeApp(mContext);
        FirebaseStoreHelper f = FirebaseStoreHelper.getInstance();
        f.sendData(alldata, user.getEmail(), currentTime);

        // Delete the entries
        contentResolver.delete(SensorDataContract.AccReadings.CONTENT_URI, SensorDataContract.AccReadings.TIMESTAMP + " <= ?", selectionArgs);
        contentResolver.delete(SensorDataContract.LightReadings.CONTENT_URI, SensorDataContract.LightReadings.TIMESTAMP + " <= ?", selectionArgs);
        contentResolver.delete(SensorDataContract.ScreenReadings.CONTENT_URI, SensorDataContract.ScreenReadings.TIMESTAMP + " <= ?", selectionArgs);
        contentResolver.delete(SensorDataContract.MessageData.CONTENT_URI, SensorDataContract.MessageData.TIMESTAMP + " <= ?", selectionArgs);
        // TODO update the way of update to posting the data

        // code to post the data
//        PayLoadJson alldata = new PayLoadJson(email, DataStoreHelper.getDateTime().split("\\s")[0], h);
//        postDataToServer(alldata);
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new MyRunnable(alldata, currentTime);
//        mainHandler.post(myRunnable);
    }

//    class MyRunnable implements Runnable {
//        PayLoadJson alldata;
//        long currentTime;
//        public MyRunnable(PayLoadJson alldata, long currentTime) {
//            this.alldata = alldata;
//            this.currentTime = currentTime;
//        }
//        @Override
//        public void run() {
//            postDataToServer(alldata);
//        }
//    }

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

    public void postDataToServer(PayLoadJson alldata) {
        SensorClient.getSensorService(mContext).postData(alldata).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.i("sendReceivedEvent", "SUCCESS");
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.i("sendReceivedEvent", "onFailure");
            }
        });
    }
}

