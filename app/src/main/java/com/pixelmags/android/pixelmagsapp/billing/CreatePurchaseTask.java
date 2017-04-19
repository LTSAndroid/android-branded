package com.pixelmags.android.pixelmagsapp.billing;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.pixelmags.android.api.CreatePurchase;
import com.pixelmags.android.pixelmagsapp.MainActivity;


/**
 * Created by Annie on 20/02/2016.
 */
public class CreatePurchaseTask extends AsyncTask<String, String, String>
{

    private int mIssue_Id;
    private String mPurchaseToken;
    private String mPurchaseSignature;
    private String purchasePrice;
    private String purchaseCurrencyType;
    private Activity activity;


    private CreatePurchaseTask mCreatePurchaseTask = null;
    private String TAG = "CreatePurchaseTask";


    public CreatePurchaseTask(int issueId, String purchaseToken, String purchaseSignature, String purchasePrice, String purchaseCurrencyType,
                              Activity activity) {
        mIssue_Id = issueId;
        mPurchaseToken = purchaseToken;
        mPurchaseSignature = purchaseSignature;
        this.purchasePrice = purchasePrice;
        this.purchaseCurrencyType = purchaseCurrencyType;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params)
    {
        String resultToDisplay = "";


        try
        {
            CreatePurchase apiCreatePurchase = new CreatePurchase();
            apiCreatePurchase.init(mIssue_Id,mPurchaseToken,mPurchaseSignature,purchasePrice,purchaseCurrencyType,activity);

        }
        catch (Exception e){

        }
        return resultToDisplay;
    }

    protected void onPostExecute(String result) {

        mCreatePurchaseTask=null;
        //showProgress(false);

        Log.d(TAG,"Result of the create purchase API is : "+result);

        if (result != null) {
            System.out.println("API result :: " + result);

            // Update the Issue view once purchase is success.
            //Re-launching main activity once issue is purchased successfully.

            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);


        }
    else
        {
            Toast.makeText(activity,"Error occurred when calling create purchase",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onCancelled() {
        mCreatePurchaseTask = null;
    }

}
