package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.ForgotPasswordParser;

import okhttp3.FormBody;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

/**
 * Created by Annie on 09/10/15.
 */
public class ForgotPassword extends WebRequest
{
    private static final String API_NAME="forgotPassword";
    ForgotPasswordParser fParser;
    private String mEmail;

    public ForgotPassword(){
        super(API_NAME);
    }
    public void init(String email)
    {
        mEmail = email;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            fParser = new ForgotPasswordParser(getAPIResultData());
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

        requestBody = new FormBody.Builder()
                .add("email", mEmail)
                .add("api_mode", baseApiMode)
                .add("api_version", baseApiVersion)
                .build();

//        baseApiNameValuePairs = new ArrayList<NameValuePair>(3);
//        baseApiNameValuePairs.add(new BasicNameValuePair("email", mEmail));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}

