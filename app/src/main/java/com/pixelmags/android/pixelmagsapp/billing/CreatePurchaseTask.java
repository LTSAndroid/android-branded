package com.pixelmags.android.pixelmagsapp.billing;

import android.os.AsyncTask;
import com.pixelmags.android.api.CreatePurchase;


/**
 * Created by Annie on 20/02/2016.
 */
public class CreatePurchaseTask extends AsyncTask<String, String, String>
{

    private int mIssue_Id;
    private String mPurchaseToken;
    private String mPurchaseSignature;

    private CreatePurchaseTask mCreatePurchaseTask = null;



    public CreatePurchaseTask(int issueId, String purchaseToken, String purchaseSignature) {
        mIssue_Id = issueId;
        mPurchaseToken = purchaseToken;
        mPurchaseSignature = purchaseSignature;
    }

    @Override
    protected String doInBackground(String... params)
    {
        String resultToDisplay = "";


        try
        {
            CreatePurchase apiCreatePurchase = new CreatePurchase();
            apiCreatePurchase.init(mIssue_Id,mPurchaseToken,mPurchaseSignature);

        }
        catch (Exception e){

        }
        return resultToDisplay;
    }

    protected void onPostExecute(String result) {

        mCreatePurchaseTask=null;
        //showProgress(false);

        if (result != null) {
            System.out.println("API result :: " + result);
        }
    else
        {


        }
    }

    @Override
    protected void onCancelled() {
        mCreatePurchaseTask = null;
    }

}
