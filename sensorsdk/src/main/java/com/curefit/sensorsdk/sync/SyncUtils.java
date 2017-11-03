package com.curefit.sensorsdk.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.curefit.sensorsdk.R;

/**
 * Created by rahul on 23/08/17.
 */

public class SyncUtils {
    private static final long SYNC_FREQUENCY = 60*30;
    private static final String PREF_SETUP_COMPLETE = "setup_complete";
    public static final String ACCOUNT = "SensorApp";       // TODO update this name accordingly
    public static String ACCOUNT_TYPE = null;

    public static void CreateSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);
        ACCOUNT_TYPE = context.getResources().getString(R.string.account_type);
        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        boolean flag= accountManager.addAccountExplicitly(account, null, null);

        if (flag) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            Log.d("SensorApp", "appPeriodicSync will be called");
            ContentResolver.setIsSyncable(account, SensorDataContract.CONTENT_AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, SensorDataContract.CONTENT_AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, SensorDataContract.CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);
            newAccount = true;
        }
        else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Log.d("SensorApp", "Sync Account Error");
        }
        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            TriggerRefresh();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        }
    }

    /**
     * Helper method to trigger an immediate sync ("refresh").
     *
     * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     *
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        Account account = new Account(ACCOUNT, ACCOUNT_TYPE);

        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                account,      // Sync account
                SensorDataContract.CONTENT_AUTHORITY, // Content authority
                b); // Extras
    }

    /**
     * this function is used to stop syncing process.
     * @param context
     */
    public static void stopSync(Context context) {
        Account account = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        accountManager.removeAccountExplicitly(account);
        ContentResolver.setIsSyncable(account, SensorDataContract.CONTENT_AUTHORITY, 0);
    }
}