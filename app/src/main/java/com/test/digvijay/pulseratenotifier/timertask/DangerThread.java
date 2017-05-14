package com.test.digvijay.pulseratenotifier.timertask;

import android.util.Log;

import com.test.digvijay.pulseratenotifier.constants.HealthConstants;

/**
 * Created by digvijay on 27/3/17.
 */

public class DangerThread extends Thread {

    private static final String TAG = "DangerThread";

    private static Boolean active = false;

    private DangerCallback dangerCallback;
    private Integer normalMinimum;
    private Integer normalMaximum;

    public DangerThread(DangerCallback dangerCallback) {
        this.dangerCallback = dangerCallback;

        normalMinimum = HealthConstants.getNormalRestingHeartRateMinimum();
        normalMaximum = HealthConstants.getNormalRestingHeartRateMaximum();
    }

    @Override
    public void run() {
        Log.d(TAG, "run: ");

        boolean danger = true;
        for (int i =0; i < 10; ++i) {
            Integer currentPulseRate = CurrentValues.getCurrentPulseRate();
            if(isNormal(currentPulseRate)) {
                Log.d(TAG, "run: normal " + currentPulseRate);
                danger = false;
                break;
            }
            Log.d(TAG, "run: abnormal " + currentPulseRate);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(danger) {
            Log.d(TAG, "run: proceeding ");
            dangerCallback.proceed();
        } else {
            Log.d(TAG, "run: stopping ");
            dangerCallback.stop();
        }
    }

    private boolean isNormal(Integer currentPulseRate) {
        if(currentPulseRate < normalMinimum && currentPulseRate > normalMaximum) {
            return true;
        }
        return false;
    }

    public static Boolean getActive() {
        return active;
    }

    public static void setActive(Boolean active) {
        DangerThread.active = active;
    }
}