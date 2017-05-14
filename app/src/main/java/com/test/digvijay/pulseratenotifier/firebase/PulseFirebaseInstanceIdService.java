package com.test.digvijay.pulseratenotifier.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.test.digvijay.pulseratenotifier.util.PreferenceManager;

/**
 * Created by digvijay on 20/3/17.
 */

public class PulseFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "PulseFirebaseInService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.setFirebaseToken(refreshedToken);
    }
}
