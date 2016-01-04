package com.pixelmags.android.pixelmagsapp.ui;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.pixelmags.android.api.GetPreviewImages;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.PageTypeImage;
import com.pixelmags.android.datamodels.PreviewImage;
import com.pixelmags.android.download.DownloadIssue;
import com.pixelmags.android.download.DownloadIssueThreaded;
import com.pixelmags.android.download.DownloadPreviewImages;
import com.pixelmags.android.download.DownloadThumbnails;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.IssueDataSet;
import com.pixelmags.android.util.BaseApp;

import java.util.ArrayList;


public class IssueDetailsFragment extends Fragment {


    public Magazine issueData;
    private static final String SERIALIZABLE_MAG_KEY = "serializable_mag_key";

    private DownloadIssueAsyncTask mIssueTask = null;

    LinearLayout previewImagesLayout;
    private DownloadPreviewImagesAsyncTask mPreviewImagesTask = null;


    public IssueDetailsFragment() {
        // Required empty public constructor
    }

    public static IssueDetailsFragment newInstance(Magazine magazineData) {

        IssueDetailsFragment fragment = new IssueDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(SERIALIZABLE_MAG_KEY, magazineData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            issueData = (Magazine) getArguments().getSerializable(SERIALIZABLE_MAG_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_issue_details, container, false);


        if(issueData!=null){

            // Load all data for the issue details page here

            ImageView issueDetailsImageView = (ImageView) rootView.findViewById(R.id.issueDetailsImageView);
            if(issueData.thumbnailBitmap!=null){
                issueDetailsImageView.setImageBitmap(issueData.thumbnailBitmap);
            }

            TextView issueDetailsTitle = (TextView) rootView.findViewById(R.id.issueDetailsTitle);
            issueDetailsTitle.setText(issueData.title);

            TextView issueDetailsSynopsis = (TextView) rootView.findViewById(R.id.issueDetailsSynopsis);
            issueDetailsSynopsis.setText(issueData.synopsis);

            Button issueDetailsPriceButton = (Button) rootView.findViewById(R.id.issueDetailsPriceButton);
            issueDetailsPriceButton.setText(String.valueOf(issueData.price));

            issueDetailsPriceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIssueTask = new DownloadIssueAsyncTask(Config.Magazine_Number, "110422");
                    mIssueTask.execute((String) null);
                }
            });

            /* TODO - replace with scoll of preview images */
            previewImagesLayout = (LinearLayout) rootView.findViewById(R.id.issueDetailsPreviewImageLayout);

            /*
            for (int i = 0; i < 10; i++) {
                ImageView imageView = new ImageView(rootView.getContext());
                imageView.setId(i);
                imageView.setPadding(2, 2, 2, 2);
                imageView.setImageResource(R.drawable.ic_launcher1);
 //               imageView.setImageBitmap(R.drawable.ic_launcher1);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                previewImagesLayout.addView(imageView);
            }
            */

            mPreviewImagesTask = new DownloadPreviewImagesAsyncTask(Config.Magazine_Number, String.valueOf(issueData.id));
            mPreviewImagesTask.execute((String) null);

        }



        return rootView;
    }




    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
     */

    private void startIssueDownload(String issueId){

        // Test the saved output
        IssueDataSet mDbReader = new IssueDataSet(BaseApp.getContext());
        Issue issueData = mDbReader.getIssue(mDbReader.getReadableDatabase(), issueId);

        System.out.println("<<< STARTING ISSUE DOWNLOAD >>>");

        if(issueData != null){
        // Download via queueing
            //DownloadIssue downloadIssue = new DownloadIssue(issueData);
            //downloadIssue.initDownload();

            DownloadIssueThreaded.DownloadIssuePages(issueData);

        }
    }


    /**
     *
     * Represents an asynchronous task used to download an issues.
     *
     */
    public class DownloadIssueAsyncTask extends AsyncTask<String, String, String> {

        private final String mIssueID;

        DownloadIssueAsyncTask(String magID, String issueID) {
            mIssueID = issueID;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String resultToDisplay = "";

            try {

                startIssueDownload(mIssueID);

            }catch (Exception e){
                e.printStackTrace();
            }
            return resultToDisplay;

        }

        protected void onPostExecute(String result) {
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



}
