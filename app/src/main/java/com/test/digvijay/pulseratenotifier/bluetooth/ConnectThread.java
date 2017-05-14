package com.test.digvijay.pulseratenotifier.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by digvijay on 28/3/17.
 */

public class ConnectThread extends Thread {

    private static final String TAG = "ConnectThread";

    private static final UUID HC05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ReceiveDataFromHC05Service.CommunicationThread communicationThread;

    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;

    private Handler bluetoothDataHandler;

    Context context;

    public ConnectThread(BluetoothDevice bluetoothDevice, Handler bluetoothDataHandler, Context context) {
        this.bluetoothDataHandler = bluetoothDataHandler;
        this.bluetoothDevice = bluetoothDevice;
        this.context = context;

        BluetoothSocket tempBluetoothSocket = null;
        try {
            tempBluetoothSocket = this.bluetoothDevice.createInsecureRfcommSocketToServiceRecord(HC05_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "ConnectThread: " + tempBluetoothSocket.toString());
        this.bluetoothSocket = tempBluetoothSocket;
    }

    @Override
    public void run() {
        try {

            bluetoothSocket.connect();

            communicationThread = new ReceiveDataFromHC05Service(bluetoothDataHandler, context).new CommunicationThread(bluetoothSocket);
            communicationThread.start();

        } catch (IOException e) {
            Log.i(TAG, "Error Connecting module");
            e.printStackTrace();
        }
        Log.i(TAG, "Connection created successfully");
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public ReceiveDataFromHC05Service.CommunicationThread getCommunicationThread() {
        return communicationThread;
    }
}
