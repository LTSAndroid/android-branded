package com.pixelmags.android.pixelmagsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.pixelmags.android.api.GetIssues;
import com.pixelmags.android.api.GetSubscriptions;
import com.pixelmags.android.api.ValidateUser;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;

public class LaunchActivity extends Activity {

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

        //apiGetIssues ::
        //apiGetSubscriptions ::



        PreLaunchAppTask() {

        }


        @Override
        protected String doInBackground(String... params) {

            String resultToDisplay = "";


            try {

                apiGetIssues = new GetIssues();
                apiGetIssues.init(Config.Magazine_Number, Config.Bundle_ID);

                apiGetSubs = new GetSubscriptions();
                apiGetSubs.init();


            }catch (Exception e){
                e.printStackTrace();
            }

            // check if user is logged in
            if(UserPrefs.getUserLoggedIn()){



                //Validate the user again
                apiValidateUser = new ValidateUser(UserPrefs.getUserEmail(), UserPrefs.getUserPassword());
                apiValidateUser.init();

                if(apiValidateUser.isSuccess()){

                        // Get MyIssues
                        // Get MySubscriptions

                }

            }


            return resultToDisplay;

        }
        protected void onPostExecute(String result) {

            launchMainActivity();

        }

    }

}

