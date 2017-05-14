package com.test.digvijay.pulseratenotifier.timertask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.gson.Gson;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.account.PulseAccount;
import com.test.digvijay.pulseratenotifier.asynctasks.GetEmergencyNumbersAsyncTask;
import com.test.digvijay.pulseratenotifier.bluetooth.BluetoothHelper;
import com.test.digvijay.pulseratenotifier.notifications.NotificationUtil;
import com.test.digvijay.pulseratenotifier.response.PatientData;
import com.test.digvijay.pulseratenotifier.response.Response;
import com.test.digvijay.pulseratenotifier.response.ResponseWrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DangerCallback {

    private static final String TAG = "DangerCallback";
    private Context context;
    private DangerThread dangerThread;
    BluetoothHelper bluetoothHelper;

    public DangerCallback(Context context, DangerThread dangerThread, BluetoothHelper bluetoothHelper) {
        this.context = context;
        this.dangerThread = dangerThread;
        this.bluetoothHelper = bluetoothHelper;
    }

    public void proceed() {
        Log.d(TAG, "proceed: Emergency Measures");

        String email = new PulseAccount().getDataOfLoggedInUser(context, "email");
        if(email == null) {
            Log.d(TAG, "undertakeEmergencyMeasures: No user Logged in");
            return;
        }

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("email", email);

        GetEmergencyNumbersAsyncTask getEmergencyNumbersAsyncTask = new GetEmergencyNumbersAsyncTask(urlParams, new GetEmergencyNumbersAsyncTask.AsyncResponse(){

            private ProgressDialog progressDialog;

            @Override
            public void processFinish(String output) {

                if(output == null) {
                    Log.d(TAG, "processFinish: Cant connect to server");

                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("An error occurred");
                    alertDialog.setMessage("Can not connect to server.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    return;
                }

                Log.d(TAG, "processFinish: received emergency data " + output);

                Gson gson = new Gson();
                ResponseWrapper responseWrapper = gson.fromJson(output, ResponseWrapper.class);
                Response response = responseWrapper.getResponse();
                PatientData patientData = response.getData().getPatientData();

                String patientName = new PulseAccount().getDataOfLoggedInUser(context, "fullName");
                List<String> emergencyContacts = patientData.getEmergencyContacts();

//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 2);
                NotificationUtil notificationUtil = new NotificationUtil();

                for (String emergencyContact : emergencyContacts) {
                    notificationUtil.sendSMS(emergencyContact, patientName);
                    Log.d(TAG, "processFinish: sent sms to " + emergencyContact);
                }

                for (String emergencyContact : emergencyContacts) {
                    notificationUtil.call(context, emergencyContact);
                    Log.d(TAG, "processFinish: called " + emergencyContact);
                }
            }

            @Override
            public void showDialog() { }

            @Override
            public void dismissDialog() { }
        });

        OutputStream outputStream = null;
        try {
            outputStream = bluetoothHelper.getConnectThread().getBluetoothSocket().getOutputStream();
            outputStream.write("ON".getBytes());
            Log.d(TAG, "onClick: sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
        getEmergencyNumbersAsyncTask.execute();

    }

    public void stop() {
        dangerThread.setActive(false);
        Log.d(TAG, "stop: dangerThread.isActive() " + dangerThread.getActive());
    }
}
