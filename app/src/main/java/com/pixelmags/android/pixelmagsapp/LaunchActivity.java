package com.pixelmags.android.pixelmagsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.pixelmags.android.api.GetIssues;
import com.pixelmags.android.api.GetMyIssues;
import com.pixelmags.android.api.GetMySubscriptions;
import com.pixelmags.android.api.GetSubscriptions;
import com.pixelmags.android.api.ValidateUser;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.storage.UserPrefs;

import io.fabric.sdk.android.Fabric;

public class LaunchActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_launch);
        PreLaunchAppTask mPreLaunchTask = new PreLaunchAppTask();
        mPreLaunchTask.execute((String) null);

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
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String resultToDisplay = "";

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

                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }


            return resultToDisplay;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPreExecute();
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

    }
}

