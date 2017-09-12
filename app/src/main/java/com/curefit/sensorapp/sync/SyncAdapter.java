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
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.curefit.sensorapp.FirebaseStoreHelper;
import com.curefit.sensorapp.GlobalVariable;
import com.curefit.sensorapp.PayLoad;
import com.curefit.sensorapp.SensorData;
import com.curefit.sensorapp.data.AccDataContracted;
import com.curefit.sensorapp.data.LightData;
import com.curefit.sensorapp.data.LightDataContracted;
import com.curefit.sensorapp.data.PayLoadJson;
import com.curefit.sensorapp.data.ScreenData;
import com.curefit.sensorapp.data.User;
import com.curefit.sensorapp.data.AccelerometerData;
import com.curefit.sensorapp.db.DataStoreHelper;
import com.curefit.sensorapp.network.SensorClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
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
        String projectionAcc[] = { SensorDataContract.AccReadings.TIMESTAMP, SensorDataContract.AccReadings.ACCX, SensorDataContract.AccReadings.ACCY, SensorDataContract.AccReadings.ACCZ};

        long currentTime = System.currentTimeMillis();
        currentTime = currentTime - currentTime % WINDOW;      // rounded to last minute
        String selectionArgs[] = { String.valueOf(currentTime) };
        long currentMinuteEnd = -1;

        Cursor c = contentResolver.query(SensorDataContract.AccReadings.CONTENT_URI, projectionAcc, SensorDataContract.AccReadings.TIMESTAMP + "<= ?", selectionArgs, null);
        List<AccelerometerData> accReadings = new ArrayList<>();
        List<AccDataContracted> accContractedReadings = new ArrayList<>();
        List<AccelerometerData> accReadingsWindow = new ArrayList<>();      // represents the current window
        boolean flag_acc_absent = false;
        boolean flag_light_absent = false;
        boolean flag_screen_absent = false;

        if (c.moveToFirst()) {
            // traverse through c
            do {
                float accValues[] = new float[3];
                accValues[0] = Float.parseFloat(c.getString(1));
                accValues[1] = Float.parseFloat(c.getString(2));
                accValues[2] = Float.parseFloat(c.getString(3));
                long timestamp = Long.parseLong(c.getString(0));

                if(currentMinuteEnd == -1)
                    currentMinuteEnd = timestamp - (timestamp % WINDOW) + WINDOW;

                AccelerometerData data = new AccelerometerData(accValues, timestamp);
                System.out.println("------ Acc contracted ------");
                System.out.println(timestamp);
                System.out.println(currentMinuteEnd);
                accReadings.add(data);
                if (timestamp < currentMinuteEnd) {
                    accReadingsWindow.add(data);
                }
                else {
                    System.out.println("--- adding new value acc contracted ------- ");
                    if(accReadingsWindow.size() > 0)
                        accContractedReadings.add(new AccDataContracted(accReadingsWindow, currentMinuteEnd - WINDOW));
                    accReadingsWindow = new ArrayList<>();
                    currentMinuteEnd = timestamp - (timestamp % WINDOW) + WINDOW;
                }
            } while (c.moveToNext());
        }

        if (accReadingsWindow.size() > 0) {
            accContractedReadings.add(new AccDataContracted(accReadingsWindow, currentMinuteEnd - WINDOW));
        }

        HashMap<String, List> h = new HashMap<>();
//        h.put("acc", accReadings);
        System.out.println("Accelerometer Contracted Readings number : "  + String.valueOf(accReadings.size()));
        if(accContractedReadings.size() > 0)
            h.put("acc_contracted", accContractedReadings);
        else
            flag_acc_absent = true;

        // Light
        String projectionLight[] = { SensorDataContract.LightReadings.TIMESTAMP, SensorDataContract.LightReadings.LIGHT};

        c = contentResolver.query(SensorDataContract.LightReadings.CONTENT_URI, projectionLight, SensorDataContract.LightReadings.TIMESTAMP + "<= ?", selectionArgs, null);
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
                    System.out.println("----adding new contracted light value --------");
                    if (lightReadingsWindow.size() > 0)
                        lightContractedReadings.add(new LightDataContracted(lightReadingsWindow, currentMinuteEnd - WINDOW));
                    lightReadingsWindow = new ArrayList<>();
                    lightReadings.add(data);
                    currentMinuteEnd = timestamp - (timestamp % WINDOW) + WINDOW;
                }
            }while (c.moveToNext());
        }

//        h.put("light", lightReadings);
        System.out.println("LightContractedReadings : " + String.valueOf(lightContractedReadings.size()));
        if(lightReadingsWindow.size() > 0)
            lightContractedReadings.add(new LightDataContracted(lightReadingsWindow, currentTime));

        if (lightContractedReadings.size() > 0)
            h.put("light_contracted", lightContractedReadings);
        else
            flag_light_absent = true;
        // Screen
        String projectionScreen[] = { SensorDataContract.ScreenReadings.TIMESTAMP, SensorDataContract.ScreenReadings.SCREEN };
        c = contentResolver.query(SensorDataContract.ScreenReadings.CONTENT_URI, projectionScreen, SensorDataContract.ScreenReadings.TIMESTAMP + "<= ?" , selectionArgs, null);
        List<ScreenData> screenReadings = new ArrayList<>();

        if (c.moveToFirst()) {
            // traverse through c
            do {
                ScreenData data = new ScreenData(Integer.parseInt(c.getString(1)), Long.parseLong(c.getString(0)));
                screenReadings.add(data);
            }while (c.moveToNext());
        }

        if (screenReadings.size() > 0)
            h.put("screen", screenReadings);
        else
            flag_screen_absent = true;

        // in case none of the readings are present.
        if (flag_acc_absent && flag_light_absent && flag_screen_absent)
            return;

        System.out.println("Length of lists : ");
        System.out.println(screenReadings.size());
        System.out.println(lightReadings.size());
        System.out.println(accReadings.size());
        // user
        String projection2[] = {SensorDataContract.UserData.NAME, SensorDataContract.UserData.EMAIL };
        Cursor user_c = contentResolver.query(SensorDataContract.UserData.CONTENT_URI, projection2, null, null, null);
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
        }

        User user = new User(username, email);
        // send it to firebase database
        FirebaseApp.initializeApp(mContext);
        FirebaseStoreHelper f = new FirebaseStoreHelper(GlobalVariable.URL);
        f.sendData(h, user, currentTime);
        contentResolver.delete(SensorDataContract.AccReadings.CONTENT_URI, SensorDataContract.AccReadings.TIMESTAMP + " <= ?", selectionArgs);
        contentResolver.delete(SensorDataContract.LightReadings.CONTENT_URI, SensorDataContract.LightReadings.TIMESTAMP + " <= ?", selectionArgs);
        contentResolver.delete(SensorDataContract.ScreenReadings.CONTENT_URI, SensorDataContract.ScreenReadings.TIMESTAMP + " <= ?", selectionArgs);


//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        String jsonObject = gson.toJson(h);
//        System.out.println(jsonObject);
        // code to post the data
//        PayLoadJson alldata = new PayLoadJson(email, DataStoreHelper.getDateTime().split("\\s")[0], h);
//        postDataToServer(alldata);
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new MyRunnable(alldata, currentTime);
//        mainHandler.post(myRunnable);

    }
    class MyRunnable implements Runnable {
        PayLoadJson alldata;
        long currentTime;
        public MyRunnable(PayLoadJson alldata, long currentTime) {
            this.alldata = alldata;
            this.currentTime = currentTime;
        }
        @Override
        public void run() {
            postDataToServer(alldata);
        }
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

