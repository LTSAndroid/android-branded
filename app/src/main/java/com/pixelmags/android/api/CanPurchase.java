package com.pixelmags.android.api;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.CanPurchaseParser;
import com.pixelmags.android.storage.UserPrefs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class CanPurchase extends WebRequest
{
    private static final String API_NAME = "canPurchase";

    private int mIssue_id;
    private String mSKU;
    CanPurchaseParser cParser;

    public CanPurchase()
    {
        super(API_NAME);
    }
    public boolean init(String SKU , int issue_id)
    {
        boolean success = false;
        mIssue_id = issue_id;
        mSKU = SKU;
        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            cParser = new CanPurchaseParser(getAPIResultData());
            if(cParser.initJSONParse()){

                if(cParser.isSuccess())
                {
                    success = true;
                    // Enable these when going live

                    //cParser.parse();
                    //launch purchase
                   /* MainActivity mActivity= new MainActivity();
                    mActivity.createPurchaseLauncher(mSKU,mIssue_id);*/
                }
            }

        }
        return success;
    }
    private void setApiNameValuePairs()
    {
            String issueId = String.valueOf(mIssue_id);
        baseApiNameValuePairs = new ArrayList<NameValuePair>(9);
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", UserPrefs.getUserEmail()));
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", UserPrefs.getUserPassword()));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", UserPrefs.getDeviceID()));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", Config.Magazine_Number));
        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", issueId));
        baseApiNameValuePairs.add(new BasicNameValuePair("payment_gateway", "Amazon"));  // Change it to google once it going to live
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", Config.Bundle_ID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));

    }
    }
