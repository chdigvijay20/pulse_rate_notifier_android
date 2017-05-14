package com.test.digvijay.pulseratenotifier.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AuthenticatorService extends Service {

    private static final String TAG = "AuthenticatorService";
    private Authenticator authenticator;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        authenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
