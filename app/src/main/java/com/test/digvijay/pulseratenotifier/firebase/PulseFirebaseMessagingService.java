package com.test.digvijay.pulseratenotifier.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.activities.MainActivity;
import com.test.digvijay.pulseratenotifier.notifications.NotificationUtil;

/**
 * Created by digvijay on 20/3/17.
 */

public class PulseFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "PulseFirebaseMsgService";
    private int notificationId = 1729;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent detailsIntent = new Intent(this, MainActivity.class)
                                .putExtra("fragment", "reportFragment");
        PendingIntent detailsPendingIntent = PendingIntent.getActivity(this, 0, detailsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action detailsAction = new NotificationCompat.Action.Builder(0                                                                   , "Details", detailsPendingIntent).build();

        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("contactNumber"));
        Intent callingIntent = NotificationUtil.getCallingIntent(remoteMessage.getData().get("contactNumber"));
        PendingIntent callingPendingIntent = PendingIntent.getActivity(this, 0, callingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action callingAction = new NotificationCompat.Action.Builder(0, "Call", callingPendingIntent).build();

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.cardiogram)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .addAction(detailsAction)
                .addAction(callingAction);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());

        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }
    
}
