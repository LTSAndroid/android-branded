package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.FindUserByDeviceIDParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class FindUserByDeviceID extends WebRequest
{
    private static final String API_NAME="findUserByDeviceID";
    FindUserByDeviceIDParser fParser;
    private String mDeviceID;

    public FindUserByDeviceID(){
        super(API_NAME);
    }
    public void init(String deviceID)
    {
        mDeviceID = deviceID;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            fParser = new FindUserByDeviceIDParser(getAPIResultData());
            if(fParser.initJSONParse()){

                if(fParser.isSuccess()){
                    fParser.parse();

                } else{

                    // Add error handling code here

                }

            }
        }

    }
    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(3);
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", mDeviceID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}

