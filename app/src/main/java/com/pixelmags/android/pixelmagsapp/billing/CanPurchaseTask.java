package com.pixelmags.android.pixelmagsapp.billing;

import android.os.AsyncTask;
import com.pixelmags.android.api.CanPurchase;
import com.pixelmags.android.pixelmagsapp.MainActivity;

/**
 * Created by Annie on 20/02/2016.
 */
public class CanPurchaseTask extends AsyncTask<String, String, String> {

    public int mIssue_id;
    public String mSKU;
    private CanPurchaseTask mCanPurchaseTask = null;


    public CanPurchaseTask(String SKU , int issue_id) {
        mIssue_id = issue_id;
        mSKU = SKU;
    }

    @Override
    protected String doInBackground(String... params) {
        String resultToDisplay = "";

        try {
            CanPurchase apiCanPurchase = new CanPurchase();
            apiCanPurchase.init(mSKU,mIssue_id);

        } catch (Exception e) {

        }
        return resultToDisplay;
    }

    protected void onPostExecute(String result) {

        mCanPurchaseTask = null;

           /* for(int i=0; i< issuessArray.size();i++) {

                Magazine magazine = issuessArray.get(i);

            }*/




    }
    @Override
    protected void onCancelled() {
        mCanPurchaseTask = null;
    }
}

