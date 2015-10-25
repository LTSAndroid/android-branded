package com.pixelmags.android.pixelmagsapp.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.InputStream;
import java.util.ArrayList;


public class AllIssuesFragment extends Fragment {

    private ArrayList<Magazine> magazinesList = null;
    private GetAllIssuesTask mGetAllIssuesTask = null;
    public CustomGridAdapter gridAdapter;

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

        setGridAdapter(rootView);

        // Inflate the layout for this fragment
        return rootView;
    }


   public void setGridAdapter(View rootView){

       // set the Grid Adapter

       // use rootview to fetch view (when called from onCreateView) else null returns
       GridView gridview = (GridView) rootView.findViewById(R.id.displayIssuesGridView);
       gridAdapter = new CustomGridAdapter(getActivity());
       gridview.setAdapter(gridAdapter);
       //   gridview.setNumColumns(4);
   }


/**
 *  A custom GridView to display the Magazines.
 *
     */
    public class CustomGridAdapter extends BaseAdapter {

        private Context mContext;

        public CustomGridAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {

            if(magazinesList == null){
                return 0;
            }

            return magazinesList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return magazinesList.get(arg0).thumbnailBitmap;
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

            // Set the magazine image
            if(magazinesList.get(position).thumbnailBitmap != null) {
                ImageView imageView = (ImageView) grid.findViewById(R.id.gridImage);
                imageView.setImageBitmap(magazinesList.get(position).thumbnailBitmap);
                //imageView.setImageResource(mThumbIds[position]);
            }

            if(magazinesList.get(position).title != null) {
                TextView issueTitleText = (TextView) grid.findViewById(R.id.gridTitleText);
                issueTitleText.setText(magazinesList.get(position).title);
            }


            Button issuePriceButton = (Button) grid.findViewById(R.id.gridPriceButton);
            double price = magazinesList.get(position).price;
            String priceConv = String.valueOf(price);
            issuePriceButton.setText(priceConv);

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

        GetAllIssuesTask(String MagazineID, String appBundleID) {
            mMagazineID = MagazineID;
            mAppBundleID = appBundleID;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String resultToDisplay = "";

            try {

                GetIssues apiGetIssues = new GetIssues();
                apiGetIssues.init(mMagazineID, mAppBundleID);

            }catch (Exception e){
                    e.printStackTrace();
            }
            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

            mGetAllIssuesTask = null;

            loadAllIssues();
           /* for(int i=0; i< issuessArray.size();i++) {

                Magazine magazine = issuessArray.get(i);

            }*/


        }

        @Override
        protected void onCancelled() {
            mGetAllIssuesTask = null;
        }
    }


    public void loadAllIssues(){

        magazinesList = null; // clear the list

        AllIssuesDataSet mDbHelper = new AllIssuesDataSet(BaseApp.getContext());
        magazinesList = mDbHelper.getAllIssues(mDbHelper.getReadableDatabase());
        mDbHelper.close();


        if(magazinesList != null){


            for(int i=0; i< magazinesList.size();i++) {
                DownloadImageTask mDownloadTask = new DownloadImageTask(i);
                mDownloadTask.execute((String) null);
            }
        }
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        int index;
        //Bitmap bmImage;

        public DownloadImageTask(int i) {
            this.index = i;
        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = magazinesList.get(index).thumbnailURL;
            Bitmap imageBitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                magazinesList.get(index).thumbnailBitmap = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return magazinesList.get(index).thumbnailBitmap;
        }

        protected void onPostExecute(Bitmap result) {

            if(result!=null){
                System.out.println("Image downladed for Issue :: " + magazinesList.get(index).id);

                displayMagazineInGrid(index);
            }
           // bmImage.setImageBitmap(result);
        }
    }


    public void displayMagazineInGrid(int index){

        // update the Grid View Adapter here

        if(gridAdapter!=null){
            gridAdapter.notifyDataSetChanged();
        }

    }


 // end of class
}
