package com.pixelmags.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.download.DownloadThumbnails;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class AllDownloadsFragment extends Fragment {

    private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker = null;
    public CustomAllDownloadsGridAdapter gridDownloadAdapter;
    private GetAllDownloadedIssuesTask mGetAllDownloadedIssuesTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_all_downloads, container, false);

        // retrieving the downlaoded issues - run inside a async task as there is db access required.
        mGetAllDownloadedIssuesTask = new GetAllDownloadedIssuesTask(Config.Magazine_Number);
        mGetAllDownloadedIssuesTask.execute((String) null);

        // loadAllIssues();

        setGridAdapter(rootView);

        // Inflate the layout for this fragment
        return rootView;
    }


   public void setGridAdapter(View rootView){

       // set the Grid Adapter

       // use rootview to fetch view (when called from onCreateView) else null returns
       GridView gridView = (GridView) rootView.findViewById(R.id.displayAllDownloadsGridView);
       gridDownloadAdapter = new CustomAllDownloadsGridAdapter(getActivity());
       gridView.setAdapter(gridDownloadAdapter);
       //   gridview.setNumColumns(4);

   }



/**
 *  A custom GridView to display the Downloaded Issues.
 *
 */

    public class CustomAllDownloadsGridAdapter extends BaseAdapter {

        private Context mContext;

        public CustomAllDownloadsGridAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {

            if(allDownloadsIssuesListTracker == null){
                return 0;
            }

            return allDownloadsIssuesListTracker.size();
        }

        @Override
        public Object getItem(int arg0) {
            return allDownloadsIssuesListTracker.get(arg0).thumbnailBitmap;
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
                grid=inflater.inflate(R.layout.all_downloads_custom_grid_layout, parent, false);

            }else{
                grid = (View)convertView;
            }

            // Set the magazine image

                if(allDownloadsIssuesListTracker.get(position).thumbnailBitmap != null){

                    ImageView imageView = (ImageView) grid.findViewById(R.id.gridDownloadedIssueImage);
                    imageView.setImageBitmap(allDownloadsIssuesListTracker.get(position).thumbnailBitmap);
                    //imageView.setImageBitmap(bmp);

                    imageView.setTag(position);
                    imageView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            // gridIssueImageClicked((Integer) v.getTag());

                        }
                    });
                }

            if(allDownloadsIssuesListTracker.get(position).issueTitle != null) {
                TextView issueTitleText = (TextView) grid.findViewById(R.id.gridDownloadedTitleText);
                issueTitleText.setText(allDownloadsIssuesListTracker.get(position).issueTitle);
            }


            Button gridDownloadStatusButton = (Button) grid.findViewById(R.id.gridDownloadStatusButton);
                int status = allDownloadsIssuesListTracker.get(position).downloadStatus;
                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
                gridDownloadStatusButton.setText(downloadStatusText);

            gridDownloadStatusButton.setTag(position); // save the gridview index
            gridDownloadStatusButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // action based on status
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
    public class GetAllDownloadedIssuesTask extends AsyncTask<String, String, String> {

        private final String mMagazineID;

        GetAllDownloadedIssuesTask(String MagazineID) {
            mMagazineID = MagazineID;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String resultToDisplay = "";

            try {

                allDownloadsIssuesListTracker = null; // clear the list

                AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                allDownloadsIssuesListTracker = mDbReader.getDownloadIssueList(mDbReader.getReadableDatabase(), Config.Magazine_Number);
                mDbReader.close();

                if(allDownloadsIssuesListTracker != null) {

                    for (int i = 0; i < allDownloadsIssuesListTracker.size(); i++) {

                        System.out.println("<< singleDownloadedIssue "+ allDownloadsIssuesListTracker.get(i).issueID+" >>");
                        allDownloadsIssuesListTracker.get(i).thumbnailBitmap = loadImageFromStorage(
                                DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory(
                                        String.valueOf(allDownloadsIssuesListTracker.get(i).issueID)
                                )
                        );

                    }

                }


                loadAllIssues(); //new change


            }catch (Exception e){
                    e.printStackTrace();
            }
            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

           if(gridDownloadAdapter!=null){
               gridDownloadAdapter.notifyDataSetChanged();
            }


        }

        @Override
        protected void onCancelled() {

        }
    }


    public void loadAllIssues(){

            //  magazinesList.get(i).thumbnailBitmap = loadImageFromStorage(magazinesList.get(i).thumbnailDownloadedInternalPath);

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

        if(gridDownloadAdapter!=null){
            gridDownloadAdapter.notifyDataSetChanged();
        }

    }

 // end of class
}
