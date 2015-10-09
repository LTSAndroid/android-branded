package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.GetIssuesParser;
import com.pixelmags.android.json.GetMyIssuesParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetMyIssues extends WebRequest
{
    private static final String API_NAME="getMyIssues";
    private String mEmail;
    private String mPassword;
    private String mMagazineID;
    private String mAppBundleID;
    private String mDeviceID;
    GetMyIssuesParser getMyIssuesParserParser;

    public GetMyIssues(){
        super(API_NAME);
    }
    public void init(String email, String password, String deviceID, String magazineID,String appBundleID)
    {
        mMagazineID = magazineID;
        mAppBundleID = appBundleID;
        mEmail = email;
        mPassword = password;
        mDeviceID = deviceID;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            getMyIssuesParserParser = new GetMyIssuesParser(getAPIResultData());
            if(getMyIssuesParserParser.initJSONParse()){

                if(getMyIssuesParserParser.isSuccess()){
                    getMyIssuesParserParser.parse();

                } else{

                    // Add error handling code here

                }

            }
        }

    }
    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(7);
        baseApiNameValuePairs.add(new BasicNameValuePair("email", mEmail));
        baseApiNameValuePairs.add(new BasicNameValuePair("password", mPassword));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", mDeviceID));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", mMagazineID));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", mAppBundleID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}

