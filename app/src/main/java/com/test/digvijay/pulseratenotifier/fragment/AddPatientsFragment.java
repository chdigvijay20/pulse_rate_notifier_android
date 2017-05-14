package com.test.digvijay.pulseratenotifier.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.account.PulseAccount;
import com.test.digvijay.pulseratenotifier.asynctasks.AddPatientsAsyncTask;
import com.test.digvijay.pulseratenotifier.response.Response;
import com.test.digvijay.pulseratenotifier.response.ResponseConstants;
import com.test.digvijay.pulseratenotifier.response.ResponseWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddPatientsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddPatientsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPatientsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "AddPatientsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddPatientsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPatientsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPatientsFragment newInstance(String param1, String param2) {
        AddPatientsFragment fragment = new AddPatientsFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_patients, container, false);

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
     * >Communicating with Other Fragments</a> for more informaMtion.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private Button addPatientsButton;
    private LinearLayout addPatientsLinearLayout;
    private Button skipButton;
    private Button proceedButton;

    private void initUI(final View view) {
        addPatientsButton = (Button) view.findViewById(R.id.add_patients_button);
        addPatientsLinearLayout = (LinearLayout) view.findViewById(R.id.add_patients_linear_layout);
        skipButton = (Button) view.findViewById(R.id.add_patients_skip_button);
        proceedButton = (Button) view.findViewById(R.id.proceed_button);

        addPatientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View customEmailView = getActivity().getLayoutInflater().inflate(R.layout.custom_email, null);
                Button customEmailDeleteButton = (Button) customEmailView.findViewById(R.id.custom_email_delete_button);

                customEmailDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPatientsLinearLayout.removeView(customEmailView);
                    }
                });

                addPatientsLinearLayout.addView(customEmailView);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment homeFragment = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContent, homeFragment);
                fragmentTransaction.commit();
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            List<EditText> editTexts;

            @Override
            public void onClick(View v) {

                editTexts = new ArrayList<EditText>();
                int childCount = addPatientsLinearLayout.getChildCount();
                for (int i = 0; i < childCount; ++i) {
                    View linearView = addPatientsLinearLayout.getChildAt(i);
                    if(linearView instanceof  LinearLayout) {
                        EditText editText = (EditText) ((LinearLayout) linearView).getChildAt(0);
                        editTexts.add(editText);
                    }
                }

                String patientEmails = new String("");
                for (EditText editText : editTexts) {
                    patientEmails += editText.getText().toString().trim();
                    patientEmails += ";";
                }

                Map<String, String> urlParams = new HashMap<String, String>();

                Account account = new PulseAccount().getLoggedInAccount(getContext());
                String email = AccountManager.get(getContext()).getUserData(account, "email");
                Log.d(TAG, "onClick: " + email);

                urlParams.put("userEmail", email);
                urlParams.put("patientEmails", patientEmails);

                Log.d(TAG, "patientEmails: " + patientEmails);

                AddPatientsAsyncTask addPatientsAsyncTask = new AddPatientsAsyncTask(urlParams, new AddPatientsAsyncTask.AsyncResponse() {

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

                        if(response.getStatus().equals(ResponseConstants.ERROR)) {
                            List<String> invalidEmails = response.getData().getInvalidEmails();

                            Log.d(TAG, "processFinish: Invalid Emails");
                            for (String email : invalidEmails) {
                                Log.d(TAG, "processFinish: " + email);

                                for (EditText editText : editTexts) {
                                    if(editText.getText().toString().trim().equals(email)) {
                                        editText.setError("Email is not registered");
                                    }
                                }
                            }

                            return;
                        }

                        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setTitle("Added patients")
                                .setMessage("The patients were added successfully.")
                                .create();

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();
                    }

                    @Override
                    public void showDialog() {
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Adding patients...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                    }

                    @Override
                    public void dismissDialog() {
                        progressDialog.dismiss();
                    }

                });

                addPatientsAsyncTask.execute();
            }
        });
    }
}
