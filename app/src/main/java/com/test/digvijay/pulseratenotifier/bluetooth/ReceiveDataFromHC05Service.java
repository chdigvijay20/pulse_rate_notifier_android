package com.test.digvijay.pulseratenotifier.bluetooth;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.constants.MessageConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReceiveDataFromHC05Service extends Service {

    private static final String LOG_TAG = ReceiveDataFromHC05Service.class.toString();
    private Handler dataReceptionHandler;

    private NotificationManager notificationManager;
    private Context context;
    private int bluetoothNotificationId = 12345;
    private String currentPulseRate = "00";
    private NotificationCompat.Builder builder;

    public ReceiveDataFromHC05Service(Handler dataReceptionHandler, Context context) {
        this.dataReceptionHandler = dataReceptionHandler;
        this.context = context;
    }

    public class CommunicationThread extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public CommunicationThread(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            InputStream tempInputStream = null;
            OutputStream tempOutputStream = null;

            try {
                tempInputStream = bluetoothSocket.getInputStream();
                tempOutputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempInputStream;
            outputStream = tempOutputStream;
        }

        @Override
        public void run() {

            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_sync_black_24dp)
                    .setContentTitle("Connected")
                    .setContentText("Your current pulse rate is " + currentPulseRate)
                    .setOngoing(true);

            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(bluetoothNotificationId, builder.build());

            while(true) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();

                    int charRead = inputStream.read();
                    while ((char)charRead != ';') {
                        stringBuilder.append((char)charRead);
                        charRead = inputStream.read();
                    }

                    String receivedText = stringBuilder.toString();

                    String[] messageArray = receivedText.split(":");

                    if(messageArray[0].equals("value")) {
                        currentPulseRate = messageArray[1];
                        builder.setContentText("Your current pulse rate is " + currentPulseRate);
                        notificationManager.notify(bluetoothNotificationId, builder.build());
                    }

                    Message readMessage = dataReceptionHandler.obtainMessage(MessageConstants.MESSAGE_READ, receivedText);
                    readMessage.sendToTarget();

                    Log.d(LOG_TAG, "Received Something... : " + receivedText);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
