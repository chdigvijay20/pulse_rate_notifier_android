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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.account.PulseAccount;
import com.test.digvijay.pulseratenotifier.activities.MainActivity;
import com.test.digvijay.pulseratenotifier.asynctasks.RegisterUserAsyncTask;
import com.test.digvijay.pulseratenotifier.asynctasks.UpdateFirebaseTokenAsyncTask;
import com.test.digvijay.pulseratenotifier.response.Response;
import com.test.digvijay.pulseratenotifier.response.ResponseWrapper;
import com.test.digvijay.pulseratenotifier.response.UserData;
import com.test.digvijay.pulseratenotifier.util.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "RegisterFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

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

    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText contactNumberEditText;
    private EditText passwordEditText;
    private Spinner roleSpinner;

    private String fullName;
    private String email;
    private String contactNumber;
    private String password;
    private String role;

    private Button registerButton;
    private TextView haveAccountTextView;

    private PreferenceManager preferenceManager;

    private void initUI(View view) {

        fullNameEditText = (EditText) view.findViewById(R.id.full_name_edit_text);
        emailEditText = (EditText) view.findViewById(R.id.email_edit_text);
        contactNumberEditText = (EditText) view.findViewById(R.id.contact_edit_text);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text);

        registerButton = (Button) view.findViewById(R.id.register_button);
        haveAccountTextView = (TextView) view.findViewById(R.id.have_account_text_view);

        ArrayAdapter<CharSequence> roleArrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.role_array, android.R.layout.simple_spinner_item);
        roleArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        roleSpinner = (Spinner) view.findViewById(R.id.role_spinner);
        roleSpinner.setAdapter(roleArrayAdapter);

        preferenceManager = new PreferenceManager(getContext());

        addListeners();
    }

    private boolean isDataValid() {

        boolean valid = true;

        fullName = String.valueOf(fullNameEditText.getText());
        email = String.valueOf(emailEditText.getText());
        contactNumber = String.valueOf(contactNumberEditText.getText());
        password = String.valueOf(passwordEditText.getText());
        role = String.valueOf(roleSpinner.getSelectedItem());

        if(fullName == null || "".equals(fullName)) {
            fullNameEditText.setError("Enter Full Name");
            valid = false;
        }

        if(email == null || "".equals(email)) {
            emailEditText.setError("Enter Email Id");
            valid = false;
        }

        if(contactNumber == null || "".equals(contactNumber)) {
            contactNumberEditText.setError("Enter Mobile Number");
            valid = false;
        }

        if(password == null || "".equals(password)) {
            passwordEditText.setError("Enter Password");
            valid = false;
        }

        return valid;
    }

    private void addListeners() {
        haveAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                LoginFragment fragment = new LoginFragment();
                fragmentTransaction.replace(R.id.flContent, fragment);
                fragmentTransaction.commit();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDataValid()) {
                    Log.e(TAG, "Invalid entries");
                    return;
                }

                Map<String, String> urlParams = new HashMap<>();
                urlParams.put("fullName", fullName);
                urlParams.put("email", email);
                urlParams.put("contactNumber", contactNumber);
                urlParams.put("password", password);
                urlParams.put("role", role);

                Map firebaseUrlParams = new HashMap();
                firebaseUrlParams.put("email", email);
                firebaseUrlParams.put("firebaseToken", preferenceManager.getFirebaseToken());

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

                RegisterUserAsyncTask asyncTask = new RegisterUserAsyncTask(urlParams, new RegisterUserAsyncTask.AsyncResponse() {

                    private ProgressDialog progressDialog;

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

                        Log.d(TAG, "processFinish: received " + output);


                        Gson gson = new Gson();
                        ResponseWrapper responseWrapper = gson.fromJson(output, ResponseWrapper.class);
                        Response response = responseWrapper.getResponse();

                        Log.d(TAG, "processFinish: " + response.getStatus() + " " + response.getData());


                        UserData userData = response.getData().getUserData();

                        Bundle bundle = new Bundle();
                        bundle.putString("fullName", userData.getFullName());
                        bundle.putString("role", userData.getRole());
                        bundle.putString("email", userData.getEmail());

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

                        if(preferenceManager.getFirebaseToken() != null) {
                            updateFirebaseTokenAsyncTask.execute();
                        }

                        Fragment fragment = null;
                        if(role.equals(getString(R.string.patient))) {
                            fragment = new PatientDetailsFragment();
                        } else if(role.equals(getString(R.string.care_taker))) {
                            fragment = new AddPatientsFragment();
                        } else if(role.equals(getString(R.string.doctor))) {
                            fragment = new AddPatientsFragment();
                        }

                        if(fragment != null) {
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            fragmentTransaction.replace(R.id.flContent, fragment);
                            fragmentTransaction.commit();
                        }
                    }

                    @Override
                    public void showDialog() {
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Registering wait...");
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
    }
}
