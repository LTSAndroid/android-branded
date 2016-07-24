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
import com.pixelmags.android.util.GetInternetStatus;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class AllIssuesFragment extends Fragment {

    private ArrayList<Magazine> magazinesList = null;
    private GetAllIssuesTask mGetAllIssuesTask = null;
    private DownloadIssue downloadIssue = null;
    public CustomGridAdapter gridAdapter;
    private String TAG = "AllIssuesFragment";
    private MultiStateButton issuePriceButton;
    ArrayList<AllDownloadsIssueTracker> allDownloadsTracker;
    private String documentKey;
    private ProgressDialog progressBar;
    public static String currentPage;
    ProgressDialog progressDialog;
    ArrayList<IssueDocumentKey> issueDocumentKeys;

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
            myAct.canPurchaseLauncher(magazinesList.get(position).android_store_sku, magazinesList.get(position).id);
        }
        else
        {
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

            GetInternetStatus getInternetStatus = new GetInternetStatus(getActivity());
            if(getInternetStatus.isNetworkAvailable()){
                downloadIssue = new DownloadIssue(position);
                downloadIssue.execute((String) null);

            }else{
                getInternetStatus.showAlertDialog();
            }



            magazinesList.get(position).status = Magazine.STATUS_VIEW;
            gridAdapter.notifyDataSetChanged();


        }else{
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
    }

    public void saveDocumentKey(String issueId, String magazineNumber, String documentKey){


        try {
            // Save the Subscription Objects into the SQlite DB
            MyIssueDocumentKey mDbHelper = new MyIssueDocumentKey(BaseApp.getContext());
            Log.d(TAG,"mDbHelper reference is : "+mDbHelper);
            if (mDbHelper != null) {

                boolean isExists = mDbHelper.isTableExists(mDbHelper.getReadableDatabase(), BrandedSQLiteHelper.TABLE_DOCUMENT_KEY);

                Log.d(TAG,"Table exists is : "+isExists);
                String issueKeyFromTable = null;

                if(isExists){
                    issueKeyFromTable = getIssueDocumentKey(Integer.parseInt(issueId));
                }

                Log.d(TAG,"Issue Key from table is : "+issueKeyFromTable);
                if(issueKeyFromTable == null) {
                    mDbHelper.insert_my_issues_documentKey(mDbHelper.getWritableDatabase(), issueId, magazineNumber, documentKey, isExists);
                    mDbHelper.close();
                }else{
                    mDbHelper.close();
                }

            }else{
                mDbHelper.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public String getIssueDocumentKey(int issueId){

        String issueKey = null;

        MyIssueDocumentKey mDbReader = new MyIssueDocumentKey(BaseApp.getContext());
        if(mDbReader != null) {
            issueDocumentKeys = mDbReader.getMyIssuesDocumentKey(mDbReader.getReadableDatabase());
            mDbReader.close();
        }else{
            mDbReader.close();
        }

        for(int i=0; i<issueDocumentKeys.size(); i++){
            if(issueId == issueDocumentKeys.get(i).issueID){
                issueKey = issueDocumentKeys.get(i).documentKey;
            }
        }

        return issueKey;
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
        private LayoutInflater inflater;
        private ImageView imageView;


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

            View v;
//            if(convertView==null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.all_issues_custom_grid_layout, parent, false);

//            }else{
//                v = convertView;
//            }

            // Set the magazine image
            if(magazinesList.get(position).isThumbnailDownloaded) {

                imageView = (ImageView) v.findViewById(R.id.gridImage);
                Log.d(TAG,"Thumbnail Download is : "+magazinesList.get(position).isThumbnailDownloaded);
               // Bitmap bmp = loadImageFromStorage(magazinesList.get(position).thumbnailDownloadedInternalPath);

                if(magazinesList.get(position).thumbnailURL != null){

                        Picasso.with(mContext)
                                .load(magazinesList.get(position).thumbnailURL)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .into(imageView);


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
                TextView issueTitleText = (TextView) v.findViewById(R.id.gridTitleText);
                issueTitleText.setText(magazinesList.get(position).title);
            }


            issuePriceButton = (MultiStateButton) v.findViewById(R.id.gridMultiStateButton);

            // check if price/View/Download
            issuePriceButton.setButtonState(magazinesList.get(position));
    //            issuePriceButton.setAsPurchase(magazinesList.get(position).price);

            issuePriceButton.setTag(position); // save the gridview index
            issuePriceButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(magazinesList.get((Integer) v.getTag()).status == Magazine.STATUS_PRICE) {

                        gridPriceButtonClicked((Integer) v.getTag());

                    }else if(magazinesList.get((Integer) v.getTag()).status == Magazine.STATUS_QUEUE){

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Issue download is in queue!")
                                .setMessage("You can view your Issue once download start.")
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

                    }else if(magazinesList.get((Integer) v.getTag()).status == Magazine.STATUS_PAUSED){


                    }else if(magazinesList.get((Integer) v.getTag()).status == Magazine.STATUS_DOWNLOAD){

                        downloadButtonClicked((Integer) v.getTag());

                    }else if(magazinesList.get((Integer) v.getTag()).status == Magazine.STATUS_VIEW) {

                        int pos = (int) v.getTag();
                        String issueId = String.valueOf(magazinesList.get(pos).id);

                        documentKey = getIssueDocumentKey(magazinesList.get(pos).id);

                        Log.d(TAG,"Document key when view button click is : "+documentKey);

                        Intent intent = new Intent(getActivity(),NewIssueView.class);
                        intent.putExtra("issueId",issueId);
                        intent.putExtra("documentKey",documentKey);
                        startActivity(intent);

                    }

                }
            });

            return v;
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

        for(int i=0; i<magazinesList.size(); i++){
            Log.d(TAG,"Magazine thumbnailURL is : " +magazinesList.get(i).thumbnailURL);
            Log.d(TAG,"Magazine thumbnailBitmap is : " +magazinesList.get(i).thumbnailBitmap);
            if(magazinesList.get(i).paymentProvider.trim().equalsIgnoreCase("free")){
                magazinesList.get(i).status = Magazine.STATUS_DOWNLOAD;
            }
        }



        AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
        if(mDbReader != null) {

            boolean isExists = mDbReader.isTableExists(mDbReader.getReadableDatabase(), BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS);
            if(isExists) {
                allDownloadsTracker = mDbReader.getDownloadIssueList(mDbReader.getReadableDatabase(), Config.Magazine_Number);

                for(int j=0; j<magazinesList.size(); j++){
                    for(int k=0; k<allDownloadsTracker.size(); k++){
                        if(allDownloadsTracker.get(k).issueID == magazinesList.get(j).id){

                            if(allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_QUEUED){
                                magazinesList.get(j).status = Magazine.STATUS_QUEUE;
                            }else if(allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED){
                                magazinesList.get(j).status = Magazine.STATUS_VIEW;
                            }
                        }
                    }
                }

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
                    for(int issueCount=0; issueCount< myIssueArray.size(); issueCount++)
                    {
                        MyIssue issue = myIssueArray.get(issueCount);
                        if(issue.issueID == magazinesList.get(i).id){
                            magazinesList.get(i).isIssueOwnedByUser = true;

                            if(allDownloadsTracker == null){
                                magazinesList.get(i).status = Magazine.STATUS_DOWNLOAD;

                            }else{

                                for(int j=0; j<magazinesList.size(); j++){
                                    for(int k=0; k<allDownloadsTracker.size(); k++){
                                        if(allDownloadsTracker.get(k).issueID == magazinesList.get(j).id){
                                            if(allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_QUEUED){
                                                magazinesList.get(j).status = Magazine.STATUS_QUEUE;
                                            }else if(allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW){
                                                magazinesList.get(j).status = Magazine.STATUS_VIEW;
                                            }else if(allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW){
                                                magazinesList.get(j).status = Magazine.STATUS_VIEW;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if(allDownloadsTracker !=null){
                    for (int downloadCount=0; downloadCount < allDownloadsTracker.size() ; downloadCount++){

                        AllDownloadsIssueTracker singleDownload = allDownloadsTracker.get(downloadCount);
                        if(singleDownload.issueID == magazinesList.get(i).id){
                            magazinesList.get(i).currentDownloadStatus = singleDownload.downloadStatus;
                            if(magazinesList.get(i).currentDownloadStatus == 0){
                                magazinesList.get(i).status = Magazine.STATUS_VIEW;
                            }else if(magazinesList.get(i).currentDownloadStatus == 4){
                                magazinesList.get(i).status = Magazine.STATUS_QUEUE;
                            }else if(magazinesList.get(i).currentDownloadStatus == 1){
                                magazinesList.get(i).status = String.valueOf(AllDownloadsDataSet.DOWNLOAD_STATUS_IN_PROGRESS);
                            }else if(magazinesList.get(i).currentDownloadStatus == 3){
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



    public class DownloadIssue extends AsyncTask<String, String, String> {

        private int position;
        private String issueId;

        public DownloadIssue(int position){
            this.position = position;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            if (progressDialog != null) {
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressDialog.setContentView(R.layout.progress_dialog);
            }

        }


        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String resultToDisplay = "";

            try {

                issueId = String.valueOf(magazinesList.get(position).id);

                GetIssue getIssue = new GetIssue();
                getIssue.init(issueId);

                GetDocumentKey getDocumentKey = new GetDocumentKey();
                documentKey = getDocumentKey.init(UserPrefs.getUserEmail(), UserPrefs.getUserPassword(), UserPrefs.getDeviceID(),
                        issueId,Config.Magazine_Number, Config.Bundle_ID);

                Log.d(TAG, "Document key when download button clicked is : " + documentKey);

                if(documentKey != null){
                    Log.d(TAG,"Inside the document key not null");
                    saveDocumentKey(issueId, Config.Magazine_Number, documentKey.trim());
                }


            }catch (Exception e){
                e.printStackTrace();
            }
            return resultToDisplay;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            progressDialog.dismiss();



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


        }

        @Override
        protected void onCancelled() {
            mGetAllIssuesTask = null;
        }
    }




}
