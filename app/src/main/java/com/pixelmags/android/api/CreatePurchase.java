package com.pixelmags.android.api;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.ErrorMessage;
import com.pixelmags.android.comms.WebRequest;
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
    CreatePurchaseParser cParser;
    private int mIssue_id;
    private String mPurchaseReceipt;
    private String mPurchaseSignature;
    private String purchasePrice;
    private String purchaseCurrencyType;
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
        this.purchasePrice = purchasePrice;
        this.purchaseCurrencyType = purchaseCurrencyType;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            cParser = new CreatePurchaseParser(getAPIResultData());

            ErrorMessage.canPurchaseResponse = getAPIResultData();
            Log.d(TAG,"Can Purchase response is : "+getAPIResultData());

            if(cParser.initJSONParse()){

                if(cParser.isSuccess()){
                    cParser.parse();

                } else{

                    // Add error handling code here
                    showAlertDialog(cParser.getErrorMessage());
                    Log.d(TAG,"Get API Error Message "+cParser.getErrorMessage());
                    ErrorMessage.hasError = true;
                    ErrorMessage.errorCode = cParser.getErrorCode();
                    ErrorMessage.errorMessage = cParser.getErrorMessage();
                }

            }
        }

    }
    private void setApiNameValuePairs()
    {
        String issueId = String.valueOf(mIssue_id);
        baseApiNameValuePairs = new ArrayList<NameValuePair>(13);
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", UserPrefs.getUserEmail()));
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", UserPrefs.getUserPassword()));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", UserPrefs.getDeviceID()));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", Config.Magazine_Number));
        baseApiNameValuePairs.add(new BasicNameValuePair("issue_id", issueId));
        baseApiNameValuePairs.add(new BasicNameValuePair("payment_gateway", "google"));
        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_receipt", mPurchaseReceipt));
        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_signature", mPurchaseSignature));
        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_price", purchasePrice));
        baseApiNameValuePairs.add(new BasicNameValuePair("purchase_locale", purchaseCurrencyType));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", Config.Bundle_ID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));

//        Log.d(TAG,"Base API JSON String for Create purchase is : "+baseApiNameValuePairs);
//
//        ErrorMessage.createPurchaseRequest = String.valueOf(baseApiNameValuePairs);

    }


    public void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                            activity.finishAffinity();
//                        } else {
//                            ActivityCompat.finishAffinity(activity);
//                        }
//                        System.exit(0);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
