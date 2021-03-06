package com.pixelmags.android.api;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.RequestDeregisterParser;

import okhttp3.FormBody;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

/**
 * Created by Annie on 09/10/15.
 */
public class RequestDeregister extends WebRequest
{
    private static final String API_NAME="requestDeregister";
    RequestDeregisterParser getRequestDeregisterParser;
    private String mEmail;
    private String mdeviceID;

    public RequestDeregister(){
        super(API_NAME);
    }
    public void init(String email,String deviceID)
    {
        mEmail = email;
        mdeviceID = deviceID;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            getRequestDeregisterParser = new RequestDeregisterParser(getAPIResultData());
            if(getRequestDeregisterParser.initJSONParse()){

                if(getRequestDeregisterParser.isSuccess()){
                    getRequestDeregisterParser.parse();

                } else{

                    // Add error handling code here

                }

            }
        }

    }
    private void setApiNameValuePairs(){

        requestBody = new FormBody.Builder()
                .add("email", mEmail)
                .add("device_id", mdeviceID)
                .add("api_mode", Config.api_mode)
                .add("api_version", Config.api_version)

                .build();

//        baseApiNameValuePairs = new ArrayList<NameValuePair>(3);
//        baseApiNameValuePairs.add(new BasicNameValuePair("email", mEmail));
//        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", mdeviceID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}


