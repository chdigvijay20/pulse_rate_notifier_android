package com.test.digvijay.pulseratenotifier.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by digvijay on 18/2/17.
 */

public class PreferenceManager {

    private static final String TAG = "PreferenceManager";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    private static final String PREFERENCE_NAME = "pulse-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String FIREBASE_TOKEN = "FirebaseToken";

    private static final String NORMAL_MINIMUM_RESTING_HEART_RATE = "NormalMinimumRestingHeartRate";

    private static final String NORMAL_MAXIMUM_RESTING_HEART_RATE = "NormalMaximumRestingHeartRate";

    public PreferenceManager(Context context) {
        this.context = context;
        Log.d(TAG, "PreferenceManager: " + context);
        sharedPreferences = this.context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTimeLaunch) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirebaseToken(String firebaseToken) {
        editor.putString(FIREBASE_TOKEN, firebaseToken);
        editor.commit();
    }

    public String getFirebaseToken() {
        return sharedPreferences.getString(FIREBASE_TOKEN, null);
    }

    public void setNormalMinimumRestingHeartRate(Integer normalMinimumRestingHeartRate) {
        editor.putInt(NORMAL_MINIMUM_RESTING_HEART_RATE, normalMinimumRestingHeartRate);
        editor.commit();
    }

    public Integer getNormalMinimumRestingHeartRate() {
        return sharedPreferences.getInt(NORMAL_MINIMUM_RESTING_HEART_RATE, 60);
    }

    public void setNormalMaximumRestingHeartRate(Integer normalMaximumRestingHeartRate) {
        editor.putInt(NORMAL_MAXIMUM_RESTING_HEART_RATE, normalMaximumRestingHeartRate);
        editor.commit();
    }

    public Integer getNormalMaximumRestingHeartRate() {
        return sharedPreferences.getInt(NORMAL_MAXIMUM_RESTING_HEART_RATE, 100);
    }
}
