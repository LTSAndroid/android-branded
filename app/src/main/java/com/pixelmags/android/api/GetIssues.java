package com.pixelmags.android.api;

import android.util.Log;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.download.DownloadThumbnails;
import com.pixelmags.android.json.GetIssuesParser;
import com.pixelmags.android.storage.AllIssuesDataSet;
import com.pixelmags.android.util.BaseApp;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetIssues extends WebRequest {

    private static final String API_NAME = "getIssues";
    GetIssuesParser getIssuesParserParser;
    private String mMagazineID;
    private String mAppBundleID;
    private String TAG = "GetIssues";

    public GetIssues() {
        super(API_NAME);
    }

    public void init(String magazineID, String appBundleID) {
        mMagazineID = magazineID;
        mAppBundleID = appBundleID;

        setApiNameValuePairs();
        doPostRequest();

        if (responseCode == 200) {
            getIssuesParserParser = new GetIssuesParser(getAPIResultData());
            if (getIssuesParserParser.initJSONParse()) {
                if (getIssuesParserParser.isSuccess()) {
                    getIssuesParserParser.parse();
                    saveAllIssuesData();

                } else {

                    // Add error handling code here

                }

            }
        }

    }

    private void setApiNameValuePairs() {

        baseApiNameValuePairs = new ArrayList<NameValuePair>(4);
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", mMagazineID));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", mAppBundleID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }


    public void saveAllIssuesData() {

        // printing the values of the Magazine objects
/*        for (int i = 0; i < getIssuesParserParser.allIssuesList.size(); i++) {
            Magazine mag = getIssuesParserParser.allIssuesList.get(i);
            System.out.println("MAGAZINE Title === " + mag.title);
        }
*/

        Log.d(TAG,"All Issue received from the API is : "+getIssuesParserParser.allIssuesList.toString());

        getIssuesParserParser.allIssuesList = DownloadThumbnails.DownloadAllThumbnailData(getIssuesParserParser.allIssuesList);


        // Save the Subscription Objects into the SQlite DB
        AllIssuesDataSet mDbHelper = new AllIssuesDataSet(BaseApp.getContext());

        Log.d(API_NAME,"MDbHelper Writable Database : " +mDbHelper.getWritableDatabase() +
                " All Issue list is :" +getIssuesParserParser.allIssuesList.toString());


        mDbHelper.insert_all_issues_data(mDbHelper.getWritableDatabase(), getIssuesParserParser.allIssuesList);
        mDbHelper.close();

    }

}