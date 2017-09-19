package com.curefit.sensorsdk;

import android.content.Context;
import android.content.Intent;

import com.curefit.sensorsdk.db.DataStoreHelper;
import com.curefit.sensorsdk.services.SensorUpdateService;
import com.curefit.sensorsdk.sync.SyncAdapter;
import com.curefit.sensorsdk.sync.SyncUtils;

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

    public static void startService() {
        // start syncing using the sync adapter
        dsh.addUser(builder.deviceId, builder.userId);
        SyncUtils.CreateSyncAccount(builder.mContext);
        SyncAdapter.performSync();

        // Start the service
        Intent i = new Intent(builder.mContext, SensorUpdateService.class);
        builder.mContext.startService(i);
    }

    public static void endService() {
        Intent i = new Intent(builder.mContext, SensorUpdateService.class);
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
