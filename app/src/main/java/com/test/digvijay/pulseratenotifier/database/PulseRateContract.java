package com.test.digvijay.pulseratenotifier.database;

import android.provider.BaseColumns;

public final class PulseRateContract {

    public static class PulseRateEntry implements BaseColumns {
        public static final String TABLE_NAME = "pulse";
        public static final String COLUMN_NAME_DEVICE_ID = "device_id";
        public static final String COLUMN_NAME_PULSE_RATE = "pulse_rate";
        public static final String COLUMN_NAME_DATE_TIME = "date_time";
    }
}
