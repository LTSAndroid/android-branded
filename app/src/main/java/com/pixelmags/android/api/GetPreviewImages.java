package com.pixelmags.android.api;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.datamodels.PreviewImage;
import com.pixelmags.android.json.GetPreviewImagesParser;

import java.util.ArrayList;

import okhttp3.FormBody;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

/**
 * Created by Annie on 09/10/15.
 */
public class GetPreviewImages extends WebRequest
{
    private static final String API_NAME="getPreviewImages";
    GetPreviewImagesParser getPreviewImagesParser;
    private String mIssueID;
    private String mAppBundleID;

    public GetPreviewImages(){
        super(API_NAME);
    }
    public ArrayList<PreviewImage> init(String issueID,String appBundleID)
    {
        mIssueID = issueID;
        mAppBundleID = appBundleID;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            getPreviewImagesParser = new GetPreviewImagesParser(getAPIResultData(),mIssueID);
            if(getPreviewImagesParser.initJSONParse()){

                if(getPreviewImagesParser.isSuccess()){
                    getPreviewImagesParser.parse();

                    return getPreviewImagesParser.previewImagesList;

                } else{

                    // Add error handling code here

                }

            }
        }

        return null;

    }
    private void setApiNameValuePairs(){

        requestBody = new FormBody.Builder()
                .add("issue_id", mIssueID)
                .add("app_bundle_id", Config.Bundle_ID)
                .add("api_mode", Config.api_mode)
                .add("api_version", Config.api_version)

                .build();

//        baseApiNameValuePairs = new ArrayList<NameValuePair>(3);
//        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", mIssueID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", mAppBundleID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}

