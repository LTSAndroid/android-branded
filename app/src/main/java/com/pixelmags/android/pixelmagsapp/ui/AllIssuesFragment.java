package com.pixelmags.android.pixelmagsapp.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixelmags.android.api.CreateUser;
import com.pixelmags.android.api.GetIssues;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.json.GetIssuesParser;
import com.pixelmags.android.json.JSONParser;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.pixelmagsapp.test.ResultsFragment;
import com.pixelmags.android.storage.UserPrefs;


public class AllIssuesFragment extends Fragment {



    private GetAllIssuesTask mGetAllIssuesTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // retrieving the issues
        mGetAllIssuesTask = new GetAllIssuesTask(Config.Magazine_Number,Config.Bundle_ID);
        mGetAllIssuesTask.execute((String) null);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_issues, container, false);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetAllIssuesTask extends AsyncTask<String, String, String> {

        private final String mMagazineID;
        private final String mAppBundleID;

        GetAllIssuesTask(String MagazineID, String appBundleID) {
            mMagazineID = MagazineID;
            mAppBundleID = appBundleID;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String resultToDisplay = "";

            try{
                GetIssues apiGetIssues = new GetIssues();
                apiGetIssues.init(mMagazineID,mAppBundleID);
                resultToDisplay = apiGetIssues.getAPIResultData();


            }catch (Exception e){

            }
            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

            mGetAllIssuesTask = null;
            if (result != null) {
                System.out.println("API result :: " + result);
            }

            Fragment fragment = new ResultsFragment();
            Bundle args = new Bundle();
            args.putInt("Results Key", 1);
            args.putString("DISPLAY_RESULTS", result);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            // FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentManager.beginTransaction()
                    .replace(((ViewGroup) (getView().getParent())).getId(), fragment)
                    .addToBackStack(null)
                    .commit();

        }

        @Override
        protected void onCancelled() {
            mGetAllIssuesTask = null;
        }
    }
}
