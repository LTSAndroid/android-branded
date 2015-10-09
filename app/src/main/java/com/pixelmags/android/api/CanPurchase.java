package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.CanPurchaseParser;
import com.pixelmags.android.json.CreateUserParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class CanPurchase extends WebRequest
{
    private static final String API_NAME="canPurchase";
    private String mEmail;
    private String mPassword;
    private String mPaymentGateway;
    private String mIssueID;
    CanPurchaseParser cParser;

    public CanPurchase(){
        super(API_NAME);
    }
    public void init(String email, String password, String paymentGateway, String issueID)
    {
        mEmail = email;
        mPassword = password;
        mPaymentGateway=paymentGateway;
        mIssueID=issueID;

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

        baseApiNameValuePairs = new ArrayList<NameValuePair>(8);
        baseApiNameValuePairs.add(new BasicNameValuePair("email", mEmail));
        baseApiNameValuePairs.add(new BasicNameValuePair("password", mPassword));
        baseApiNameValuePairs.add(new BasicNameValuePair("payment_gateway", mPaymentGateway));
        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", mIssueID));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", baseDeviceId));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", baseMagazineId));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }
    }
