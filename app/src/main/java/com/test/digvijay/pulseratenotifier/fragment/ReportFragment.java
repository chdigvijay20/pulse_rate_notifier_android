package com.test.digvijay.pulseratenotifier.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.account.PulseAccount;
import com.test.digvijay.pulseratenotifier.asynctasks.GetPulseRateDataAsyncTask;
import com.test.digvijay.pulseratenotifier.response.Data;
import com.test.digvijay.pulseratenotifier.response.PulseRateData;
import com.test.digvijay.pulseratenotifier.response.PulseRateDataMultiple;
import com.test.digvijay.pulseratenotifier.response.Response;
import com.test.digvijay.pulseratenotifier.response.ResponseWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ReportFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String currentEmail;

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
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
        View view = inflater.inflate(R.layout.fragment_report, container, false);

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

    private GraphView reportGraph;
    private LineGraphSeries<DataPoint> series;
    private TextView averageBPMTextView;

    private Long averageBPM = Long.valueOf(0);

    private void initUI(View view) {

        reportGraph = (GraphView) view.findViewById(R.id.report_graph);
        series = new LineGraphSeries<>();
        series.setColor(Color.parseColor("#28b463"));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(8);
        series.setThickness(6);

        reportGraph.addSeries(series);

        reportGraph.getViewport().setScalable(true);
        reportGraph.getViewport().setScrollable(true);
        reportGraph.getViewport().setScalableY(true);
        reportGraph.getViewport().setScrollableY(true);


        reportGraph.getViewport().setXAxisBoundsManual(true);
        reportGraph.getViewport().setMinX(0);
        reportGraph.getViewport().setMaxX(10000);

        reportGraph.getViewport().setYAxisBoundsManual(true);
        reportGraph.getViewport().setMinY(0);
        reportGraph.getViewport().setMaxY(200);


        reportGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        reportGraph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space


        reportGraph.getGridLabelRenderer().setHumanRounding(false);

        averageBPMTextView = (TextView) view.findViewById(R.id.average_bpm_text_view);

        initList(view);

        showGraph();
    }

    private void initList(View view) {

        ListView listView = (ListView) view.findViewById(R.id.days_list_view);

        Calendar calendar = Calendar.getInstance();
        final ArrayList<String> dateList = new ArrayList<>();
        final ArrayList<String> dayList = new ArrayList<>();

        for (int i = 0; i < 6; ++i) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            dateList.add(new SimpleDateFormat("MM/dd/yyyy").format(calendar.getTime()));
            dayList.add(new SimpleDateFormat("EEEE").format(calendar.getTime()));
            Log.d(TAG, "initList: " + dateList.get(i) + " " + dayList.get(i));
//            Log.d(TAG, "initList: " + new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
        }

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, dayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(dateList.get(position));
                text2.setText(dayList.get(position));
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ReportFragment();

                Bundle bundle = new Bundle();
                bundle.putString("email", currentEmail);
                bundle.putString("date", (String) ((TextView) view.findViewById(android.R.id.text1)).getText());
                fragment.setArguments(bundle);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.flContent, fragment).commit();
            }
        });
    }

    private void showGraph() {
        Map<String, String> urlParams = new HashMap<>();

        Bundle bundle = this.getArguments();
        String email = null;
        String date = null;
        if (bundle != null) {
            email = bundle.getString("email");
            date = bundle.getString("date");
        }

        if(email != null) {
            currentEmail = email;
            urlParams.put("email", email);
        } else {
            urlParams.put("email", new PulseAccount().getDataOfLoggedInUser(getContext(), "email"));
        }

        if(date != null) {
            urlParams.put("date", date);
        }

        Log.d(TAG, "showGraph: URL params " + urlParams.get("email") + " " + urlParams.get("date"));

        GetPulseRateDataAsyncTask pulseRateDataAsyncTask = new GetPulseRateDataAsyncTask(urlParams, new GetPulseRateDataAsyncTask.AsyncResponse() {

            ProgressDialog progressDialog;

            @Override
            public void processFinish(String output) {
                Log.d(TAG, "processFinish: " + output);

                if(output == null) {
                    Log.d(TAG, "Cant connect to server.");

                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("An error occurred");
                    alertDialog.setMessage("Can not connect to server.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    return;
                }

                Gson gson = new Gson();
                ResponseWrapper responseWrapper = gson.fromJson(output, ResponseWrapper.class);
                Response response = responseWrapper.getResponse();

                Data data = response.getData();
                if(data != null) {
                    PulseRateDataMultiple pulseRateDataMultiple = data.getPulseRateDataMultiple();
                    List<PulseRateData> pulseRateDataList = pulseRateDataMultiple.getPulseRateDataList();

                    for (PulseRateData pulseRateData : pulseRateDataList) {
                        series.appendData(new DataPoint(pulseRateData.getPulseRateLogTime(), pulseRateData.getPulseRateCount()), true, 20000);
                        Log.d(TAG, "processFinish: " + pulseRateData.getPulseRateLogTime());
                    }

                    Long totalBPM = 0l;
                    for (PulseRateData pulseRateData : pulseRateDataList) {
                        totalBPM += pulseRateData.getPulseRateCount();
                    }
                    if(pulseRateDataList.size() != 0) {
                        averageBPM = totalBPM / pulseRateDataList.size();
                    }
                    averageBPMTextView.setText(String.valueOf(averageBPM));
                }

//                Collections.sort(pulseRateDataList, new Comparator<PulseRateData>(){
//                    public int compare(PulseRateData o1, PulseRateData o2){
//                        if(o1.getPulseRateLogTime() == o2.getPulseRateLogTime())
//                            return 0;
//                        return o1.getPulseRateLogTime() < o2.getPulseRateLogTime() ? -1 : 1;
//                    }
//                });

//                Long initialTime = pulseRateDataList.get(0).getPulseRateLogTime();

            }

            @Override
            public void showDialog() {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Receiving Data...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

            @Override
            public void dismissDialog() {
                progressDialog.dismiss();
            }
        });

        pulseRateDataAsyncTask.execute();
    }

//    public class AddDataPointThread extends Thread {
//
//        private List<PulseRateData> pulseRateDataList;
//
//        public AddDataPointThread(List<PulseRateData> pulseRateDataList) {
//            this.pulseRateDataList = pulseRateDataList;
//        }
//
//        @Override
//        public void run() {
//
//            Long initialTime = pulseRateDataList.get(0).getPulseRateLogTime();
//
//
//        }
//    }
}
