package com.pixelmags.android.api;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.ErrorMessage;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.CreatePurchaseParser;
import com.pixelmags.android.pixelmagsapp.MainActivity;
import com.pixelmags.android.storage.UserPrefs;

import okhttp3.FormBody;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

/**
 * Created by Annie on 09/10/15.
 */
public class CreatePurchase extends WebRequest
{
    private static final String API_NAME="createPurchase";
    CreatePurchaseParser cParser;
    GetMyIssues apiGetMyIssues;
    GetMySubscriptions apiGetMySubscription;
    private int mIssue_id;
    private String mPurchaseReceipt;
    private String mPurchaseSignature;
    private String mPurchasePrice;
    private String mPurchaseCurrencyType;
    private String TAG = "CreatePurchase";
    private Activity activity;

    public CreatePurchase(){
        super(API_NAME);
    }
    public void init(int issue_id, String purchaseReceipt, String purchaseSignature,String purchasePrice,
                     String purchaseCurrencyType, Activity activity)
    {

        this.activity = activity;
        mIssue_id = issue_id;
        mPurchaseReceipt = purchaseReceipt;
        mPurchaseSignature = purchaseSignature;
        mPurchasePrice = purchasePrice;
        mPurchaseCurrencyType = purchaseCurrencyType;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            cParser = new CreatePurchaseParser(getAPIResultData());

            ErrorMessage.canPurchaseResponse = getAPIResultData();
            Log.d(TAG,"Create Purchase response is : "+getAPIResultData());

            Log.d(TAG,"Create Purchase initJSONParse is : "+cParser.initJSONParse());

            Log.d(TAG,"Create Purchase is success is : "+cParser.isSuccess());
            if(cParser.initJSONParse()){

                if(cParser.isSuccess()){
                    cParser.parse();

                    if(cParser.transaction_Id != null) {
                        apiGetMyIssues = new GetMyIssues();
                        apiGetMyIssues.init();

                        apiGetMySubscription = new GetMySubscriptions();
                        apiGetMySubscription.init();
                    }

                    // Update the Issue view once purchase is success.
                    //Re-launching main activity once issue is purchased successfully.

                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);

                } else{

                    // Add error handling code here
                    ErrorMessage.hasError = true;

                    Log.d(TAG,"Get API Error Message "+cParser.getErrorMessage());
                    ErrorMessage.errorCode = cParser.getErrorCode();
                    ErrorMessage.errorMessage = cParser.getErrorMessage();
                }

            }
        }

    }
    private void setApiNameValuePairs()
    {
        Log.d(TAG,"Price when setting name value pair is : "+mPurchasePrice);

        String issueId = String.valueOf(mIssue_id);





        Log.v("auth_email_address =  ",UserPrefs.getUserEmail());
        Log.v("auth_password",UserPrefs.getUserPassword());
        Log.v("device_id",UserPrefs.getDeviceID());
        Log.v("magazine_id",String.valueOf(Config.Magazine_Number));
        Log.v("issue_id",issueId);
        Log.v("payment_gateway  ","google");
        Log.v("purchase_receipt =  ",String.valueOf(mPurchaseReceipt));
        Log.v("purchase_signature = ",String.valueOf(mPurchaseSignature));
        Log.v("purchase_price = ",String.valueOf(mPurchasePrice));
        Log.v("purchase_locale = ",mPurchaseCurrencyType);
        Log.v("app_bundle_id = ",Config.Bundle_ID);
        Log.v("api_mode = ",Config.api_mode);
        Log.v("api_version = ",Config.api_version);









        requestBody = new FormBody.Builder()
                .add("auth_email_address", UserPrefs.getUserEmail())
                .add("auth_password", UserPrefs.getUserPassword())
                .add("device_id", UserPrefs.getDeviceID())
                .add("magazine_id", Config.Magazine_Number)
                .add("issue_id", issueId)
                .add("payment_gateway", "google")
                .add("purchase_receipt", mPurchaseReceipt)
                .add("purchase_signature", mPurchaseSignature)
                .add("purchase_price", mPurchasePrice)
                .add("purchase_locale", mPurchaseCurrencyType)
                .add("app_bundle_id", Config.Bundle_ID)
                .add("api_mode", Config.api_mode)
                .add("api_version", Config.api_version)

                .build();





//        baseApiNameValuePairs = new ArrayList<NameValuePair>(13);
//        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", UserPrefs.getUserEmail()));
//        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", UserPrefs.getUserPassword()));
//        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", UserPrefs.getDeviceID()));
//        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", Config.Magazine_Number));
//        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", issueId));
//        baseApiNameValuePairs.add(new BasicNameValuePair("payment_gateway", "google"));
//        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_receipt", mPurchaseReceipt));
//        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_signature", mPurchaseSignature));
//        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_price", mPurchasePrice));
//        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_locale", mPurchaseCurrencyType));
//        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", Config.Bundle_ID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));

//        Log.d(TAG,"Base API JSON String for Create purchase is : "+baseApiNameValuePairs);
//
//        ErrorMessage.createPurchaseRequest = String.valueOf(baseApiNameValuePairs);

    }

}
