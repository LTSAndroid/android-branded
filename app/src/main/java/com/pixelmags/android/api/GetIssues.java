package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.FindMemberByEmailParser;
import com.pixelmags.android.json.GetIssuesParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetIssues extends WebRequest
{
    private static final String API_NAME="getIssues";
    private String mMagazineID;
    private String mAppBundleID;
    GetIssuesParser getIssuesParserParser;

    public GetIssues(){
        super(API_NAME);
    }
    public void init(String magazineID,String appBundleID)
    {
        mMagazineID = magazineID;
        mAppBundleID = appBundleID;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            getIssuesParserParser = new GetIssuesParser(getAPIResultData());
            if(getIssuesParserParser.initJSONParse()){

                if(getIssuesParserParser.isSuccess()){
                    getIssuesParserParser.parse();

                } else{

                    // Add error handling code here

                }

            }
        }

    }
    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(3);
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", mMagazineID));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", mAppBundleID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}

