package com.pixelmags.android.pixelmagsapp.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pixelmags.android.api.GetIssue;
import com.pixelmags.android.api.GetPreviewImages;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.PreviewImage;
import com.pixelmags.android.download.DownloadPreviewImages;
import com.pixelmags.android.download.QueueDownload;
import com.pixelmags.android.pixelmagsapp.MainActivity;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.AllIssuesDataSet;
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
    Button issueDetailsPriceButton;


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
        issueDetailsPriceButton = (Button) rootView.findViewById(R.id.issueDetailsPriceButton);

        mLoadIssueTask = new LoadIssueAsyncTask(mMagazineID, mIssueID);
        mLoadIssueTask.execute((String)null);

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
                issueDetailsPriceButton.setText(String.valueOf(issueData.price));

                issueDetailsPriceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIssueTask = new DownloadIssueAsyncTask(Config.Magazine_Number, "120974");
                        mIssueTask.execute((String) null);
                    }
                });
            }

            loadPreviewImages();

        }


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
