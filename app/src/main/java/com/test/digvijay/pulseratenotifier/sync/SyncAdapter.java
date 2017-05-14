package com.test.digvijay.pulseratenotifier.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.test.digvijay.pulseratenotifier.constants.CharsetsConstants;
import com.test.digvijay.pulseratenotifier.constants.ServerConstants;
import com.test.digvijay.pulseratenotifier.dbutils.PulseRateDbUtils;
import com.test.digvijay.pulseratenotifier.entity.PulseRateEntity;
import com.test.digvijay.pulseratenotifier.response.PulseRateData;
import com.test.digvijay.pulseratenotifier.response.Response;
import com.test.digvijay.pulseratenotifier.util.ServerCommunicationUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = AbstractThreadedSyncAdapter.class.getCanonicalName();
    ContentResolver contentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.d(TAG, "SyncAdapter: ");
        contentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync: syncing now...");

        PulseRateDbUtils pulseRateDbUtils = new PulseRateDbUtils(getContext());
        ArrayList<PulseRateEntity> pulseRateEntities = pulseRateDbUtils.readPulseRates();

//        PulseRateDataMultiple pulseRateDataMultiple = new PulseRateDataMultiple();
//        for (PulseRateEntity pulseRateEntity : pulseRateEntities) {
//            PulseRateData pulseRateData = new PulseRateData();
//            pulseRateData.setDeviceId(pulseRateEntity.getDeviceId());
//            pulseRateData.setPulseRateCount(pulseRateEntity.getPulseRate());
//            pulseRateData.setPulseRateLogTime(pulseRateEntity.getDateTime());
//
//            Log.d(TAG, "onPerformSync: " + pulseRateData.getDeviceId() + " " + pulseRateData.getPulseRateCount() + " " + pulseRateData.getPulseRateLogTime());
//
//            pulseRateDataMultiple.add(pulseRateData);
//        }

        ArrayList<PulseRateData> pulseRateDataList = new ArrayList<>();
        for (PulseRateEntity pulseRateEntity : pulseRateEntities) {
            PulseRateData pulseRateData = new PulseRateData();
            pulseRateData.setDeviceId(pulseRateEntity.getDeviceId());
            pulseRateData.setPulseRateCount(pulseRateEntity.getPulseRate());
            pulseRateData.setPulseRateLogTime(pulseRateEntity.getDateTime());
            Log.d(TAG, "onPerformSync: " + pulseRateEntity.getDeviceId() + " " + pulseRateEntity.getPulseRate() + " " + pulseRateEntity.getDateTime());

            pulseRateDataList.add(pulseRateData);
        }

        Response response = new Response();
//        response.setPulseRateDataMultiple(pulseRateDataMultiple);
        response.setPulseRateDataList(pulseRateDataList);

        Gson gson = new Gson();
        String pulseRateDataJsonString = gson.toJson(response);
        Log.d(TAG, "onPerformSync: sent " + pulseRateDataJsonString);

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("pulseRateDataJsonString", pulseRateDataJsonString);

        try {
            String postData = new ServerCommunicationUtil().getPostDataString(urlParams);
            byte[] postDataBytes = postData.getBytes(CharsetsConstants.UTF8);

            URL url = new URL(ServerConstants.LOCAL_URL + "/savepulseratedata");
            HttpURLConnection connection = new ServerCommunicationUtil().getConnectionToPost(url, postDataBytes);

            connection.getOutputStream().write(postDataBytes);
            String serverResponse = new ServerCommunicationUtil().readResponse(connection);

            Log.d(TAG, "onPerformSync: response " + serverResponse);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
