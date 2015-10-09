package com.pixelmags.android.comms;

import com.pixelmags.android.storage.UserPrefs;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by austincoutinho on 01/09/15.
 */
public class WebRequest {


    private static final int NET_READ_TIMEOUT_MILLIS = 30000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 30000;

    private String API_URL;

    public String baseDeviceId;
    public String baseMagazineId;
    public String baseAppBundleId;
    public String baseApiMode;
    public String baseApiVersion;

    public List<NameValuePair> baseApiNameValuePairs;
    public int responseCode;
    private String resultData;

    public WebRequest(){}

    public WebRequest(String apiCall){

        API_URL = Config.API_BASE_URL+"/"+apiCall;

        baseDeviceId = UserPrefs.getDeviceID();
        baseMagazineId = Config.Magazine_Number;
        baseAppBundleId = Config.Bundle_ID;
        baseApiMode = Config.api_mode;
        baseApiVersion = Config.api_version;

        responseCode = -1;
        resultData = null;
    }

    public int getResponseCode(){
        return responseCode;
    }

    public String getAPIResultData(){
        return resultData;
    }


    public String doPostRequest(){

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(API_URL);

        try {

            httppost.setEntity(new UrlEncodedFormEntity(baseApiNameValuePairs));


            for (int i = 0; i < baseApiNameValuePairs.size(); i++) {
                System.out.println("baseApiNameValuePairs NAME === " + baseApiNameValuePairs.get(i).getName());
                System.out.println("baseApiNameValuePairs VALUE == " + baseApiNameValuePairs.get(i).getValue());
            }


            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            responseCode = response.getStatusLine().getStatusCode();

            if(responseCode == 200) {
                InputStream is = response.getEntity().getContent();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer baf = new ByteArrayBuffer(20);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

            /* Convert the Bytes read to a String. */
                resultData = new String(baf.toByteArray());

            }


        } catch (IOException e) {

            System.out.println(e.getMessage());
            return e.getMessage();

        }

        return resultData;

    }



}
