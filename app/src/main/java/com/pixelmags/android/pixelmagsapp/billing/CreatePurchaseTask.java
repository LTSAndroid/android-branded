package com.pixelmags.android.pixelmagsapp.billing;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.pixelmags.android.api.CreatePurchase;
import com.pixelmags.android.comms.ErrorMessage;


/**
 * Created by Annie on 20/02/2016.
 */
public class CreatePurchaseTask extends AsyncTask<String, String, String>
{

    private int mIssue_Id;
    private String mPurchaseToken;
    private String mPurchaseSignature;
    private String mPurchasePrice;
    private String mPurchaseCurrencyType;
    private Activity activity;


    private CreatePurchaseTask mCreatePurchaseTask = null;
    private String TAG = "CreatePurchaseTask";


    public CreatePurchaseTask(int issueId, String purchaseToken, String purchaseSignature, String purchasePrice, String purchaseCurrencyType,
                              Activity activity) {
        mIssue_Id = issueId;
        mPurchaseToken = purchaseToken;
        mPurchaseSignature = purchaseSignature;
        mPurchasePrice = purchasePrice;
        mPurchaseCurrencyType = purchaseCurrencyType;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params)
    {
        String resultToDisplay = "";


        try
        {
            Log.d(TAG,"Purchase price when doing init is : "+mPurchasePrice);

            CreatePurchase apiCreatePurchase = new CreatePurchase();
            apiCreatePurchase.init(mIssue_Id,mPurchaseToken,mPurchaseSignature,mPurchasePrice,mPurchaseCurrencyType,activity);

        }
        catch (Exception e){

        }
        return resultToDisplay;
    }

    protected void onPostExecute(String result) {

        mCreatePurchaseTask=null;
        //showProgress(false);

        Log.d(TAG,"Has Error from API Response is : "+ ErrorMessage.hasError);

        if (ErrorMessage.hasError) {

//            Note :  Commented for testing

            // Update the Issue view once purchase is success.
            //Re-launching main activity once issue is purchased successfully.

//            Intent intent = new Intent(activity, MainActivity.class);
//            activity.startActivity(intent);
            showAlertDialog(ErrorMessage.errorMessage);

        }
    }

    @Override
    protected void onCancelled() {
        mCreatePurchaseTask = null;
    }

    public void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
