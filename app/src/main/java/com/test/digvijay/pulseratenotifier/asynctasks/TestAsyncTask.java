package com.test.digvijay.pulseratenotifier.asynctasks;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.test.digvijay.pulseratenotifier.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class TestAsyncTask extends AsyncTask {

    private NotificationManager notificationManager;
    private int syncingNotificationId = 7979;

    private Context context;

    public TestAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_sync_black_24dp)
                .setContentTitle("Testing")
                .setContentText("Testing notification ongoing...")
                .setOngoing(true);

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(syncingNotificationId, builder.build());
    }

    @Override
    protected Object doInBackground(Object[] params) {
        while (true) {

        }
    }

    @Override
    protected void onPostExecute(Object o) {
        notificationManager.cancel(syncingNotificationId);
    }
}
