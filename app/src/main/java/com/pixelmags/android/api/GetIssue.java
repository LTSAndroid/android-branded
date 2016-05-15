package com.pixelmags.android.api;

import android.util.Log;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.download.QueueDownload;
import com.pixelmags.android.json.GetIssueParser;
import com.pixelmags.android.pixelmagsapp.service.PMService;
import com.pixelmags.android.storage.IssueDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.BaseApp;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 04/10/15.
 */

public class GetIssue extends WebRequest
{
    private static final String API_NAME="getIssue";
    private String mIssueID;
    private String TAG = "GetIssue";

    GetIssueParser getIssueParser;

    public GetIssue(){
        super(API_NAME);
    }
    public void init(String issueID)
    {

        mIssueID = issueID;

        setApiNameValuePairs();
        doPostRequest();

//        System.out.println("RESPONSE CODE "+responseCode+" data::" +getAPIResultData());

        if(responseCode==200){
            getIssueParser = new GetIssueParser(getAPIResultData());
            if(getIssueParser.initJSONParse()){

                if(getIssueParser.isSuccess()){
                    getIssueParser.parse();

                    saveIssueToApp();

                } else{

                    // Add error handling code here

                }

            }
        }

    }

    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(8);
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", UserPrefs.getUserEmail()));
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", UserPrefs.getUserPassword()));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", UserPrefs.getDeviceID()));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", Config.Magazine_Number));
        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", mIssueID));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", Config.Bundle_ID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));
    }

    private void saveIssueToApp(){

        // Save the Subscription Objects into the SQlite DB
        IssueDataSet mDbHelper = new IssueDataSet(BaseApp.getContext());

        Log.d(TAG, "Get issue parser of Issue is :" + getIssueParser.mIssue.toString());

        mDbHelper.insertIssueData(mDbHelper.getWritableDatabase(), getIssueParser.mIssue);
        mDbHelper.close();

        // Saving Object into All Download Data Set . change by Likith

//        QueueDownload queueDownload = new QueueDownload();
//        queueDownload.insertIssueInDownloadQueue(String.valueOf(getIssueParser.mIssue.issueID));

        boolean result = startIssueDownload(String.valueOf(getIssueParser.mIssue.issueID));

        Log.d(TAG,"Result of queue download insert is : "+result);

        if(result){
            Log.d(TAG,"Inside the if condition of notify service of new download");
            PMService pmService = new PMService();
            pmService.newDownloadRequested();
        }

    }

    private boolean startIssueDownload(String issueId){

        QueueDownload queueIssue = new QueueDownload();
        return queueIssue.insertIssueInDownloadQueue(issueId);

    }


}

