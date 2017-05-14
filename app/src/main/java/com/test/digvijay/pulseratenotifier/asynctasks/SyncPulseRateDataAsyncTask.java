package com.test.digvijay.pulseratenotifier.asynctasks;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.test.digvijay.pulseratenotifier.R;
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
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by digvijay on 22/3/17.
 */

public class SyncPulseRateDataAsyncTask extends AsyncTask<String, String, String> {

    private static final String TAG = "SavePulseRateAsyncTask";
    private Context context;
    private Long pulseRateEntriesCount;

    private NotificationManager notificationManager;
    private int syncingNotificationId = 8787;
    private int syncedNotificationId = 8989;

    private String response;

    public SyncPulseRateDataAsyncTask(Context context, Long pulseRateEntriesCount) {
        this.context = context;
        this.pulseRateEntriesCount = pulseRateEntriesCount;
    }

    @Override
    protected void onPreExecute() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_sync_black_24dp)
                .setContentTitle("Syncing")
                .setContentText("Syncing pulse rate data with server")
                .setOngoing(true);

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(syncingNotificationId, builder.build());
    }

    @Override
    protected String doInBackground(String... params) {

        String serverResponse = null;

        PulseRateDbUtils pulseRateDbUtils = new PulseRateDbUtils(context);
        ArrayList<PulseRateEntity> pulseRateEntities = pulseRateDbUtils.readPulseRates();
        List<Integer> ids = null;
        while (pulseRateEntities.size() > 0) {

            ids = new ArrayList<>();
            ArrayList<PulseRateData> pulseRateDataList = new ArrayList<>();
            for (PulseRateEntity pulseRateEntity : pulseRateEntities) {
                PulseRateData pulseRateData = new PulseRateData();
                pulseRateData.setDeviceId(pulseRateEntity.getDeviceId());
                                                                                                                                                                                                                                                                 pulseRateData.setPulseRateCount(pulseRateEntity.getPulseRate());
                pulseRateData.setPulseRateLogTime(pulseRateEntity.getDateTime());
                Log.d(TAG, "onPerformSync: " + pulseRateEntity.getDeviceId() + " " + pulseRateEntity.getPulseRate() + " " + pulseRateEntity.getDateTime());

                pulseRateDataList.add(pulseRateData);
                ids.add(pulseRateEntity.getId());
            }

            Response response = new Response();
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
                serverResponse = new ServerCommunicationUtil().readResponse(connection);

                Log.d(TAG, "onPerformSync: response " + serverResponse);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(serverResponse != null) {
                Integer[] idsArray = new Integer[ids.size()];
                pulseRateDbUtils.deletePulseRates(ids.toArray(idsArray));
            } else {
                break;
            }

            pulseRateEntities = pulseRateDbUtils.readPulseRates();
        }

        return serverResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: " + result);
        notificationManager.cancel(syncingNotificationId);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.cardiogram)
                .setContentTitle("Sync successful")
                .setContentText("The data has been synced successfully")
                .setAutoCancel(true);

        notificationManager.notify(syncedNotificationId, builder.build());
    }
}