package com.pixelmags.android.pixelmagsapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.pixelmags.android.api.GetIssue;
import com.pixelmags.android.api.GetIssues;
import com.pixelmags.android.api.GetMyIssues;
import com.pixelmags.android.api.GetMySubscriptions;
import com.pixelmags.android.api.GetSubscriptions;
import com.pixelmags.android.api.ValidateUser;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.IabHelper;
import com.pixelmags.android.util.IabResult;

public class LaunchActivity extends Activity {


    private static final String TAG = "<your domain>.inappbilling";
    public static IabHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_launch);
        PreLaunchAppTask mPreLaunchTask = new PreLaunchAppTask();
        mPreLaunchTask.execute((String) null);

        // setting a sleep of 3 sec so that the splash screen is shown for at least that time.
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{

                }
            }
        };
        timerThread.start();

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhE2tqqq+WSoEXHyqOdeFjKFGgWVuhapArdTe/b0wzxAJE0pdsM8FlywwyIQlLd51hj6vvDkmd8T3dRi6LX2Ww2M8+fpK7jP3ydMyTZB9efuAiRZq2tlo2GmrFmO0vTdD0MkY4OdX9ROEvY9k/cbzXX73uNH0FAcZ38ypr/qf66IS2yI+z+Oiip7c39pDrG0P4kVamJQOjs7PLTmtwU1PWc43phqISxxpLJWxj0yW/YjfZ7Knk5n84p02CpDJcoZXdsBu7X4GOc79DRURDHuLu3tgkp3roXTQeX6y4Ht9843Hu5rSRgADQ/5828+SozdhIAhQ4CT/MZ0w0NEd0/OitwIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               //failed
                                           } else {
                                               //success
                                           }
                                       }
                                   });


       //Configurations.getInstance().getGooglePlayLicenseKey();
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    /*Utilities.log("Unable to setup billing: " + result);
                    isBillingSetup = false;*/
                } else {
                    /*Utilities.log("Billing setup successfully");
                    isBillingSetup = true;*/
                }
            }
        });
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }


    private void launchMainActivity(){

        Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
        startActivity(intent);

    }

    private class PreLaunchAppTask extends AsyncTask<String, String, String> {


        GetIssues apiGetIssues;
        GetSubscriptions apiGetSubs;
        ValidateUser apiValidateUser;
        GetMyIssues apiGetMyIssues;
        GetMySubscriptions apiGetMySubscriptions;


        //apiGetIssues ::
        //apiGetSubscriptions ::



        PreLaunchAppTask() {

        }


        @Override
        protected String doInBackground(String... params) {

            String resultToDisplay = "";


            //System.out.println("USER LOGGED IN :"+ UserPrefs.getUserLoggedIn());
            //System.out.println("USER Email STORED:" + UserPrefs.getUserEmail());

            //Phase 1 - Get All issues and Subs for app
            try {

                apiGetIssues = new GetIssues();
                apiGetIssues.init(Config.Magazine_Number, Config.Bundle_ID);

                apiGetSubs = new GetSubscriptions();
                apiGetSubs.init();


            }catch (Exception e){
                e.printStackTrace();
            }



            // Phase 2
            // check if user is logged in


            try{

                if(UserPrefs.getUserLoggedIn()){

                    //Validate the user again
                    apiValidateUser = new ValidateUser(UserPrefs.getUserEmail(), UserPrefs.getUserPassword());
                    apiValidateUser.init();

                    System.out.println("apiValidateUser success  - " + apiValidateUser.isSuccess());

                    if(apiValidateUser.isSuccess()){

                        // Get MyIssues
                        apiGetMyIssues = new GetMyIssues();
                        apiGetMyIssues.init();

                        // Get MySubscriptions
                        apiGetMySubscriptions = new GetMySubscriptions();
                        apiGetMySubscriptions.init();

                        // testing Get Issue and parse
                        GetIssue issueFetch = new GetIssue();
                        issueFetch.init("110422");



                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }


            return resultToDisplay;

        }
        protected void onPostExecute(String result) {

            launchMainActivity();

        }

    }
/**
 094.
 * Very important!
 095.
 */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }
}

