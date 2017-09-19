package com.curefit.sensorsdk.sync;

import android.accounts.Account;

/**
 * Created by rahul on 28/08/17.
 */

public final class AccountGeneral {
    /**
     * This is the type of account we are using. i.e. we can specify our app or apps
     * to have different types, such as 'read-only', 'sync-only', & 'admin'.
     */
    private static final String ACCOUNT_TYPE = SyncUtils.ACCOUNT_TYPE;

    /**
     * This is the name that appears in the Android 'Accounts' settings.
     */
    private static final String ACCOUNT_NAME = SyncUtils.ACCOUNT;


    /**
     * Gets the standard sync account for our app.
     * @return {@link Account}
     */
    public static Account getAccount() {
        return new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
    }
}