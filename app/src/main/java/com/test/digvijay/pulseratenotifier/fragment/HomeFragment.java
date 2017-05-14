package com.test.digvijay.pulseratenotifier.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sevenheaven.iosswitch.ShSwitchView;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.account.PulseAccount;
import com.test.digvijay.pulseratenotifier.bluetooth.BluetoothHelper;
import com.test.digvijay.pulseratenotifier.constants.HealthConstants;
import com.test.digvijay.pulseratenotifier.constants.RoleConstants;
import com.test.digvijay.pulseratenotifier.dbutils.PulseRateDbUtils;
import com.test.digvijay.pulseratenotifier.timertask.CurrentValues;
import com.test.digvijay.pulseratenotifier.timertask.DangerCallback;
import com.test.digvijay.pulseratenotifier.timertask.DangerThread;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HomeFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

    private GraphView graph;
    private ShSwitchView pulseRateSwitchView;
    private TextView pulseRateTextView;
    private BluetoothAdapter bluetoothAdapter;

    private LineGraphSeries<DataPoint> series;

    private void initUI(View view) {

        View pulseView = view.findViewById(R.id.current_pulse_rate_layout);

        ((TextView)view.findViewById(R.id.buzzer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutputStream outputStream = null;
                try {
                    outputStream = bluetoothHelper.getConnectThread().getBluetoothSocket().getOutputStream();
                    outputStream.write("ON".getBytes());
                    Log.d(TAG, "onClick: sent");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        String role = null;
        role = new PulseAccount().getDataOfLoggedInUser(getContext(), "role");

        if(role != null && role.equals(RoleConstants.PATIENT)) {

            pulseView.setVisibility(View.VISIBLE);

            pulseRateSwitchView = (ShSwitchView) view.findViewById(R.id.pulse_rate_switch_view);
            pulseRateTextView = (TextView) view.findViewById(R.id.pulse_rate_text_view);

            Log.d(TAG, "initUI: calling helper...");

            graph = (GraphView) view.findViewById(R.id.live_graph);
            series = new LineGraphSeries<>();
            graph.addSeries(series);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(40);

            addPulseViewListeners(view);

        } else {
            pulseView.setVisibility(View.INVISIBLE);
        }
    }

    private void addPulseViewListeners(View view) {
        pulseRateSwitchView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                if(isOn) {
                    startRecordingPulseRate();
                } else {
                    stopRecordingPulseRate();
                }
            }
        });
    }

    public static BluetoothHelper bluetoothHelper;

    public static BluetoothHelper getBluetoothHelper() {
        return bluetoothHelper;
    }

    private void stopRecordingPulseRate() {
        if(bluetoothHelper != null) {
            bluetoothHelper.closeConnection();
        }
    }

    public void startRecordingPulseRate() {
        bluetoothHelper = new BluetoothHelper(getContext(), getActivity(), bluetoothDataHandler);
        bluetoothHelper.openConnection();
    }

    private double i = 5d;
    private void refreshGraph(Integer pulseRate) {
        series.appendData(new DataPoint(i, pulseRate), true, 40);
        i += 1d;
    }

    private Handler bluetoothDataHandler = new Handler() {
        private String deviceId;
        private Integer pulseRate;

        @Override
        public void handleMessage(Message msg) {
            String message = String.valueOf(msg.obj);
            String[] messageArray = message.split(":");

            if(messageArray[0].equals("id")) {
//                Log.d(TAG, "Received device Id : " + messageArray[1]);
                this.deviceId = messageArray[1];
            } else if(messageArray[0].equals("value")) {
//                Log.d(TAG, "Received pulse rate : " + messageArray[1]);
                pulseRateTextView.setText(messageArray[1]);
                this.pulseRate = Integer.valueOf(messageArray[1]);
            } else {
                Log.d(TAG, "handleMessage: Garbage " + message);
            }

            if(deviceId == null) {
                pulseRate = null;
            } else if(pulseRate != null) {

                Log.d(TAG, "handleMessage: " + this.deviceId + " " + this.pulseRate);

                refreshGraph(pulseRate);

                CurrentValues.setCurrentPulseRate(pulseRate);
                checkForAlert(pulseRate);

                long newRowId = new PulseRateDbUtils(getContext()).savePulseRate(deviceId, pulseRate);

//                Log.d(TAG, "onCreate: added to sqlite database " + deviceId + " " + pulseRate);
                Log.d(TAG, "onCreate: added to sqlite database new row id " + newRowId);
            }
        }
    };

    private DangerThread dangerThread;

    private void checkForAlert(Integer pulseRate) {

        if(pulseRate < HealthConstants.getNormalRestingHeartRateMinimum() || pulseRate > HealthConstants.getNormalRestingHeartRateMaximum()) {
//            Log.d(TAG, "checkForAlert: Its danger");
            if(dangerThread == null) {
//                Log.d(TAG, "checkForAlert: Creating DangerThread");
                dangerThread = new DangerThread(new DangerCallback(getContext(), dangerThread, bluetoothHelper));
                dangerThread.start();
            }
            if(!dangerThread.isAlive()) {
//                Log.d(TAG, "checkForAlert: Setting Active DangerThread");
                dangerThread.setActive(true);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
