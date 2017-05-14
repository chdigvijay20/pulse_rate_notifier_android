package com.test.digvijay.pulseratenotifier.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.account.PulseAccount;
import com.test.digvijay.pulseratenotifier.activities.ReportActivity;
import com.test.digvijay.pulseratenotifier.asynctasks.GetPatientListToObserve;
import com.test.digvijay.pulseratenotifier.response.DoctorData;
import com.test.digvijay.pulseratenotifier.response.ResponseWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PatientListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PatientListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PatientListFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PatientListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientListFragment newInstance(String param1, String param2) {
        PatientListFragment fragment = new PatientListFragment();
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

        final View view = inflater.inflate(R.layout.fragment_patient_list, container, false);

        Map urlParams = new HashMap();
        urlParams.put("email", new PulseAccount().getDataOfLoggedInUser(getContext(), "email"));

        GetPatientListToObserve getPatientListToObserve = new GetPatientListToObserve(urlParams, new GetPatientListToObserve.AsyncResponse() {
            ProgressDialog progressDialog;

            @Override
            public void processFinish(String output) {

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

                ListView listView = (ListView) view.findViewById(R.id.patients_list_view);

                Gson gson = new Gson();
                ResponseWrapper responseWrapper = gson.fromJson(output, ResponseWrapper.class);
                DoctorData doctorData = responseWrapper.getResponse().getData().getDoctorData();
                final List<String> patientsNameList = doctorData.getPatientsNameList();
                final List<String> patientsEmailList = doctorData.getPatientsEmailList();

//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_2, patientsNameList);

                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, patientsNameList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        text1.setText(patientsNameList.get(position));
                        text2.setText(patientsEmailList.get(position));
                        return view;
                    }
                };

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getContext(), ((TextView)view.findViewById(android.R.id.text1)).getText() + " " + ((TextView)view.findViewById(android.R.id.text2)).getText(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getContext(), ReportActivity.class);
                        intent.putExtra("email", ((TextView)view.findViewById(android.R.id.text2)).getText());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void showDialog() {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Getting patients...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

            @Override
            public void dismissDialog() {
                progressDialog.dismiss();
            }
        });

        getPatientListToObserve.execute();

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
}
