package com.pixelmags.android.comms;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by austincoutinho on 01/09/15.
 */
public class WebRequest {


    private static final int NET_READ_TIMEOUT_MILLIS = 30000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 30000;


    public String doWebRequest(String urlString){


        // auth_email_address :: austin.coutinho@pixelmags.com
        // auth_password :: austinpixelmags
        // device_id :: 12345
        // magazine_id :: 1597
        // app_bundle_id :: com.pixelmags.android.managed.hoffman-media-cottage-journal-magazine
        // API Mode :: Dedicated App


        String resultToDisplay = "";
        InputStream in = null;

        // HTTP Get
        try {

            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
            urlConnection.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
            urlConnection.setRequestMethod("GET");

            in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }

            resultToDisplay = total.toString();

        } catch (Exception e ) {

            System.out.println(e.getMessage());
            return e.getMessage();

        }

        return resultToDisplay;
    }



}
