package com.pixelmags.android.api;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.GetEncryptionKeysParser;
import com.pixelmags.android.storage.UserPrefs;

import okhttp3.FormBody;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

/**
 * Created by Annie on 09/10/15.
 */
public class GetEncryptionKeys extends WebRequest
{
    private static final String API_NAME="getEncryptionKeys";
    GetEncryptionKeysParser getEncryptionKeysParser;
    private String mEmail;
    private String mPassword;
    private String mMagazineID;
    private String mAppBundleID;
    private String mDeviceID;
    private String mIssueID;

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

        requestBody = new FormBody.Builder()
                .add("email", mEmail)
                .add("password", mPassword)
                .add("device_id", UserPrefs.getDeviceID())
                .add("issue_id", mIssueID)
                .add("magazine_id", Config.Magazine_Number)
                .add("app_bundle_id", Config.Bundle_ID)
                .add("api_mode", Config.api_mode)
                .add("api_version", Config.api_version)

                .build();

//        baseApiNameValuePairs = new ArrayList<NameValuePair>(7);
//        baseApiNameValuePairs.add(new BasicNameValuePair("email", mEmail));
//        baseApiNameValuePairs.add(new BasicNameValuePair("password", mPassword));
//        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", mDeviceID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", mIssueID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", mMagazineID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", mAppBundleID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}

