package com.pixelmags.android.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.download.QueueDownload;
import com.pixelmags.android.json.GetIssueParser;
import com.pixelmags.android.pixelmagsapp.service.PMService;
import com.pixelmags.android.storage.IssueDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.BaseApp;

import okhttp3.FormBody;

import static android.content.Context.MODE_PRIVATE;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

/**
 * Created by austincoutinho on 04/10/15.
 */

public class GetIssue extends WebRequest
{
    private static final String API_NAME="getIssue";
    private static boolean failure = false;
    GetIssueParser getIssueParser;
    private String mIssueID;
    private String TAG = "GetIssue";

    public GetIssue(){
        super(API_NAME);
    }

    public static boolean setGetIssueFailure(){
        return failure;
    }


    public void init(String issueID)
    {

        mIssueID = issueID;

        setApiNameValuePairs();
        doPostRequest();

        System.out.println("RESPONSE CODE "+responseCode+" data::" +getAPIResultData());

        if(responseCode==200){
            getIssueParser = new GetIssueParser(getAPIResultData());
            if(getIssueParser.initJSONParse()){

                Log.d(TAG," value of is success is : "+getIssueParser.isSuccess());

                if(getIssueParser.isSuccess()){
                    getIssueParser.parse();
                    failure = false;


                    saveIssueToApp();


                } else{

                    Log.d(TAG,"Inside the error condition");

                    // Add error handling code here
                    failure = true;

                }

            }
        }

    }

    private void setApiNameValuePairs(){

        requestBody = new FormBody.Builder()
                .add("auth_email_address", UserPrefs.getUserEmail())
                .add("auth_password", UserPrefs.getUserPassword())
                .add("device_id", UserPrefs.getDeviceID())
                .add("magazine_id", Config.Magazine_Number)
                .add("issue_id", mIssueID)
                .add("app_bundle_id", Config.Bundle_ID)
                .add("api_mode", Config.api_mode)
                .add("api_version", Config.api_version)

                .build();
        Log.e("auth_email_address", UserPrefs.getUserEmail());
        Log.e("auth_password", UserPrefs.getUserPassword());
        Log.e("device_id", UserPrefs.getDeviceID());
        Log.e("magazine_id", Config.Magazine_Number);
        Log.e("issue_id", mIssueID);
        Log.e("app_bundle_id", Config.Bundle_ID);
        Log.e("api_mode", Config.api_mode);
        Log.e("api_version", Config.api_version);


//        baseApiNameValuePairs = new ArrayList<NameValuePair>(8);
//        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", UserPrefs.getUserEmail()));
//        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", UserPrefs.getUserPassword()));
//        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", UserPrefs.getDeviceID()));
//        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", Config.Magazine_Number));
//        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", mIssueID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", Config.Bundle_ID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));
    }

    private void saveIssueToApp(){


        try {


            IssueDataSet mDbHelper = new IssueDataSet(BaseApp.getContext());
            mDbHelper.insertIssueData(mDbHelper.getWritableDatabase(), getIssueParser.mIssue);
            mDbHelper.close();

            boolean result = startIssueDownload(String.valueOf(getIssueParser.mIssue.issueID));

            Log.e(TAG, "Result of queue download insert is : " + result);


            if (result) {

                Log.e(TAG, "Inside the if condition of notify service of new download");
                PMService pmService = new PMService();
                pmService.newDownloadRequested();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean startIssueDownload(String issueId){

        QueueDownload queueIssue = new QueueDownload();
        return queueIssue.insertIssueInDownloadQueue(issueId);

    }




}

