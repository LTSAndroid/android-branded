package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.GetSubscriptionsParser;
import com.pixelmags.android.json.ValidateUserParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 07/10/15.
 */
public class ValidateUser extends WebRequest
{
    private static final String API_NAME="validateUser";

    private String mEmail;
    private String mPassword;
    ValidateUserParser validateParser;

    public ValidateUser(){
        super(API_NAME);
    }

    public void setAPIData(String email, String password) {

        mEmail = email;
        mPassword = password;


        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            validateParser = new ValidateUserParser(getAPIResultData());
            if(validateParser.initJSONParse()){

                if(validateParser.isSuccess()){
                    validateParser.parse();
                } else{

                    // Add error handling code here

                }

            }
        }

    }


    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(6);
        baseApiNameValuePairs.add(new BasicNameValuePair("email", mEmail));
        baseApiNameValuePairs.add(new BasicNameValuePair("password", mPassword));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", baseDeviceId));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", baseMagazineId));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode",baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }


}

