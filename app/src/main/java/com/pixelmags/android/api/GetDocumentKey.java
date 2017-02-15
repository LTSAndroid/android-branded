package com.pixelmags.android.api;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.GetDocumentKeyParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetDocumentKey extends WebRequest
{
    private static final String API_NAME="getDocumentKey";
    GetDocumentKeyParser getDocumentKeyParser;
    private String mEmail;
    private String mPassword;
    private String mMagazineID;
    private String mAppBundleID;
    private String mDeviceID;
    private String mIssueID;
    private String documentKey;

    public GetDocumentKey(){
        super(API_NAME);
    }
    public String init(String email, String password, String deviceID, String issueID,String magazineID,String appBundleID)
    {
        this.mMagazineID = magazineID;
        this.mAppBundleID = appBundleID;
        this.mEmail = email;
        this.mPassword = password;
        this.mDeviceID = deviceID;
        this.mIssueID = issueID;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){

            documentKey = getAPIResultData();
            getDocumentKeyParser = new GetDocumentKeyParser(getAPIResultData());
            if(getDocumentKeyParser.initJSONParse()){

                if(getDocumentKeyParser.isSuccess()){
                    getDocumentKeyParser.parse();
                    documentKey = getAPIResultData();

                } else{

                    // Add error handling code here

                }

            }
        }

        return documentKey;

    }
    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(8);
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", mEmail));
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", mPassword));
        baseApiNameValuePairs.add(new BasicNameValuePair("password", mPassword));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", mDeviceID));
        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", mIssueID));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", mMagazineID));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", mAppBundleID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));

    }

}

