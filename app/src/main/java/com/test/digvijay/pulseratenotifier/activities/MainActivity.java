package com.test.digvijay.pulseratenotifier.activities;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sevenheaven.iosswitch.ShSwitchView;
import com.test.digvijay.pulseratenotifier.R;
import com.test.digvijay.pulseratenotifier.account.PulseAccount;
import com.test.digvijay.pulseratenotifier.asynctasks.GetEmergencyNumbersAsyncTask;
import com.test.digvijay.pulseratenotifier.constants.HealthConstants;
import com.test.digvijay.pulseratenotifier.fragment.AddPatientsFragment;
import com.test.digvijay.pulseratenotifier.fragment.HomeFragment;
import com.test.digvijay.pulseratenotifier.fragment.LoginFragment;
import com.test.digvijay.pulseratenotifier.fragment.PatientDetailsFragment;
import com.test.digvijay.pulseratenotifier.fragment.PatientListFragment;
import com.test.digvijay.pulseratenotifier.fragment.PulseRateFragment;
import com.test.digvijay.pulseratenotifier.fragment.RegisterFragment;
import com.test.digvijay.pulseratenotifier.fragment.ReportFragment;
import com.test.digvijay.pulseratenotifier.fragment.SyncFragment;
import com.test.digvijay.pulseratenotifier.fragment.TestFragment;
import com.test.digvijay.pulseratenotifier.notifications.NotificationUtil;
import com.test.digvijay.pulseratenotifier.response.PatientData;
import com.test.digvijay.pulseratenotifier.response.Response;
import com.test.digvijay.pulseratenotifier.response.ResponseWrapper;
import com.test.digvijay.pulseratenotifier.util.PreferenceManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.test.digvijay.pulseratenotifier.fragment.HomeFragment.bluetoothHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RegisterFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener, PulseRateFragment.OnFragmentInteractionListener, PatientDetailsFragment.OnFragmentInteractionListener, AddPatientsFragment.OnFragmentInteractionListener, TestFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, SyncFragment.OnFragmentInteractionListener, ReportFragment.OnFragmentInteractionListener, PatientListFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Account account = new PulseAccount().getLoggedInAccount(getApplicationContext());

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        HealthConstants.setNormalRestingHeartRateMinimum(preferenceManager.getNormalMinimumRestingHeartRate());
        HealthConstants.setNormalRestingHeartRateMaximum(preferenceManager.getNormalMaximumRestingHeartRate());

//        for (Account account : accounts) {
////            AccountManager.get(getApplicationContext()).removeAccountExplicitly(account);
//            Log.d(TAG, "onCreate: " + account.name);
//        }

        if(account != null) {
            startActivityForLoggedInUser(navigationView, account);
        } else {
            startActivityForLoggedOutUser(navigationView);
        }

        String menuFragment = getIntent().getStringExtra("fragment");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment startFragment = null;
        if (menuFragment != null) {
            if (menuFragment.equals("reportFragment")) {
                startFragment = new ReportFragment();
            }
        } else {
            startFragment = new HomeFragment();
        }

        fragmentTransaction.replace(R.id.flContent, startFragment);
        fragmentTransaction.commit();

        Log.d(TAG, "onCreate: refreshed token " + new PreferenceManager(getApplicationContext()).getFirebaseToken());
    }

    public void startActivityForLoggedOutUser(NavigationView navigationView) {

        View headerView = navigationView.getHeaderView(0);
        TextView nameTextView = (TextView) headerView.findViewById(R.id.nav_bar_name_text_view);
        Menu nav_menu = navigationView.getMenu();

        nameTextView.setText("Hello, Guest");
        nav_menu.findItem(R.id.nav_register_or_login).setVisible(true);
        nav_menu.findItem(R.id.nav_logout).setVisible(false);
        nav_menu.findItem(R.id.nav_sync).setVisible(false);
        nav_menu.findItem(R.id.nav_settings).setVisible(false);
        nav_menu.findItem(R.id.nav_show_analysis).setVisible(false);
    }

    public void startActivityForLoggedInUser(NavigationView navigationView, Account account) {

        View headerView = navigationView.getHeaderView(0);
        TextView nameTextView = (TextView) headerView.findViewById(R.id.nav_bar_name_text_view);
        Menu nav_menu = navigationView.getMenu();

        String fullName = new PulseAccount().getDataOfLoggedInUser(getApplicationContext(), "fullName");

        nameTextView.setText("Hello, " + fullName);
        nav_menu.findItem(R.id.nav_logout).setVisible(true);
        nav_menu.findItem(R.id.nav_register_or_login).setVisible(false);
        nav_menu.findItem(R.id.nav_sync).setVisible(true);
        nav_menu.findItem(R.id.nav_settings).setVisible(true);
        nav_menu.findItem(R.id.nav_show_analysis).setVisible(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_emergency) {
            undertakeEmergencyMeasures();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_home) {
            fragmentClass = HomeFragment.class;
        } else if (id == R.id.nav_register_or_login) {
            fragmentClass = RegisterFragment.class;
        } else if (id == R.id.nav_sync) {
            fragmentClass = SyncFragment.class;
        } else if (id == R.id.nav_about_us) {
            Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        } else if (id == R.id.nav_logout) {

            new PulseAccount().removeAccount(getApplicationContext());

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            startActivityForLoggedOutUser(navigationView);

        } else if (id == R.id.nav_settings) {

            return true;
        } else if (id == R.id.nav_show_analysis) {
            fragmentClass = ReportFragment.class;
        } else if (id == R.id.nav_show_patients) {
            fragmentClass = PatientListFragment.class;
        }

        if(fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void undertakeEmergencyMeasures() {

        String email = new PulseAccount().getDataOfLoggedInUser(getApplicationContext(), "email");
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

//                    final AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
//                    alertDialog.setTitle("An error occurred");
//                    alertDialog.setMessage("Can not connect to server.");
//                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            alertDialog.dismiss();
//                        }
//                    });
//                    alertDialog.show();
                    return;
                }

                Log.d(TAG, "processFinish: received emergency data " + output);

                Gson gson = new Gson();
                ResponseWrapper responseWrapper = gson.fromJson(output, ResponseWrapper.class);
                Response response = responseWrapper.getResponse();
                PatientData patientData = response.getData().getPatientData();

                String patientName = new PulseAccount().getDataOfLoggedInUser(getApplicationContext(), "fullName");
                List<String> emergencyContacts = patientData.getEmergencyContacts();

//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 2);
                NotificationUtil notificationUtil = new NotificationUtil();

                for (String emergencyContact : emergencyContacts) {
                    notificationUtil.sendSMS(emergencyContact, patientName);
                    Log.d(TAG, "processFinish: sent sms to " + emergencyContact);
                }

                for (String emergencyContact : emergencyContacts) {
                    notificationUtil.call(getApplicationContext(), emergencyContact);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            HomeFragment.getBluetoothHelper().connectToDevice();
        } else {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            View view = fragmentList.get(0).getView();
            ShSwitchView pulseSwitchView = (ShSwitchView) view.findViewById(R.id.pulse_rate_switch_view);
            pulseSwitchView.setOn(false);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
