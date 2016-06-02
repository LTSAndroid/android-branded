package com.pixelmags.android.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixelmags.android.IssueView.NewIssueView;
import com.pixelmags.android.api.GetDocumentKey;
import com.pixelmags.android.api.GetIssue;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.IssueDocumentKey;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.MyIssue;
import com.pixelmags.android.pixelmagsapp.MainActivity;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.AllIssuesDataSet;
import com.pixelmags.android.storage.BrandedSQLiteHelper;
import com.pixelmags.android.storage.MyIssueDocumentKey;
import com.pixelmags.android.storage.MyIssuesDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.ui.uicomponents.MultiStateButton;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class AllIssuesFragment extends Fragment {

    private ArrayList<Magazine> magazinesList = null;
    private GetAllIssuesTask mGetAllIssuesTask = null;
    public CustomGridAdapter gridAdapter;
    private String TAG = "AllIssuesFragment";
    private MultiStateButton issuePriceButton;
    ArrayList<AllDownloadsIssueTracker> allDownloadsTracker;
    private String documentKey;
    ProgressDialog progressBar;
    public static String currentPage;

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

    public void updateButtonState(){

        gridAdapter = new CustomGridAdapter(getActivity());
        gridAdapter.notifyDataSetChanged();
    }



    public void gridPriceButtonClicked(int position)
    {
        //Launch Can Purchase, if user loggedin
        if(UserPrefs.getUserLoggedIn())
        {

            MainActivity myAct = (MainActivity) getActivity();

            Log.d(TAG,"Android store Sku is : " +magazinesList.get(position).android_store_sku);
            Log.d(TAG,"Magazine List Id is : " +magazinesList.get(position).id);

            myAct.canPurchaseLauncher(magazinesList.get(position).android_store_sku, magazinesList.get(position).id);
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(getString(R.string.purchase_initiation_fail_title));

            // set dialog message
            alertDialogBuilder
                    .setMessage(getString(R.string.download_initiation_fail_message))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.dismiss();

                            Fragment fragmentLogin = new LoginFragment();

                            // Insert the fragment by replacing any existing fragment
                            FragmentManager allIssuesFragmentManager = getFragmentManager();
                            allIssuesFragmentManager.beginTransaction()
                                    .replace(R.id.main_fragment_container, fragmentLogin)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        //check for download state before launch, prefer separate class as we need to reuse
        //if check passes then start the activity
       /* Intent intent = new Intent(getActivity(), NewIssueView.class);
        intent.putExtra("ISSUE_ID", 120974);
        startActivity(intent);*/
       // startActivity(new Intent(getActivity(), NewIssueView.class));
       /* NewIssueView issueViewFragment = (NewIssueView) getActivity();
        issueViewFragment.loadIssue(magazinesList.get(position).id);*/
       /* AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.allIssues_purchase_title));
        String message = getString(R.string.allIssues_purchase_message)+ " "+magazinesList.get(position).title + "?";
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        builder.show();*/

    }

    public void downloadButtonClicked(int position){
        if(UserPrefs.getUserLoggedIn()){

            progressBar = new ProgressDialog(getActivity());
            if (progressBar != null) {
                progressBar.show();
                progressBar.setCancelable(false);
                progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressBar.setContentView(R.layout.progress_dialog);
            }

            String issueId = String.valueOf(magazinesList.get(position).id);

            GetIssue getIssue = new GetIssue();
            getIssue.init(issueId);

            GetDocumentKey getDocumentKey = new GetDocumentKey();
            documentKey = getDocumentKey.init(UserPrefs.getUserEmail(), UserPrefs.getUserPassword(), UserPrefs.getDeviceID(),
                   issueId,Config.Magazine_Number, Config.Bundle_ID);

            saveDocumentKey(issueId,Config.Magazine_Number,documentKey.trim());

            magazinesList.get(position).status = Magazine.STATUS_VIEW;
            gridAdapter.notifyDataSetChanged();

            progressBar.dismiss();

            new AlertDialog.Builder(getActivity())
                    .setTitle("Issue Download!")
                    .setMessage("You can view your Issue in download section.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            currentPage =getString(R.string.menu_title_downloads);

                            Fragment fragmentDownload = new AllDownloadsFragment();
                            // Insert the fragment by replacing any existing fragment
                            FragmentManager allIssuesFragmentManager = getFragmentManager();
                            allIssuesFragmentManager.beginTransaction()
                                    .replace(R.id.main_fragment_container, fragmentDownload)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                            //       .addToBackStack(null)
                                    .commit();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(getString(R.string.purchase_initiation_fail_title));

            // set dialog message
            alertDialogBuilder
                    .setMessage(getString(R.string.purchase_initiation_fail_message))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.dismiss();

                            Fragment fragmentLogin = new LoginFragment();

                            // Insert the fragment by replacing any existing fragment
                            FragmentManager allIssuesFragmentManager = getFragmentManager();
                            allIssuesFragmentManager.beginTransaction()
                                    .replace(R.id.main_fragment_container, fragmentLogin)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void saveDocumentKey(String issueId, String magazineNumber, String documentKey){

        // Save the Subscription Objects into the SQlite DB
        MyIssueDocumentKey mDbHelper = new MyIssueDocumentKey(BaseApp.getContext());
        mDbHelper.insert_my_issues_documentKey(mDbHelper.getWritableDatabase(), issueId,magazineNumber,documentKey);
        mDbHelper.close();



    }


    public void gridIssueImageClicked(int position){

        navigateToIssueDetails(position);

    }

    private void navigateToIssueDetails(int position) {

        FragmentManager fragmentManager = getFragmentManager();

        Magazine mag = magazinesList.get(position);
        String issueID = String.valueOf(mag.id);

        Fragment fragment = IssueDetailsFragment.newInstance(issueID, Config.Magazine_Number);
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
        ArrayList<IssueDocumentKey> issueDocumentKeys;


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


            issuePriceButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);

            // check if price/View/Download
            issuePriceButton.setButtonState(magazinesList.get(position));
    //            issuePriceButton.setAsPurchase(magazinesList.get(position).price);

            issuePriceButton.setTag(position); // save the gridview index
            issuePriceButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(magazinesList.get((Integer) v.getTag()).status == Magazine.STATUS_PRICE) {

                        Log.d(TAG,"Price Button clicked");

                        gridPriceButtonClicked((Integer) v.getTag());

                    }else if(magazinesList.get((Integer) v.getTag()).status == Magazine.STATUS_DOWNLOAD){

                        Log.d(TAG,"Download Button clicked");

                        downloadButtonClicked((Integer) v.getTag());

                    }else if(magazinesList.get((Integer) v.getTag()).status == Magazine.STATUS_VIEW) {

                        int pos = (int) v.getTag();
                        String issueId = String.valueOf(magazinesList.get(pos).id);

                        documentKey = getIssueDocumentKey(magazinesList.get(pos).id);

                        Log.d(TAG,"Document Key is : " +documentKey);

                        Intent intent = new Intent(getActivity(),NewIssueView.class);
                        intent.putExtra("issueId",issueId);
                        intent.putExtra("documentKey",documentKey);
                        startActivity(intent);

                    }

                }
            });

            return grid;
        }


    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    public String getIssueDocumentKey(int issueId){

        String issueKey = null;

        MyIssueDocumentKey mDbReader = new MyIssueDocumentKey(BaseApp.getContext());
        if(mDbReader != null) {
            issueDocumentKeys = mDbReader.getMyIssuesDocumentKey(mDbReader.getReadableDatabase());
            mDbReader.close();
        }

        for(int i=0; i<issueDocumentKeys.size(); i++){
            if(issueId == issueDocumentKeys.get(i).issueID){
                issueKey = issueDocumentKeys.get(i).documentKey;
            }
        }

        return issueKey;
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
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                progressBar = new ProgressDialog(getActivity());
                if (progressBar != null) {
                    progressBar.show();
                    progressBar.setCancelable(false);
                    progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    progressBar.setContentView(R.layout.progress_dialog);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

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

        @Override
        protected void onPostExecute(String result) {
        super.onPostExecute(result);
            mGetAllIssuesTask = null;
            progressBar.dismiss();

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

        Log.d(TAG, "Magazine List is : " + magazinesList);
        Log.d(TAG,"Magazine List size is : " +magazinesList.size());

        for(int i=0; i<magazinesList.size(); i++){
            Log.d(TAG,"Magazine id is : " +magazinesList.get(i).id);
            Log.d(TAG,"Magazine Status is : " +magazinesList.get(i).status);
            Log.d(TAG,"Magazine price is : " +magazinesList.get(i).price);
            Log.d(TAG,"Magazine Synopsis is : " +magazinesList.get(i).synopsis);
            Log.d(TAG,"Magazine thumbnailURL is : " +magazinesList.get(i).thumbnailURL);
            Log.d(TAG,"Magazine thumbnailBitmap is : " +magazinesList.get(i).thumbnailBitmap);
            Log.d(TAG,"Magazine Type is : " +magazinesList.get(i).type);
            Log.d(TAG,"Magazine currentDownloadStatus is : " +magazinesList.get(i).currentDownloadStatus);
            Log.d(TAG,"Magazine issueDate is : " +magazinesList.get(i).issueDate);
            Log.d(TAG,"Magazine ageRestriction is : " +magazinesList.get(i).ageRestriction);
            Log.d(TAG,"Magazine state is : " +magazinesList.get(i).state);
            if(magazinesList.get(i).paymentProvider.trim().equalsIgnoreCase("free")){
                Log.d(TAG,"Entered the magazine List Payment Provider condition");
                magazinesList.get(i).status = Magazine.STATUS_DOWNLOAD;
            }
        }



        AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
        if(mDbReader != null) {

            boolean isExists = mDbReader.isTableExists(mDbReader.getReadableDatabase(), BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS);
            Log.d(TAG,"All Download Table exists is : "+isExists);
            if(isExists) {
                allDownloadsTracker = mDbReader.getDownloadIssueList(mDbReader.getReadableDatabase(), Config.Magazine_Number);
                mDbReader.close();
            }else{
                mDbReader.close();
            }
        }


        // retrieve any user issues
        ArrayList<MyIssue> myIssueArray = null;
        if(UserPrefs.getUserLoggedIn())
        {
            MyIssuesDataSet myIssuesDbReader = new MyIssuesDataSet(BaseApp.getContext());
            myIssueArray = myIssuesDbReader.getMyIssues(myIssuesDbReader.getReadableDatabase());
            myIssuesDbReader.close();


            Log.d(TAG, "My Magazine List is : " + myIssueArray);
            Log.d(TAG, "My Magazine List size is : " + myIssueArray.size());

            for(int i=0; i<myIssueArray.size(); i++){
                Log.d(TAG,"My Magazine id is : " +myIssueArray.get(i).magazineID);
                Log.d(TAG,"My Magazine Status is : " +myIssueArray.get(i).issueID);
                Log.d(TAG,"My Magazine price is : " +myIssueArray.get(i).removeFromSale);
            }


        }


        if(magazinesList != null){

            for(int i=0; i< magazinesList.size();i++) {
               // DownloadImageTask mDownloadTask = new DownloadImageTask(i);
               // mDownloadTask.execute((String) null);
                Log.d(TAG,"Magazine thumbnail Download : " + magazinesList.get(i).isThumbnailDownloaded);
               if( magazinesList.get(i).isThumbnailDownloaded)
               {
                   Log.d(TAG,"Magazine thumbnail Download Internal Path is : " + magazinesList.get(i).thumbnailDownloadedInternalPath);
                   magazinesList.get(i).thumbnailBitmap = loadImageFromStorage(magazinesList.get(i).thumbnailDownloadedInternalPath);
               }

                // update the issue owned field
                // check if the issue is already owned by user
                if(myIssueArray != null){
                    Log.d(TAG,"My Issue size is : "+myIssueArray.size());
                    for(int issueCount=0; issueCount< myIssueArray.size(); issueCount++)
                    {
                        MyIssue issue = myIssueArray.get(issueCount);
                        Log.d(TAG,"My Issue is : "+issue.issueID);
                        Log.d(TAG,"Magazine List is : "+magazinesList.get(i).id);
                        if(issue.issueID == magazinesList.get(i).id){
                            Log.d(TAG,"Inside the first if condition matched of issue id");
                            magazinesList.get(i).isIssueOwnedByUser = true;

                            if(allDownloadsTracker == null){

                                Log.d(TAG,"Size of all downloaded issue is : NULL");
                                magazinesList.get(i).status = Magazine.STATUS_DOWNLOAD;

                            }else{

                                Log.d(TAG,"Size of all downloaded issue is : " +allDownloadsTracker.size());

                                Log.d(TAG,"Inside the else condition of all download issue tracker");
                                Log.d(TAG,"All Download Tracker size is : " +allDownloadsTracker.size());
                                for(int k=0 ; k<allDownloadsTracker.size(); k++){
                                    if(magazinesList.get(i).id == allDownloadsTracker.get(k).issueID){
                                        Log.d(TAG,"Inside the if condition of all download tracker");
                                        magazinesList.get(i).status = Magazine.STATUS_VIEW;
                                    }
                                }
                            }
                        }
                    }
                }

                if(allDownloadsTracker !=null){
                    for (int downloadCount=0; downloadCount < allDownloadsTracker.size() ; downloadCount++){

                        AllDownloadsIssueTracker singleDownload = allDownloadsTracker.get(downloadCount);
                        Log.d(TAG," Single Download is : "+singleDownload);
                        if(singleDownload.issueID == magazinesList.get(i).id){
                            magazinesList.get(i).currentDownloadStatus = singleDownload.downloadStatus;
                            Log.d(TAG," singleDownload.downloadStatus is : "+magazinesList.get(i).currentDownloadStatus);
                            if(magazinesList.get(i).currentDownloadStatus == 0){
                                magazinesList.get(i).status = Magazine.STATUS_VIEW;
                            }
                        }
                    }
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

    public void displayMagazineInGrid(int index){

        // update the Grid View Adapter here

        if(gridAdapter!=null){
            gridAdapter.notifyDataSetChanged();
        }

    }


}
