package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.CanPurchaseParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class CreatePurchase extends WebRequest
{
    private static final String API_NAME="createPurchase";
    private String mEmail;
    private String mPassword;
    private String mPaymentGateway;
    private String mIssueID;
    private String mPurchaseReceipt;
    CanPurchaseParser cParser;

    public CreatePurchase(){
        super(API_NAME);
    }
    public void init(String email, String password, String paymentGateway, String issueID, String purchaseReceipt)
    {
        mEmail = email;
        mPassword = password;
        mPaymentGateway=paymentGateway;
        mIssueID=issueID;
        mPurchaseReceipt = purchaseReceipt;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            cParser = new CanPurchaseParser(getAPIResultData());
            if(cParser.initJSONParse()){

                if(cParser.isSuccess()){
                    cParser.parse();

                } else{

                    // Add error handling code here

                }

            }
        }

    }
    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(9);
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", mEmail));
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", mPassword));
        baseApiNameValuePairs.add(new BasicNameValuePair("payment_gateway", mPaymentGateway));
        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", mIssueID));
        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_receipt", mPurchaseReceipt));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", baseDeviceId));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", baseMagazineId));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
}
