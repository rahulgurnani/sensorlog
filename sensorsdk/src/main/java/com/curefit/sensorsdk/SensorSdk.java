package com.curefit.sensorsdk;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.curefit.sensorsdk.db.DataStoreHelper;
import com.curefit.sensorsdk.db.FirebaseStoreHelper;
import com.curefit.sensorsdk.services.SensorUpdateService;
import com.curefit.sensorsdk.sync.SensorDataContract;
import com.curefit.sensorsdk.sync.SyncAdapter;
import com.curefit.sensorsdk.sync.SyncUtils;

import java.util.HashMap;

/**
 * Created by rahul on 18/09/17.
 */


public class SensorSdk {

    private static Builder builder;

    private static DataStoreHelper dsh;

    public static Builder initialize(Context context) {
        builder = new Builder(context);
        dsh = DataStoreHelper.getInstance(builder.mContext);
        return builder;
    }

    public static String getDeviceId(){
        return builder.deviceId;
    }

    public static String getUserId(){
        return builder.userId;
    }

    /**
     *
     * @param login tells us whether the user is logging for the first time
     */
    public static void startService(boolean login) {
        Log.d("SensorApp", "startService called");
        if (login) {
            ContentValues values = new ContentValues();
            values.put("CURTIME", System.currentTimeMillis());
            values.put("MESSAGE", "login");
            builder.mContext.getContentResolver().insert(SensorDataContract.MessageData.CONTENT_URI,
                    values);
        }
        // start syncing using the sync adapter
        ContentValues values = new ContentValues();
        values.put("CURTIME", System.currentTimeMillis());
        values.put("NAME", builder.deviceId);
        values.put("EMAIL", builder.userId);
        String selectionArgs[] = { String.valueOf(System.currentTimeMillis()) };
        builder.mContext.getContentResolver().delete(SensorDataContract.UserData.CONTENT_URI, SensorDataContract.UserData.TIMESTAMP + " <= ?", selectionArgs);
        builder.mContext.getContentResolver().insert(SensorDataContract.UserData.CONTENT_URI, values);

//        dsh.addUser(builder.deviceId, builder.userId); // TODO add delete option
        SyncUtils.CreateSyncAccount(builder.mContext);
        SyncAdapter.performSync();

        // Start the service
        Intent i = new Intent(builder.mContext, SensorUpdateService.class);
        builder.mContext.startService(i);
    }

    public static void endService() {
        Log.d("SensorApp", "endService");
        ContentValues values = new ContentValues();
        values.put("CURTIME", System.currentTimeMillis());
        values.put("MESSAGE", "logout");
        builder.mContext.getContentResolver().insert(SensorDataContract.MessageData.CONTENT_URI,
                values);
        SyncAdapter.performSync();  // force sync when the user logs out
        Intent i = new Intent(builder.mContext, SensorUpdateService.class);
        String selectionArgs[] = { String.valueOf(System.currentTimeMillis()) };
        builder.mContext.getContentResolver().delete(SensorDataContract.UserData.CONTENT_URI, SensorDataContract.UserData.TIMESTAMP + " <= ?", selectionArgs);
//        dsh.deleteUser();
        SyncUtils.stopSync(builder.mContext);
        builder.mContext.stopService(i);
    }

    public static void sendSleepData(String name, int hour, int minute) {
        dsh.addEntrySleepTime(name, hour, minute);
    }

    public static class Builder  {
        private String deviceId;
        private String userId;
        private Context mContext;
        Builder(Context context){
            this.mContext = context;
        }

        public Builder setDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder build() {
            return this;
        }
    }
}
