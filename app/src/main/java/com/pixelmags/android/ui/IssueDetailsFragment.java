package com.pixelmags.android.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pixelmags.android.IssueView.NewIssueView;
import com.pixelmags.android.api.GetDocumentKey;
import com.pixelmags.android.api.GetIssue;
import com.pixelmags.android.api.GetPreviewImages;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.IssueDocumentKey;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.MyIssue;
import com.pixelmags.android.datamodels.PreviewImage;
import com.pixelmags.android.download.DownloadPreviewImages;
import com.pixelmags.android.download.QueueDownload;
import com.pixelmags.android.pixelmagsapp.MainActivity;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.AllIssuesDataSet;
import com.pixelmags.android.storage.MyIssueDocumentKey;
import com.pixelmags.android.storage.MyIssuesDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.ui.uicomponents.MultiStateButton;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class IssueDetailsFragment extends Fragment {


    public Magazine issueData;
    private static final String SERIALIZABLE_MAG_KEY = "serializable_mag_key";
    private static final String ISSUE_ID_KEY = "issue_id_key";
    private static final String MAGAZINE_ID_KEY = "magazine_id_key";

    private DownloadIssueAsyncTask mIssueTask = null;

    LinearLayout previewImagesLayout;
    private DownloadPreviewImagesAsyncTask mPreviewImagesTask = null;
    private LoadIssueAsyncTask mLoadIssueTask = null;

    private String mIssueID;
    private String mMagazineID;

    ImageView issueDetailsImageView;
    TextView issueDetailsTitle;
    TextView issueDetailsSynopsis;
    MultiStateButton issueDetailsPriceButton;
    private String TAG = "IssueDetailFragments";
    private String documentKey;
    ProgressDialog progressBar;
    ArrayList<IssueDocumentKey> issueDocumentKeys;
    ArrayList<AllDownloadsIssueTracker> allDownloadsTracker;


    public IssueDetailsFragment() {
        // Required empty public constructor
    }

    public static IssueDetailsFragment newInstance(String issueID, String magazineID) {

        IssueDetailsFragment fragment = new IssueDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ISSUE_ID_KEY, issueID);
        args.putString(MAGAZINE_ID_KEY, magazineID);
       // args.putSerializable(SERIALIZABLE_MAG_KEY, magazineData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
        if (getArguments() != null) {

            mIssueID = (String) getArguments().getString(ISSUE_ID_KEY);
            mMagazineID = (String) getArguments().getString(MAGAZINE_ID_KEY);

            //issueData = (Magazine) getArguments().getSerializable(SERIALIZABLE_MAG_KEY);
        }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_issue_details, container, false);
        previewImagesLayout = (LinearLayout) rootView.findViewById(R.id.issueDetailsPreviewImageLayout);


        issueDetailsImageView = (ImageView) rootView.findViewById(R.id.issueDetailsImageView);
        issueDetailsTitle = (TextView) rootView.findViewById(R.id.issueDetailsTitle);
        issueDetailsSynopsis = (TextView) rootView.findViewById(R.id.issueDetailsSynopsis);
        issueDetailsPriceButton = (MultiStateButton) rootView.findViewById(R.id.issueDetailsPriceButton);

        mLoadIssueTask = new LoadIssueAsyncTask(mMagazineID, mIssueID);
        mLoadIssueTask.execute((String) null);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        // mLoadIssueTask = new LoadIssueAsyncTask(mMagazineID, mIssueID);
        // mLoadIssueTask.execute((String) null);

    }

    public void loadIssueData(){

        if(issueData != null) {

            // Load all data for the issue details page here
            if (issueData.isThumbnailDownloaded) {
                if(issueData.thumbnailBitmap != null && issueDetailsImageView !=null ){
                    issueDetailsImageView.setImageBitmap(issueData.thumbnailBitmap);
                }
            }


            if(issueDetailsTitle != null) {
                issueDetailsTitle.setText(issueData.title);
            }

            if(issueDetailsSynopsis != null) {
                issueDetailsSynopsis.setText(issueData.synopsis);
            }

            if(issueDetailsPriceButton != null){


//                issueDetailsPriceButton.setText(String.valueOf(issueData.price));
//
//                issueDetailsPriceButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        //Launch Can Purchase, if user loggedin
//                        if(UserPrefs.getUserLoggedIn())
//                        {
//                            MainActivity myAct = (MainActivity) getActivity();
//                            myAct.canPurchaseLauncher(issueData.android_store_sku,issueData.id);
//                        }
//                        else
//                        {
//                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                            alertDialogBuilder.setTitle(getString(R.string.purchase_initiation_fail_title));
//
//                            // set dialog message
//                            alertDialogBuilder
//                                    .setMessage(getString(R.string.purchase_initiation_fail_message))
//                                    .setCancelable(false)
//                                    .setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog,int id) {
//                                            dialog.dismiss();
//
//                                            Fragment fragmentLogin = new LoginFragment();
//
//                                            // Insert the fragment by replacing any existing fragment
//                                            FragmentManager allIssuesFragmentManager = getFragmentManager();
//                                            allIssuesFragmentManager.beginTransaction()
//                                                    .replace(R.id.main_fragment_container, fragmentLogin)
//                                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                                                    .commit();
//                                        }
//                                    });
//                            AlertDialog alertDialog = alertDialogBuilder.create();
//                            alertDialog.show();
//                        }
//
//                       /* mIssueTask = new DownloadIssueAsyncTask(Config.Magazine_Number, "120974");
//                        mIssueTask.execute((String) null);*/
//                    }
//                });

                Log.d(TAG," Issue Data is : " +issueData.status);
                issueDetailsPriceButton.setButtonState(issueData);
                issueDetailsPriceButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if(issueData.status == Magazine.STATUS_PRICE) {

                            Log.d(TAG, "Price Button clicked");

                            priceButtonClicked();

                        }else if(issueData.status == Magazine.STATUS_DOWNLOAD){

                            Log.d(TAG,"Download Button clicked");

                            downloadButtonClicked();

                        }else if(issueData.status == Magazine.STATUS_VIEW) {

                            int pos = (int) v.getTag();
                            String issueId = String.valueOf(issueData.id);

                            documentKey = getIssueDocumentKey(issueData.id);

                            Log.d(TAG,"Document Key is : " +documentKey);

                            Intent intent = new Intent(getActivity(),NewIssueView.class);
                            intent.putExtra("issueId",issueId);
                            intent.putExtra("documentKey",documentKey);
                            startActivity(intent);

                        }

                    }
                });



            }

            loadPreviewImages();

        }


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

    public void priceButtonClicked()
    {
        //Launch Can Purchase, if user loggedin
        if(UserPrefs.getUserLoggedIn())
        {

            MainActivity myAct = (MainActivity) getActivity();

            Log.d(TAG,"Magazine List Id is : " +issueData.id);

            myAct.canPurchaseLauncher(issueData.android_store_sku, issueData.id);
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

    }

    public void downloadButtonClicked(){
        if(UserPrefs.getUserLoggedIn()){

            progressBar = new ProgressDialog(getActivity());
            if (progressBar != null) {
                progressBar.show();
                progressBar.setCancelable(false);
                progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressBar.setContentView(R.layout.progress_dialog);
            }

            String issueId = String.valueOf(issueData.id);

            GetIssue getIssue = new GetIssue();
            getIssue.init(issueId);

            GetDocumentKey getDocumentKey = new GetDocumentKey();
            documentKey = getDocumentKey.init(UserPrefs.getUserEmail(), UserPrefs.getUserPassword(), UserPrefs.getDeviceID(),
                    issueId,Config.Magazine_Number, Config.Bundle_ID);

            saveDocumentKey(issueId,Config.Magazine_Number,documentKey.trim());

            issueData.status = Magazine.STATUS_VIEW;
            issueDetailsPriceButton.setText(BaseApp.getContext().getString(R.string.view));

            progressBar.dismiss();

            new AlertDialog.Builder(getActivity())
                    .setTitle("Issue Download!")
                    .setMessage("You can view your Issue in download section.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
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
    }

    public void saveDocumentKey(String issueId, String magazineNumber, String documentKey){

        // Save the Subscription Objects into the SQlite DB
        MyIssueDocumentKey mDbHelper = new MyIssueDocumentKey(BaseApp.getContext());
        mDbHelper.insert_my_issues_documentKey(mDbHelper.getWritableDatabase(), issueId,magazineNumber,documentKey);
        mDbHelper.close();



    }


    public void loadPreviewImages(){

        mPreviewImagesTask = new DownloadPreviewImagesAsyncTask(Config.Magazine_Number, String.valueOf(issueData.id));
        mPreviewImagesTask.execute((String) null);

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }


    private boolean startIssueDownload(String issueId){

        QueueDownload queueIssue = new QueueDownload();
        return queueIssue.insertIssueInDownloadQueue(issueId);

    }

    /**
     *
     * Represents an asynchronous task used to load the issues.
     *
     */
    public class LoadIssueAsyncTask extends AsyncTask<String, String, Boolean> {

        private final String mIssueID;

        LoadIssueAsyncTask(String magID, String issueID) {
            mIssueID = issueID;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            try {

                AllIssuesDataSet mDbHelper = new AllIssuesDataSet(BaseApp.getContext());
                issueData = mDbHelper.getSingleIssue(mDbHelper.getReadableDatabase(),mIssueID);
                mDbHelper.close();

                loadMyIssue();


                if(issueData != null) {

                    // Load the issue Image here, as it is better to do it in the background
                    if (issueData.isThumbnailDownloaded) {
                        issueData.thumbnailBitmap = loadImageFromStorage(issueData.thumbnailDownloadedInternalPath);
                    }
                }

                return true;

            }catch (Exception e){
                e.printStackTrace();
            }

            return false;

        }

        protected void onPostExecute(Boolean result) {

            if(result)
            {
                loadIssueData();

            }

        }

        @Override
        protected void onCancelled() {
            mLoadIssueTask = null;
        }
    }

    public void loadMyIssue(){

        AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
        if(mDbReader != null) {
            allDownloadsTracker = mDbReader.getDownloadIssueList(mDbReader.getReadableDatabase(), Config.Magazine_Number);
            mDbReader.close();
        }

        // retrieve any user issues
        ArrayList<MyIssue> myIssueArray = null;
        if(UserPrefs.getUserLoggedIn())
        {
            MyIssuesDataSet myIssuesDbReader = new MyIssuesDataSet(BaseApp.getContext());
            myIssueArray = myIssuesDbReader.getMyIssues(myIssuesDbReader.getReadableDatabase());
            myIssuesDbReader.close();

        }

        if(issueData != null){

            // update the issue owned field
            // check if the issue is already owned by user
            if (myIssueArray != null) {
                Log.d(TAG, "My Issue size is : " + myIssueArray.size());
                for (int issueCount = 0; issueCount < myIssueArray.size(); issueCount++) {
                    MyIssue issue = myIssueArray.get(issueCount);
                    if (issue.issueID == issueData.id) {
                        Log.d(TAG, "Inside the first if condition matched of issue id");
                        issueData.isIssueOwnedByUser = true;

                        if (allDownloadsTracker == null) {

                            Log.d(TAG, "Size of all downloaded issue is : NULL");
                            issueData.status = Magazine.STATUS_DOWNLOAD;

                        } else {

                            Log.d(TAG, "Size of all downloaded issue is : " + allDownloadsTracker.size());

                            Log.d(TAG, "Inside the else condition of all download issue tracker");
                            Log.d(TAG, "All Download Tracker size is : " + allDownloadsTracker.size());
                            for (int k = 0; k < allDownloadsTracker.size(); k++) {
                                if (issueData.id == allDownloadsTracker.get(k).issueID) {
                                    Log.d(TAG, "Inside the if condition of all download tracker");
                                    issueData.status = Magazine.STATUS_VIEW;
                                }
                            }
                        }
                    }
                }
            }

            if (allDownloadsTracker != null) {
                for (int downloadCount = 0; downloadCount < allDownloadsTracker.size(); downloadCount++) {

                    AllDownloadsIssueTracker singleDownload = allDownloadsTracker.get(downloadCount);
                    Log.d(TAG, " Single Download is : " + singleDownload);
                    if (singleDownload.issueID == issueData.id) {
                        issueData.currentDownloadStatus = singleDownload.downloadStatus;
                        Log.d(TAG, " singleDownload.downloadStatus is : " + issueData.currentDownloadStatus);
                        if (issueData.currentDownloadStatus == 0) {
                            issueData.status = Magazine.STATUS_VIEW;
                        }
                    }
                }
            }
        }



    }






    /**
     *
     * Represents an asynchronous task used to download an issues.
     *
     */
    public class DownloadIssueAsyncTask extends AsyncTask<String, String, Boolean> {

        private final String mIssueID;

        DownloadIssueAsyncTask(String magID, String issueID) {
            mIssueID = issueID;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            try {

                //Fetch Issue Details as well.
                GetIssue issueFetch = new GetIssue();
                issueFetch.init(mIssueID);

                boolean result = startIssueDownload(mIssueID);

                if(result){
                    MainActivity mActivity = (MainActivity) getActivity();
                    mActivity.notifyServiceOfNewDownload();
                }

                return result;

            }catch (Exception e){
                e.printStackTrace();
            }

            return false;

        }

        protected void onPostExecute(Boolean result) {

            if(result)
            {
                Toast toast = Toast.makeText(BaseApp.getContext(), BaseApp.getContext().getString(R.string.download_queued), Toast.LENGTH_SHORT);
                toast.show();

            }else{
                Toast toast = Toast.makeText(BaseApp.getContext(), BaseApp.getContext().getString(R.string.download_queued_fail), Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        @Override
        protected void onCancelled() {
            mIssueTask = null;
        }
    }

    /**
     *
     * Represents an asynchronous task used to download an issues.
     *
     */
    public class DownloadPreviewImagesAsyncTask extends AsyncTask<String, String, String> {

        private final String mIssueID;
        ArrayList<PreviewImage> previewImageArrayList;

        DownloadPreviewImagesAsyncTask(String magID, String issueID) {
            mIssueID = issueID;
            previewImageArrayList = null;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String resultToDisplay = "";

            try {

                GetPreviewImages getPreviewImages = new GetPreviewImages();
                previewImageArrayList = getPreviewImages.init(mIssueID, Config.Bundle_ID);

                if(previewImageArrayList != null){
                    previewImageArrayList = DownloadPreviewImages.DownloadPreviewImageBitmaps(previewImageArrayList);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

            try{
                previewImagesLayout = (LinearLayout) getActivity().findViewById(R.id.issueDetailsPreviewImageLayout);

                // start async task to download and update
                if(previewImageArrayList != null){
                    for (int i = 0; i < previewImageArrayList.size() && i < DownloadPreviewImages.MAX_PREVIEW_IMAGE_COUNT; i++){

                        PreviewImage previewImage = previewImageArrayList.get(i);

                        if(previewImage.previewImageBitmap != null) {
                            ImageView imageView = new ImageView(getActivity());
                            imageView.setId(i);
                            imageView.setPadding(2, 2, 5, 2);
                            imageView.setMinimumWidth(previewImage.imageWidth);
                            imageView.setImageBitmap(previewImage.previewImageBitmap);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                            previewImagesLayout.addView(imageView);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }

        @Override
        protected void onCancelled() {
            mIssueTask = null;
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

}
