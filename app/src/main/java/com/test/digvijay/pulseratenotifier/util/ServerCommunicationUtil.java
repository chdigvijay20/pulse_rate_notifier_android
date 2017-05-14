package com.test.digvijay.pulseratenotifier.util;

import android.util.Log;

import com.test.digvijay.pulseratenotifier.constants.CharsetsConstants;
import com.test.digvijay.pulseratenotifier.constants.ServerConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import static com.google.android.gms.internal.zzt.TAG;

public class ServerCommunicationUtil {
    
    public String readResponse(HttpURLConnection connection) {

        BufferedReader reader = null;
        String response = null;

        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), CharsetsConstants.UTF8));
            StringBuilder responseBuilder = new StringBuilder();
            for (int c; (c = reader.read()) > 0; ) {
                responseBuilder.append((char) c);
            }

            response = responseBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public String getPostDataString(Map<String, String> urlParams) throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> entry : urlParams.entrySet()) {

            if(postData.length() != 0) {
                postData.append("&");
            }

            postData.append(URLEncoder.encode(entry.getKey(), CharsetsConstants.UTF8));
            postData.append("=");
            postData.append(URLEncoder.encode(entry.getValue(), CharsetsConstants.UTF8));
            Log.d(TAG, "getPostDataString: "  + URLEncoder.encode(entry.getValue(), CharsetsConstants.UTF8));
        }

        return postData.toString();
    }

    public HttpURLConnection getConnectionToPost(URL url, byte[] postDataBytes) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(ServerConstants.POST);
        connection.setRequestProperty(ServerConstants.CONTENT_TYPE, ServerConstants.URL_ENCODED);
        connection.setRequestProperty(ServerConstants.CONTENT_LENGTH, String.valueOf(postDataBytes.length));
        connection.setDoOutput(true);

        return  connection;
    }
}
