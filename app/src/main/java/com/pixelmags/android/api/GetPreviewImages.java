package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.GetIssuesParser;
import com.pixelmags.android.json.GetPreviewImagesParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetPreviewImages extends WebRequest
{
    private static final String API_NAME="getPreviewImages";
    private String mIssueID;
    private String mAppBundleID;
    GetPreviewImagesParser getPreviewImagesParser;

    public GetPreviewImages(){
        super(API_NAME);
    }
    public void init(String issueID,String appBundleID)
    {
        mIssueID = issueID;
        mAppBundleID = appBundleID;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            getPreviewImagesParser = new GetPreviewImagesParser(getAPIResultData());
            if(getPreviewImagesParser.initJSONParse()){

                if(getPreviewImagesParser.isSuccess()){
                    getPreviewImagesParser.parse();

                } else{

                    // Add error handling code here

                }

            }
        }

    }
    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(3);
        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", mIssueID));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", mAppBundleID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}

