package com.test.digvijay.pulseratenotifier.dbutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.test.digvijay.pulseratenotifier.database.PulseRateContract;
import com.test.digvijay.pulseratenotifier.dbhelper.PulseRateDbHelper;
import com.test.digvijay.pulseratenotifier.entity.PulseRateEntity;

import java.util.ArrayList;
import java.util.Date;

public class PulseRateDbUtils {

    private SQLiteDatabase sqLiteDatabase;
    private PulseRateDbHelper pulseRateDbHelper;

    static final String LIMIT = "20";

    String[] projection = {
            PulseRateContract.PulseRateEntry._ID,
            PulseRateContract.PulseRateEntry.COLUMN_NAME_DEVICE_ID,
            PulseRateContract.PulseRateEntry.COLUMN_NAME_PULSE_RATE,
            PulseRateContract.PulseRateEntry.COLUMN_NAME_DATE_TIME
    };


    public PulseRateDbUtils(Context context) {
        pulseRateDbHelper = new PulseRateDbHelper(context);
    }

    public long savePulseRate(String deviceId, Integer pulseRate) {

        sqLiteDatabase = pulseRateDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PulseRateContract.PulseRateEntry.COLUMN_NAME_DEVICE_ID, deviceId);
        contentValues.put(PulseRateContract.PulseRateEntry.COLUMN_NAME_PULSE_RATE, pulseRate);
        contentValues.put(PulseRateContract.PulseRateEntry.COLUMN_NAME_DATE_TIME, (new Date()).getTime());

        return sqLiteDatabase.insert(PulseRateContract.PulseRateEntry.TABLE_NAME, null, contentValues);
    }

    public ArrayList<PulseRateEntity> readPulseRates() {

        sqLiteDatabase = pulseRateDbHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(PulseRateContract.PulseRateEntry.TABLE_NAME, projection, null, null, null, null, null, LIMIT);

        ArrayList<PulseRateEntity> pulseRateEntities = new ArrayList<>();
        while (cursor.moveToNext()) {
            PulseRateEntity pulseRateEntity = new PulseRateEntity();
            pulseRateEntity.setId(cursor.getInt(0));
            pulseRateEntity.setDeviceId(cursor.getString(1));
            pulseRateEntity.setPulseRate(cursor.getInt(2));
            pulseRateEntity.setDateTime(cursor.getLong(3));

            pulseRateEntities.add(pulseRateEntity);
        }

        return pulseRateEntities;
    }

    public void deletePulseRates(Integer[] ids) {
        sqLiteDatabase = pulseRateDbHelper.getWritableDatabase();

        String args = TextUtils.join(", ", ids);
        sqLiteDatabase.execSQL(String.format("DELETE FROM " + PulseRateContract.PulseRateEntry.TABLE_NAME + "  WHERE _id IN (%s);", args));
    }

    public long getPulseRatesCount() {
        sqLiteDatabase = pulseRateDbHelper.getReadableDatabase();
        return DatabaseUtils.longForQuery(sqLiteDatabase, "SELECT COUNT(*) FROM " + PulseRateContract.PulseRateEntry.TABLE_NAME, null);
    }
}
