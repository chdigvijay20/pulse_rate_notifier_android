package com.test.digvijay.pulseratenotifier.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.account.PulseAccount;
import com.test.digvijay.pulseratenotifier.asynctasks.SavePatientDetailsAsyncDetails;
import com.test.digvijay.pulseratenotifier.response.PatientData;
import com.test.digvijay.pulseratenotifier.response.Response;
import com.test.digvijay.pulseratenotifier.response.ResponseWrapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PatientDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PatientDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PatientDetailsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PatientDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientDetailsFragment newInstance(String param1, String param2) {
        PatientDetailsFragment fragment = new PatientDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_patient_details, container, false);

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


    private EditText deviceIdEditText;
    private EditText dateOfBirthEditText;
    private RadioButton radioButtonMale;
    private Button saveButton;

    private String deviceId;
    private Long dateOfBirth;
    private String gender;

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    private void initUI(View view) {

        deviceIdEditText =(EditText) view.findViewById(R.id.device_id_edit_text);
        dateOfBirthEditText = (EditText) view.findViewById(R.id.birthdate_edit_text);
        radioButtonMale = (RadioButton) view.findViewById(R.id.radio_button_male);
        saveButton = (Button) view.findViewById(R.id.save_button);

        addListeners();

        final Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void addListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDataValid()){
                    Log.e(TAG,"Invalid entries");
                    return;
                }

                Account loggedInAccount = new PulseAccount().getLoggedInAccount(getContext());
                String email = AccountManager.get(getContext()).getUserData(loggedInAccount, "email");

                Map urlParams = new HashMap();
                urlParams.put("email", email);
                urlParams.put("deviceId", deviceId);
                urlParams.put("dateOfBirth", String.valueOf(dateOfBirth));
                urlParams.put("gender", gender);

                SavePatientDetailsAsyncDetails asyncTask = new SavePatientDetailsAsyncDetails(urlParams, new SavePatientDetailsAsyncDetails.AsyncResponse() {

                    ProgressDialog progressDialog;

                    @Override
                    public void processFinish(String output) {
                        if(output == null) {
                            Log.d(TAG, "processFinish: Cant connect to server");

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

                        PatientData patientData = response.getData().getPatientData();

                        Account loggedInAccount = new PulseAccount().getLoggedInAccount(getContext());
                        AccountManager.get(getContext()).setUserData(loggedInAccount, "deviceId", patientData.getDeviceId());
                        AccountManager.get(getContext()).setUserData(loggedInAccount, "dateOfBirth", String.valueOf(patientData.getDateOfBirth()));
                        AccountManager.get(getContext()).setUserData(loggedInAccount, "gender", patientData.getGender());

                        Log.d(TAG, "processFinish: added patient details to account bundle");

                        Fragment homeFragment = new HomeFragment();
                        FragmentManager fragmentManager = getFragmentManager();

                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.flContent, homeFragment);
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void showDialog() {
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Saving details...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                    }

                    @Override
                    public void dismissDialog() {
                        progressDialog.dismiss();
                    }
                });

                asyncTask.execute();
            }
        });

        dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerFragment = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedYear = year;
                        selectedMonth = monthOfYear;
                        selectedDay = dayOfMonth;

                        dateOfBirthEditText.setText(new StringBuilder().append(selectedDay).append("/").append(selectedMonth+1).append("/").append(selectedYear));
                    }
                }, selectedYear, selectedMonth, selectedDay);
                datePickerFragment.show();
            }
        });
    }

    private boolean isDataValid() {
        boolean valid = true;

        deviceId = deviceIdEditText.getText().toString();
        String tempDate = dateOfBirthEditText.getText().toString();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            dateOfBirth = formatter.parse(tempDate).getTime();
        } catch (ParseException e) {
            Log.d(TAG, "isDataValid: " + e.getStackTrace());
        }

        gender = radioButtonMale.isChecked() ? "Male" : "Female";

        if (deviceId == null || "".equals(deviceId)){
            deviceIdEditText.setError("Enter Device Id");
            valid = false;
        }

        if (dateOfBirth == null || "".equals(dateOfBirth)){
            dateOfBirthEditText.setError("Select Date of Birth");
            valid = false;
        }

        return valid;
    }
}
