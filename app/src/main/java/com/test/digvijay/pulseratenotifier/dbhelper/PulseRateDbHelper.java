package com.test.digvijay.pulseratenotifier.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.test.digvijay.pulseratenotifier.database.PulseRateContract;

public class PulseRateDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 100;
    public static final String DATABASE_NAME = "PulseRate.sql";

    private static final String SQL_CREATE_ENTRIES =
            " CREATE TABLE " + PulseRateContract.PulseRateEntry.TABLE_NAME +
            " ( " +
                PulseRateContract.PulseRateEntry._ID + " INTEGER PRIMARY KEY, " +
                PulseRateContract.PulseRateEntry.COLUMN_NAME_DEVICE_ID + " TEXT, " +
                PulseRateContract.PulseRateEntry.COLUMN_NAME_PULSE_RATE + " INTEGER, " +
                PulseRateContract.PulseRateEntry.COLUMN_NAME_DATE_TIME + " INTEGER " +
            " ) ";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PulseRateContract.PulseRateEntry.TABLE_NAME;

    public PulseRateDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
