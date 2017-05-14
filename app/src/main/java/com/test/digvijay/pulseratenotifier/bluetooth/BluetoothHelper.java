package com.test.digvijay.pulseratenotifier.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.test.digvijay.pulseratenotifier.constants.RequestCodeConstants;

public class BluetoothHelper {

    private static final String TAG = "BluetoothHelper";

    private Context context;

    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;

    private static final String HC05_MAC_ADDRESS = "00:21:13:00:73:F7";
    private Handler bluetoothDataHandler;
    private  Activity activity;

    public BluetoothHelper(Context context, Activity activity, Handler bluetoothDataHandler) {
        this.context = context;
        this.bluetoothDataHandler = bluetoothDataHandler;
        this.activity = activity;
    }

    public ConnectThread getConnectThread() {
        return connectThread;
    }

    public void connectToDevice() {
        Log.d(TAG, "connectToDevice: ");
        BluetoothDevice HC05BluetoothDevice = bluetoothAdapter.getRemoteDevice(HC05_MAC_ADDRESS);

        if(HC05BluetoothDevice != null) {
            Log.i(TAG, "Found BT module " + HC05BluetoothDevice.getName() + " " + HC05BluetoothDevice.getAddress());
            connectThread = new ConnectThread(HC05BluetoothDevice, bluetoothDataHandler, context);

            if(connectThread != null && connectThread.getBluetoothSocket() != null) {
                Log.i(TAG, "Could createRfcommSocketToServiceRecord... Connecting now...");
                connectThread.start();
            } else {
                Log.i(TAG, "Could not createRfcommSocketToServiceRecord");
            }
        } else {
            Log.i(TAG, "Could not find BT module");
        }
    }

    public void closeConnection() {

        if(connectThread != null) {
            ReceiveDataFromHC05Service.CommunicationThread communicationThread = connectThread.getCommunicationThread();
            if(communicationThread != null) {
                communicationThread.cancel();
            }
        }

        if(bluetoothAdapter !=null) {
            bluetoothAdapter.disable();
        }
    }

    public void openConnection() {
        Log.d(TAG, "openConnection: ");
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            Log.d(TAG, "Bluetooth not supported");
            return;
        }

        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (context instanceof Activity) {
                activity.startActivityForResult(enableBluetoothIntent, RequestCodeConstants.REQUEST_ENABLE_BT_REQUEST_CODE);
            } else {
                Log.d(TAG, "openConnection: context is not instance of Activity");
            }
        } else {
            connectToDevice();
        }
    }
}
