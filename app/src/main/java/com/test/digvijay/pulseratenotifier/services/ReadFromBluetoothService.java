package com.test.digvijay.pulseratenotifier.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by digvijay on 13/3/17.
 */

public class ReadFromBluetoothService extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
