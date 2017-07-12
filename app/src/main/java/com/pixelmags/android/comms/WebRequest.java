package com.pixelmags.android.comms;

import android.util.Log;

import com.pixelmags.android.storage.UserPrefs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by austincoutinho on 01/09/15.
 */

public class WebRequest {


    private static final int NET_READ_TIMEOUT_MILLIS = 30000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 30000;
    public String baseDeviceId;
    public String baseMagazineId;
    public String baseAppBundleId;
    public String baseApiMode;
    public String baseApiVersion;
//    public List<NameValuePair> baseApiNameValuePairs;
    public int responseCode;
    public RequestBody requestBody;
    OkHttpClient clientAPI;
    private String TAG = "WebRequest";
    private String API_URL;
    private String resultData;

    public WebRequest(){}

    public WebRequest(String apiCall){

        API_URL = Config.API_BASE_URL+"/"+apiCall;

        baseDeviceId = UserPrefs.getDeviceID();
        baseMagazineId = Config.Magazine_Number;
        baseAppBundleId = Config.Bundle_ID;
        baseApiMode = Config.api_mode;
        baseApiVersion = Config.api_version;

        clientAPI = new OkHttpClient();

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

        try {

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Content-Type","application/x-www-form-urlencoded")
                    .post(requestBody)
                    .build();


            Log.e("Url Sejeeth",API_URL);
            Log.e("Keys Sejeeth",requestBody.toString());


            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.connectTimeout(60, TimeUnit.SECONDS);

            Response responses = clientAPI.newCall(request).execute();

            responseCode = responses.code();

            if(responseCode == 200) {
                InputStream is = responses.body().byteStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int current = 0;
                while ((current = bis.read()) != -1) {
                    buffer.write((byte) current);
                }

            /* Convert the Bytes read to a String. */
                resultData = new String(buffer.toByteArray());

                Log.d(TAG,"Result Data is : " +resultData);


                is.close();
            }





            // Commented Till here





//            HttpClient httpclient = new DefaultHttpClient();
//
//            Log.d(TAG,"API URL is : " +API_URL);
//
//            HttpPost httppost = new HttpPost(API_URL);
//
//            httppost.setEntity(new UrlEncodedFormEntity(baseApiNameValuePairs));
//
//  // To analyse if any API returns erroreous data
//  /*          for (int i = 0; i < baseApiNameValuePairs.size(); i++) {
//                System.out.println("baseApiNameValuePairs NAME === " + baseApiNameValuePairs.get(i).getName());
//                System.out.println("baseApiNameValuePairs VALUE == " + baseApiNameValuePairs.get(i).getValue());
//            }
//*/
//
//            // Execute HTTP Post Request
//            HttpResponse response = httpclient.execute(httppost);
//
//            responseCode = response.getStatusLine().getStatusCode();
//
//            Log.d(TAG," Response Code is : " +responseCode);
//
//            if(responseCode == 200) {
//                InputStream is = response.getEntity().getContent();
//                BufferedInputStream bis = new BufferedInputStream(is);
//                ByteArrayBuffer baf = new ByteArrayBuffer(20);
//                int current = 0;
//                while ((current = bis.read()) != -1) {
//                    baf.append((byte) current);
//                }
//
//            /* Convert the Bytes read to a String. */
//                resultData = new String(baf.toByteArray());
//
//                Log.d(TAG,"Result Data is : " +resultData);
//
//
//                is.close();
//            }
//
//
//            if( response.getEntity() != null ) {
//                response.getEntity().consumeContent();
//            }
//
//
//            httpclient.getConnectionManager().closeExpiredConnections();
//            httpclient.getConnectionManager().shutdown();  // shutdown the HttpClient after use


        } catch (IOException e) {

            System.out.println(e.getMessage());
            return e.getMessage();

        }

        return resultData;

    }



}
