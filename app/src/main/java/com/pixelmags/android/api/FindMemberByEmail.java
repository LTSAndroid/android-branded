package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.FindMemberByEmailParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class FindMemberByEmail extends WebRequest
{
    private static final String API_NAME="findMemberByEmail";
    FindMemberByEmailParser fParser;
    private String mEmail;

    public FindMemberByEmail(){
        super(API_NAME);
    }
    public void init(String email)
    {
        mEmail = email;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            fParser = new FindMemberByEmailParser(getAPIResultData());
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
        baseApiNameValuePairs.add(new BasicNameValuePair("email", mEmail));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}
