package com.pixelmags.android.api;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.datamodels.MyIssue;
import com.pixelmags.android.json.GetIssuesParser;
import com.pixelmags.android.json.GetMyIssuesParser;
import com.pixelmags.android.storage.UserPrefs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetMyIssues extends WebRequest
{
    private static final String API_NAME="getMyIssues";

    GetMyIssuesParser getMyIssuesParser;

    public GetMyIssues(){
        super(API_NAME);
    }

    public void init()
    {

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            getMyIssuesParser = new GetMyIssuesParser(getAPIResultData());
            if(getMyIssuesParser.initJSONParse()){

                if(getMyIssuesParser.isSuccess()){
                    getMyIssuesParser.parse();
                    saveMyIssuesDataToApp();
                } else{

                    // Add error handling code here

                }

            }
        }

    }
    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(7);
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", UserPrefs.getUserEmail()));
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", UserPrefs.getUserPassword()));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", UserPrefs.getDeviceID()));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", Config.Magazine_Number));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", Config.Bundle_ID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));

    }

    public void saveMyIssuesDataToApp(){

        for(int i=0; i< getMyIssuesParser.myIssuesList.size();i++) {

            MyIssue issue = getMyIssuesParser.myIssuesList.get(i);

            System.out.println(" MyIssue ID ::" +issue.issueID);

        }


        // Save the Subscription Objects into the SQlite DB
        /*
        SubscriptionsDataSet mDbHelper = new SubscriptionsDataSet(BaseApp.getContext());
        mDbHelper.insert_all_subscriptions(mDbHelper.getWritableDatabase(), subsParser.subscriptionsList);
        mDbHelper.close();
        */
    }


}

