package com.test.digvijay.pulseratenotifier.fragment;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.constants.RequestCodeConstants;
import com.test.digvijay.pulseratenotifier.database.PulseRateContract;
import com.test.digvijay.pulseratenotifier.dbhelper.PulseRateDbHelper;
import com.test.digvijay.pulseratenotifier.bluetooth.ReceiveDataFromHC05Service;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PulseRateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PulseRateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PulseRateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String LOG_TAG = PulseRateFragment.class.toString();

    private static final UUID HC05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String HC05_MAC_ADDRESS = "00:21:13:00:73:F7";
    private BluetoothAdapter bluetoothAdapter;

    public PulseRateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PulseRateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PulseRateFragment newInstance(String param1, String param2) {
        PulseRateFragment fragment = new PulseRateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pulse_rate, container, false);

        initUI(view);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        if(bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
        super.onDestroy();
    }


    private TextView pulseRateTextView;
    private PulseRateDbHelper pulseRateDbHelper;
    private SQLiteDatabase sqLiteDatabase;

    private void initUI(View view) {
        pulseRateTextView = (TextView) view.findViewById(R.id.pulse_rate_text_view_old);

        pulseRateDbHelper = new PulseRateDbHelper(getContext());
        sqLiteDatabase = pulseRateDbHelper.getWritableDatabase();

        startBluetooth(view);
    }

    public void startBluetooth(View view) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            Log.d(LOG_TAG, "Bluetooth not supported");
            return;
        }

        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, RequestCodeConstants.REQUEST_ENABLE_BT_REQUEST_CODE);
        } else {
            connectToDevice();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodeConstants.REQUEST_ENABLE_BT_REQUEST_CODE :
                if(resultCode == Activity.RESULT_OK) {
                    connectToDevice();
                }
                break;
        }
    }

    public void connectToDevice() {
        BluetoothDevice HC05BluetoothDevice = bluetoothAdapter.getRemoteDevice(HC05_MAC_ADDRESS);

        if(HC05BluetoothDevice != null) {
            Log.i(LOG_TAG, "Found BT module " + HC05BluetoothDevice.getName() + " " + HC05BluetoothDevice.getAddress());
            ConnectThread connectThread = new ConnectThread(HC05BluetoothDevice);
            if(connectThread != null && connectThread.bluetoothSocket != null) {
                Log.i(LOG_TAG, "Could createRfcommSocketToServiceRecord...");
                connectThread.start();
            } else {
                Log.i(LOG_TAG, "Could not createRfcommSocketToServiceRecord");
            }
        } else {
            Log.i(LOG_TAG, "Could not find BT module");
        }
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;

            BluetoothSocket tempBluetoothSocket = null;
            try {
                tempBluetoothSocket = this.bluetoothDevice.createInsecureRfcommSocketToServiceRecord(HC05_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(LOG_TAG, tempBluetoothSocket.toString());
            this.bluetoothSocket = tempBluetoothSocket;
        }

        @Override
        public void run() {
            try {
                bluetoothSocket.connect();

                ReceiveDataFromHC05Service.CommunicationThread connectedThread = new ReceiveDataFromHC05Service(bluetoothDataHandler, getContext()).new CommunicationThread(bluetoothSocket);
                connectedThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(LOG_TAG, "Connection created successfully");
        }
    }

    private Handler bluetoothDataHandler = new Handler() {
        private String deviceId;
        private Integer pulseRate;

        @Override
        public void handleMessage(Message msg) {
            String message = String.valueOf(msg.obj);
            String[] messageArray = message.split(":");

            if(messageArray[0].equals("id")) {
                Log.d(LOG_TAG, "Received device Id : " + messageArray[1]);
                this.deviceId = messageArray[1];
            } else if(messageArray[0].equals("value")) {
                Log.d(LOG_TAG, "Received pulse rate : " + messageArray[1]);
                pulseRateTextView.setText(messageArray[1]);
                this.pulseRate = Integer.valueOf(messageArray[1]);
            }

            if(deviceId == null) {
                pulseRate = null;
            } else if(pulseRate != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PulseRateContract.PulseRateEntry.COLUMN_NAME_DEVICE_ID, deviceId);
                contentValues.put(PulseRateContract.PulseRateEntry.COLUMN_NAME_PULSE_RATE, pulseRate);
                contentValues.put(PulseRateContract.PulseRateEntry.COLUMN_NAME_DATE_TIME, String.valueOf(new Date()));

                long newRowId = sqLiteDatabase.insert(PulseRateContract.PulseRateEntry.TABLE_NAME, null, contentValues);
                Log.d(LOG_TAG, "onCreate: added to sqlite database " + deviceId + " " + pulseRate);
                Log.d(LOG_TAG, "onCreate: new row id " + newRowId);
            }
        }
    };
}
