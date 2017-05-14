package com.test.digvijay.pulseratenotifier.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.test.digvijay.pulseratenotifier.constants.CharsetsConstants;
import com.test.digvijay.pulseratenotifier.constants.ServerConstants;
import com.test.digvijay.pulseratenotifier.util.ServerCommunicationUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class UpdateFirebaseTokenAsyncTask extends AsyncTask<String, String, String> {

    private static final String LOG_TAG = UpdateFirebaseTokenAsyncTask.class.toString();

    private Map<String, String> urlParams;
    private String response;

    AsyncResponse delegate;

    public UpdateFirebaseTokenAsyncTask(Map urlParams, AsyncResponse delegate) {
        this.urlParams = urlParams;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        delegate.showDialog();
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            String postData = new ServerCommunicationUtil().getPostDataString(urlParams);
            byte[] postDataBytes = postData.getBytes(CharsetsConstants.UTF8);

            URL url = new URL(ServerConstants.LOCAL_URL + "/updatefirebasetoken");
            HttpURLConnection connection = new ServerCommunicationUtil().getConnectionToPost(url, postDataBytes);

            connection.getOutputStream().write(postDataBytes);
            response = new ServerCommunicationUtil().readResponse(connection);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(LOG_TAG, "Update Firebase Token, result: " + result);

        delegate.dismissDialog();
        delegate.processFinish(result);
    }

    public interface AsyncResponse {
        void processFinish(String output);
        void showDialog();
        void dismissDialog();
    }
}
