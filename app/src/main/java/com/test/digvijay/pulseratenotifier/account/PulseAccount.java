package com.test.digvijay.pulseratenotifier.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import static android.R.attr.key;

public class PulseAccount {

    public final static String ACCOUNT_TYPE = "com.kit.pulse";
    private static final String TAG = PulseAccount.class.toString();

    private String name;
    private String password;
    private Bundle bundle;

    public PulseAccount() {

    }

    public PulseAccount(String name, String password, Bundle bundle) {
        this.name = name;
        this.password = password;
        this.bundle = bundle;
    }

    public void addAccount(Context context) {
        Account account = new Account(name, ACCOUNT_TYPE);

        AccountManager accountManager = AccountManager.get(context);
        boolean isAccountAdded = accountManager.addAccountExplicitly(account, password, bundle);

        if(isAccountAdded) {
            Log.d(TAG, "addAccount: added account");
            Log.d(TAG, "addAccount: present accounts --> ");
            Account[] accounts = accountManager.getAccounts();
            for (Account a : accounts) {
                Log.d(TAG, "addAccount: " + a.name);
            }
        } else {
            Log.d(TAG, "addAccount: error adding account");
        }
    }

    public Account getLoggedInAccount(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);

        if(accounts.length > 0) {
            return accounts[0];
        }
        return null;
    }

    public String getDataOfLoggedInUser(Context context, String key) {
        Account loggedInAccount = new PulseAccount().getLoggedInAccount(context);
        if(loggedInAccount != null) {
            return AccountManager.get(context).getUserData(loggedInAccount, key);
        }

        return null;
    }

    public void removeAccount(Context context) {
        Account account = getLoggedInAccount(context);
        AccountManager.get(context).removeAccount(account, null, null);
    }
}
