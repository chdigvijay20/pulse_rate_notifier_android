package com.test.digvijay.pulseratenotifier.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.account.PulseAccount;
import com.test.digvijay.pulseratenotifier.activities.MainActivity;
import com.test.digvijay.pulseratenotifier.asynctasks.GetNormalRestingHeartRateAsyncTask;
import com.test.digvijay.pulseratenotifier.asynctasks.UpdateFirebaseTokenAsyncTask;
import com.test.digvijay.pulseratenotifier.asynctasks.UserLoginAsyncTask;
import com.test.digvijay.pulseratenotifier.constants.HealthConstants;
import com.test.digvijay.pulseratenotifier.response.Data;
import com.test.digvijay.pulseratenotifier.response.ErrorData;
import com.test.digvijay.pulseratenotifier.response.NormalRestingHeartRateData;
import com.test.digvijay.pulseratenotifier.response.Response;
import com.test.digvijay.pulseratenotifier.response.ResponseConstants;
import com.test.digvijay.pulseratenotifier.response.ResponseWrapper;
import com.test.digvijay.pulseratenotifier.response.UserData;
import com.test.digvijay.pulseratenotifier.util.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "LoginFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

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


    private TextView registerTextView;
    private Button loginButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    private String email;
    private String password;

    private PreferenceManager preferenceManager;

    public void initUI(View view) {
        registerTextView = (TextView) view.findViewById(R.id.register_text_view);
        loginButton = (Button) view.findViewById(R.id.login_button);
        emailEditText = (EditText) view.findViewById(R.id.login_email_address_edit_text);
        passwordEditText = (EditText) view.findViewById(R.id.login_password_edit_text);

        preferenceManager = new PreferenceManager(getContext());

        addListeners();
    }

    public void addListeners() {
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                RegisterFragment fragment = new RegisterFragment();
                fragmentTransaction.replace(R.id.flContent, fragment);
                fragmentTransaction.commit();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            private boolean loginSuccessful = false;

            @Override
            public void onClick(View v) {
                if(isDataValid()) {
                    Map urlParams = new HashMap();
                    urlParams.put("email", email);
                    urlParams.put("password", password);

                    Map firebaseUrlParams = new HashMap();
                    firebaseUrlParams.put("email", email);
                    firebaseUrlParams.put("firebaseToken", preferenceManager.getFirebaseToken());

                    Log.d(TAG, "onClick: " + preferenceManager.getFirebaseToken());

                    final UpdateFirebaseTokenAsyncTask updateFirebaseTokenAsyncTask = new UpdateFirebaseTokenAsyncTask(firebaseUrlParams, new UpdateFirebaseTokenAsyncTask.AsyncResponse() {
                        ProgressDialog progressDialog;

                        @Override
                        public void processFinish(String output) {

                            if(output == null) {
                                Log.d(TAG, "Firebase token not updated. Cant connect to server.");
                                return;
                            }

                            Log.d(TAG, "Firebase token updated." + preferenceManager.getFirebaseToken());
                        }

                        @Override
                        public void showDialog() {
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Initializing...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                        }

                        @Override
                        public void dismissDialog() {
                            progressDialog.dismiss();
                        }
                    });

                    final GetNormalRestingHeartRateAsyncTask getNormalRestingHeartRateAsyncTask = new GetNormalRestingHeartRateAsyncTask(firebaseUrlParams, new GetNormalRestingHeartRateAsyncTask.AsyncResponse() {
                        ProgressDialog progressDialog;

                        @Override
                        public void processFinish(String output) {

                            if(output == null) {
                                Log.d(TAG, "Normal pulse rate values not updated. Cant connect to server.");
                                return;
                            }

                            Gson gson = new Gson();
                            ResponseWrapper responseWrapper = gson.fromJson(output, ResponseWrapper.class);
                            Response response = responseWrapper.getResponse();
                            Data data = response.getData();
                            NormalRestingHeartRateData normalRestingHeartRateData = data.getNormalRestingHeartRateData();

                            HealthConstants.setNormalRestingHeartRateMinimum(normalRestingHeartRateData.getMinimumPulseRate());
                            HealthConstants.setNormalRestingHeartRateMaximum(normalRestingHeartRateData.getMaximumPulseRate());

                            PreferenceManager preferenceManager = new PreferenceManager(getContext());
                            preferenceManager.setNormalMinimumRestingHeartRate(normalRestingHeartRateData.getMinimumPulseRate());
                            preferenceManager.setNormalMaximumRestingHeartRate(normalRestingHeartRateData.getMaximumPulseRate());

                            Log.d(TAG, "Normal heart rate values updated." + preferenceManager.getFirebaseToken());
                        }

                        @Override
                        public void showDialog() {
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Initializing...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                        }

                        @Override
                        public void dismissDialog() {
                            progressDialog.dismiss();
                        }
                    });

                    UserLoginAsyncTask userLoginAsyncTask = new UserLoginAsyncTask(urlParams, new UserLoginAsyncTask.AsyncResponse() {

                        private ProgressDialog progressDialog;

                        @Override
                        public void processFinish(String output) {

                            if(output == null) {
                                Log.d(TAG, "Logging In: Cant connect to server");

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

                            Log.d(TAG, "processFinish: received " + output);

                            Gson gson = new Gson();
                            ResponseWrapper responseWrapper = gson.fromJson(output, ResponseWrapper.class);
                            Response response = responseWrapper.getResponse();

                            if(!response.getStatus().equals(ResponseConstants.SUCCESS)) {

                                ErrorData errorData = response.getData().getErrorData();

                                final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("An error occurred");
                                alertDialog.setMessage(errorData.getErrorMessage());
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.got_it), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.dismiss();
                                    }
                                });
                                alertDialog.show();

                                return;

                            }

                            UserData userData = response.getData().getUserData();

                            Bundle bundle = new Bundle();
                            bundle.putString("fullName", userData.getFullName());
                            bundle.putString("role", userData.getRole());
                            bundle.putString("email", userData.getEmail());
                            bundle.putString("contactNumber", userData.getContactNumber());

                            //TODO get additional details... and put in the bundle ...important

                            PulseAccount pulseAccount = new PulseAccount(email, password, bundle);
                            pulseAccount.addAccount(getContext());
                            Log.d(TAG, "processFinish: account added");

                            NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);

                            ((MainActivity)getActivity()).startActivityForLoggedInUser(navigationView, pulseAccount.getLoggedInAccount(getContext()));

//                            View headerView = navigationView.getHeaderView(0);
//                            TextView nameTextView = (TextView) headerView.findViewById(R.id.nav_bar_name_text_view);
//                            Menu nav_menu = navigationView.getMenu();
//
//                            Account[] accounts = AccountManager.get(getContext()).getAccounts();
//                            if(accounts.length >= 1) {
//                                String fullName = AccountManager.get(getContext()).getUserData(accounts[accounts.length-1], "fullName");
//                                nameTextView.setText("Hello, " + fullName);
//                                nav_menu.findItem(R.id.nav_logout).setVisible(true);
//                                nav_menu.findItem(R.id.nav_register_or_login).setVisible(false);
////                                nav_menu.findItem(R.id.nav_show_pulse_rate).setVisible(true);
//                                nav_menu.findItem(R.id.nav_sync).setVisible(true);
//                                nav_menu.findItem(R.id.nav_settings).setVisible(true);
//                                nav_menu.findItem(R.id.nav_show_analysis).setVisible(true);
//                            } else {
//                                nameTextView.setText("Hello, Guest");
//                                nav_menu.findItem(R.id.nav_register_or_login).setVisible(true);
//                                nav_menu.findItem(R.id.nav_logout).setVisible(false);
//                            }

                            updateFirebaseTokenAsyncTask.execute();
                            getNormalRestingHeartRateAsyncTask.execute();

                            Fragment homeFragment = new HomeFragment();
                            FragmentManager fragmentManager = getFragmentManager();

                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flContent, homeFragment);
                            fragmentTransaction.commit();
                        }

                        @Override
                        public void showDialog() {
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Logging in...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                        }

                        @Override
                        public void dismissDialog() {
                            progressDialog.dismiss();
                        }
                    });

                    userLoginAsyncTask.execute();
                }
            }
        });
    }

    private boolean isDataValid() {

        boolean valid = true;

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if(email == null || "".equals(email)) {
            emailEditText.setError("Enter email address");
            valid = false;
        }

        if(password == null || "".equals(password)) {
            passwordEditText.setError("Enter password");
            valid = false;
        }

        return valid;
    }
}
