package com.pixelmags.android.api;

import android.content.Intent;

import com.pixelmags.android.IssueView.NewIssueView;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.CanPurchaseParser;
import com.pixelmags.android.json.CreatePurchaseParser;
import com.pixelmags.android.storage.UserPrefs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class CreatePurchase extends WebRequest
{
    private static final String API_NAME="createPurchase";
    private int mIssue_id;
    private String mPurchaseReceipt;
    private String mPurchaseSignature;

    CreatePurchaseParser cParser;

    public CreatePurchase(){
        super(API_NAME);
    }
    public void init(int issue_id, String purchaseReceipt, String purchaseSignature)
    {
        mIssue_id = issue_id;
        mPurchaseReceipt = purchaseReceipt;
        mPurchaseSignature = purchaseSignature;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            cParser = new CreatePurchaseParser(getAPIResultData());
            if(cParser.initJSONParse()){

                if(cParser.isSuccess()){
                    cParser.parse();

                } else{

                    // Add error handling code here

                }

            }
        }

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
        baseApiNameValuePairs.add(new BasicNameValuePair("payment_gateway", "google"));
        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_receipt", mPurchaseReceipt));
        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_signature", mPurchaseSignature));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", Config.Bundle_ID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));

    }
}
