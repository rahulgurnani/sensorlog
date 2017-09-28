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

    public static Builder initialize(Context context) {
        builder = new Builder(context);
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
            values.put(SensorDataContract.MessageData.TIMESTAMP, System.currentTimeMillis());
            values.put(SensorDataContract.MessageData.MESSAGE, "login");
            builder.mContext.getContentResolver().insert(SensorDataContract.MessageData.CONTENT_URI,
                    values);
        }
        // start syncing using the sync adapter
        ContentValues values = new ContentValues();
        values.put(SensorDataContract.UserData.TIMESTAMP, System.currentTimeMillis());
        values.put(SensorDataContract.UserData.NAME, builder.deviceId);
        values.put(SensorDataContract.UserData.EMAIL, builder.userId);
        String selectionArgs[] = { String.valueOf(System.currentTimeMillis()) };
        // if there is a previous entry of the user delete it.
        builder.mContext.getContentResolver().delete(SensorDataContract.UserData.CONTENT_URI, SensorDataContract.UserData.TIMESTAMP + " <= ?", selectionArgs);
        // insert new entry of user.
        builder.mContext.getContentResolver().insert(SensorDataContract.UserData.CONTENT_URI, values);

        SyncUtils.CreateSyncAccount(builder.mContext);
        SyncAdapter.performSync();

        // Start the service
        Intent i = new Intent(builder.mContext, SensorUpdateService.class);
        builder.mContext.startService(i);
    }


    public static void endService() {
        Log.d("SensorApp", "endService");

        // send message of logout(endservice)
        ContentValues values = new ContentValues();
        values.put(SensorDataContract.MessageData.TIMESTAMP, System.currentTimeMillis());
        values.put(SensorDataContract.MessageData.MESSAGE, "logout");
        builder.mContext.getContentResolver().insert(SensorDataContract.MessageData.CONTENT_URI,
                values);

        // force sync when the user logs out
        SyncAdapter.performSync();

        // delete the user id
        String selectionArgs[] = { String.valueOf(System.currentTimeMillis()) };
        builder.mContext.getContentResolver().delete(SensorDataContract.UserData.CONTENT_URI,
                SensorDataContract.UserData.TIMESTAMP + " <= ?", selectionArgs);

        // stop the service
        Intent i = new Intent(builder.mContext, SensorUpdateService.class);
        builder.mContext.stopService(i);

        SyncUtils.stopSync(builder.mContext);       // TODO confirm this ?
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
