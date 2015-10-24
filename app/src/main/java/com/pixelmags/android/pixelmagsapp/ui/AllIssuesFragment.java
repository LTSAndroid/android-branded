package com.pixelmags.android.pixelmagsapp.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pixelmags.android.api.CreateUser;
import com.pixelmags.android.api.GetIssues;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.Subscription;
import com.pixelmags.android.json.GetIssuesParser;
import com.pixelmags.android.json.JSONParser;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.pixelmagsapp.test.ResultsFragment;
import com.pixelmags.android.storage.AllIssuesDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.BaseApp;

import java.util.ArrayList;


public class AllIssuesFragment extends Fragment {

    private Integer[] mThumbIds = {
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
            R.drawable.cast_ic_notification_0,
    };

    private GetAllIssuesTask mGetAllIssuesTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_display_issues, container, false);

        // retrieving the issues
        mGetAllIssuesTask = new GetAllIssuesTask(Config.Magazine_Number,Config.Bundle_ID);
        mGetAllIssuesTask.execute((String) null);


        GridView gridview = (GridView) rootView.findViewById(R.id.displayIssuesGridView);

        gridview.setAdapter(new MyAdapter(getActivity()));
        gridview.setNumColumns(4);


        // Inflate the layout for this fragment
        return rootView;
    }

/**
 *  A custom GridView to display the Magazines.
 *
     */
    public class MyAdapter extends BaseAdapter {

        private Context mContext;

        public MyAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int arg0) {
            return mThumbIds[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;

            if(convertView==null){

                grid = new View(mContext);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                grid=inflater.inflate(R.layout.all_issues_custom_grid_layout, parent, false);

            }else{
                grid = (View)convertView;
            }

            ImageView imageView = (ImageView)grid.findViewById(R.id.gridImage);
            imageView.setImageResource(mThumbIds[position]);

            return grid;
        }
    }





    /**
     *
     * Represents an asynchronous task used to fetch all the issues.
     *
     */
    public class GetAllIssuesTask extends AsyncTask<String, String, String> {

        private final String mMagazineID;
        private final String mAppBundleID;

        ArrayList<Magazine> issuessArray = null;

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

                AllIssuesDataSet mDbHelper = new AllIssuesDataSet(BaseApp.getContext());
                issuessArray = mDbHelper.getAllIssues(mDbHelper.getReadableDatabase());
                mDbHelper.close();


            }catch (Exception e){

            }
            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

            mGetAllIssuesTask = null;


           /* for(int i=0; i< issuessArray.size();i++) {

                Magazine magazine = issuessArray.get(i);

            }*/


        }

        @Override
        protected void onCancelled() {
            mGetAllIssuesTask = null;
        }

    }
}
