package com.pixelmags.android.pixelmagsapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.pixelmags.android.pixelmagsapp.LaunchActivity;
import com.pixelmags.android.pixelmagsapp.MainActivity;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.pixelmagsapp.test.ResultsFragment;
import com.pixelmags.android.storage.AllIssuesDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.BaseApp;
import com.pixelmags.android.util.IabHelper;
import com.pixelmags.android.util.IabResult;
import com.pixelmags.android.util.Purchase;

import java.io.File;
import java.io.FileInputStream;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


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

        // retrieving the issues - run inside a async task as there is db access required.
        mGetAllIssuesTask = new GetAllIssuesTask(Config.Magazine_Number,Config.Bundle_ID);
        mGetAllIssuesTask.execute((String) null);

        // loadAllIssues();

        setGridAdapter(rootView);

        // Inflate the layout for this fragment
        return rootView;
    }


   public void setGridAdapter(View rootView){

       // set the Grid Adapter

       // use rootview to fetch view (when called from onCreateView) else null returns
       GridView gridView = (GridView) rootView.findViewById(R.id.displayIssuesGridView);
       gridAdapter = new CustomGridAdapter(getActivity());
       gridView.setAdapter(gridAdapter);
       //   gridview.setNumColumns(4);

   }



    public void gridPriceButtonClicked(int position)
    {

        MainActivity myAct = (MainActivity) getActivity();
        myAct.purchaseLauncher(magazinesList.get(position).android_store_sku);

       /* AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.allIssues_purchase_title));
        String message = getString(R.string.allIssues_purchase_message)+ " "+magazinesList.get(position).title + "?";
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        builder.show();*/

    }

    public void gridIssueImageClicked(int position){

        navigateToIssueDetails(position);

    }

    private void navigateToIssueDetails(int position) {

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = IssueDetailsFragment.newInstance(magazinesList.get(position));
        fragmentManager.beginTransaction()
                .replace(((ViewGroup)(getView().getParent())).getId(), fragment)
                .addToBackStack(null)
                .commit();

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
            if(magazinesList.get(position).isThumbnailDownloaded) {

               // Bitmap bmp = loadImageFromStorage(magazinesList.get(position).thumbnailDownloadedInternalPath);

                if(magazinesList.get(position).thumbnailBitmap != null){
                    ImageView imageView = (ImageView) grid.findViewById(R.id.gridImage);
                    imageView.setImageBitmap(magazinesList.get(position).thumbnailBitmap);
                    //imageView.setImageBitmap(bmp);

                    imageView.setTag(position);
                    imageView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            gridIssueImageClicked((Integer) v.getTag());

                        }
                    });
                }

            }

            if(magazinesList.get(position).title != null) {
                TextView issueTitleText = (TextView) grid.findViewById(R.id.gridTitleText);
                issueTitleText.setText(magazinesList.get(position).title);
            }


            Button issuePriceButton = (Button) grid.findViewById(R.id.gridPriceButton);
            String price = magazinesList.get(position).price;
            String priceConv = String.valueOf(price);
            issuePriceButton.setText(priceConv);
            issuePriceButton.setTag(position); // save the gridview index
            issuePriceButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    gridPriceButtonClicked((Integer)v.getTag());

                }
            });

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

                //GetIssues apiGetIssues = new GetIssues();
               // apiGetIssues.init(mMagazineID, mAppBundleID);
                loadAllIssues(); //new change


            }catch (Exception e){
                    e.printStackTrace();
            }
            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

            mGetAllIssuesTask = null;

           /* for(int i=0; i< issuessArray.size();i++) {

                Magazine magazine = issuessArray.get(i);

            }*/

            if(gridAdapter!=null){
                gridAdapter.notifyDataSetChanged();
            }


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
               // DownloadImageTask mDownloadTask = new DownloadImageTask(i);
               // mDownloadTask.execute((String) null);
               if( magazinesList.get(i).isThumbnailDownloaded)
               {
                   magazinesList.get(i).thumbnailBitmap = loadImageFromStorage(magazinesList.get(i).thumbnailDownloadedInternalPath);
               }


            /*ArrayList<String> skuList = new ArrayList<String> ();

            for(int i=0; i< magazinesList.size();i++) {

                skuList.add(magazinesList.get(i).android_store_sku);
                DownloadImageTask mDownloadTask = new DownloadImageTask(i);
                mDownloadTask.execute((String) null);*/

            }

            /*Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);*/
        }


    }

    private Bitmap loadImageFromStorage(String path)
    {

        Bitmap issueThumbnail = null;
        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            issueThumbnail = BitmapFactory.decodeStream(inputStream);


            inputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return issueThumbnail;

    }



    /*private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

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
*/

    public void displayMagazineInGrid(int index){

        // update the Grid View Adapter here

        if(gridAdapter!=null){
            gridAdapter.notifyDataSetChanged();
        }

    }

 // end of class
}
