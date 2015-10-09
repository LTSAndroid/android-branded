package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.GetDocumentKeyParser;
import com.pixelmags.android.json.GetEncryptionKeysParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetEncryptionKeys extends WebRequest
{
    private static final String API_NAME="getEncryptionKeys";
    private String mEmail;
    private String mPassword;
    private String mMagazineID;
    private String mAppBundleID;
    private String mDeviceID;
    private String mIssueID;
    GetEncryptionKeysParser getEncryptionKeysParser;

    public GetEncryptionKeys(){
        super(API_NAME);
    }
    public void init(String email, String password, String deviceID, String issueID,String magazineID,String appBundleID)
    {
        mMagazineID = magazineID;
        mAppBundleID = appBundleID;
        mEmail = email;
        mPassword = password;
        mDeviceID = deviceID;
        mIssueID = issueID;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            getEncryptionKeysParser = new GetEncryptionKeysParser(getAPIResultData());
            if(getEncryptionKeysParser.initJSONParse()){

                if(getEncryptionKeysParser.isSuccess()){
                    getEncryptionKeysParser.parse();

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
        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", mIssueID));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", mMagazineID));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", mAppBundleID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}

