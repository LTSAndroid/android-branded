package com.pixelmags.android.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pixelmags.android.IssueView.NewIssueView;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.IssueDocumentKey;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.download.DownloadThumbnails;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.pixelmagsapp.service.DownloadsManager;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.MyIssueDocumentKey;
import com.pixelmags.android.storage.SingleIssueDownloadDataSet;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class AllDownloadsFragment extends Fragment {

    private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker = null;
    public CustomAllDownloadsGridAdapter gridDownloadAdapter;
    private GetAllDownloadedIssuesTask mGetAllDownloadedIssuesTask;
    private String TAG = "AllDownloadsFragment";
    GridView gridView;
    private static View grid;
    public static boolean run = true;
    private int totalLimit;
    public static int jumpTime = 0;

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
       gridView = (GridView) rootView.findViewById(R.id.displayAllDownloadsGridView);
       gridDownloadAdapter = new CustomAllDownloadsGridAdapter(getActivity());
       gridView.setAdapter(gridDownloadAdapter);
       //   gridview.setNumColumns(4);

   }


    public void updateProgressBarFragment(int issueID){
        CustomAllDownloadsGridAdapter customAllDownloadsGridAdapter = new CustomAllDownloadsGridAdapter(getActivity());
        run = true;
        customAllDownloadsGridAdapter.updateTheProgressBar(issueID);
        customAllDownloadsGridAdapter.updateAdapter();
    }

    public void pausedProgressBar(int issueID){
        CustomAllDownloadsGridAdapter customAllDownloadsGridAdapter = new CustomAllDownloadsGridAdapter(getActivity());
        run = false;
        customAllDownloadsGridAdapter.updateTheProgressBar(issueID);
        customAllDownloadsGridAdapter.updateAdapter();
    }

    public void updateIssueTotalPage(int totalPages){
        totalLimit = totalPages;
    }


    public void updateButtonStateFragment(int status){
        CustomAllDownloadsGridAdapter customAllDownloadsGridAdapter = new CustomAllDownloadsGridAdapter(getActivity());
        customAllDownloadsGridAdapter.updateButtonState(status);
        customAllDownloadsGridAdapter.updateAdapter();
    }

    public void updateButtonView(String status){
        CustomAllDownloadsGridAdapter customAllDownloadsGridAdapter = new CustomAllDownloadsGridAdapter(getActivity());
        customAllDownloadsGridAdapter.updateButtonViewAdapter(status);
        customAllDownloadsGridAdapter.updateAdapter();
    }






/**
 *  A custom GridView to display the Downloaded Issues.
 *
 */

    public class CustomAllDownloadsGridAdapter extends BaseAdapter implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private Context mContext;
        private ProgressBar progressBar;
        private static final int PROGRESS = 0*1;
        private int mProgressStatus = 0;
        private int listMenuItemPosition;
        private CardView cardView;
        private int downloadStatus;
        private String documentKey;
        ArrayList<IssueDocumentKey> issueDocumentKeys;



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

            if(convertView==null){

                grid = new View(mContext);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                grid=inflater.inflate(R.layout.all_downloads_custom_grid_layout, parent, false);

            }else{
                grid = (View)convertView;
            }

            // Set the magazine image
            Log.d(TAG," ALL Download Issue List Tracker size is  : "+allDownloadsIssuesListTracker.size());
            Log.d(TAG," ALL Download Issue List Tracker : "+allDownloadsIssuesListTracker.get(position).thumbnailBitmap);
            Log.d(TAG," ALL Download Issue List Tracker issue Title : "+allDownloadsIssuesListTracker.get(position).issueTitle);
            Log.d(TAG, " ALL Download Issue List Tracker download status is : " + allDownloadsIssuesListTracker.get(position).downloadStatus);

            cardView = (CardView) grid.findViewById(R.id.card_view);
            cardView.setTag(position);

                if(allDownloadsIssuesListTracker.get(position).thumbnailBitmap != null){

                    ImageView imageView = (ImageView) grid.findViewById(R.id.gridDownloadedIssueImage);
                    imageView.setImageBitmap(allDownloadsIssuesListTracker.get(position).thumbnailBitmap);
                    //imageView.setImageBitmap(bmp);

                    imageView.setTag(new Integer(position));
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

            progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);


            Button gridDownloadStatusButton = (Button) grid.findViewById(R.id.gridDownloadStatusButton);
                final int status = allDownloadsIssuesListTracker.get(position).downloadStatus;
                downloadStatus = status;

            //handle the progress bar

//            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
//            ArrayList<AllDownloadsIssueTracker> allDownloadsTracker = mDbReader.getDownloadIssueList(mDbReader.getReadableDatabase(), Config.Magazine_Number);
//            mDbReader.close();

            grid.setTag(position);

            if(status == 1 || status == 2){

                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
                gridDownloadStatusButton.setText(downloadStatusText);
                updateTheProgressBar(allDownloadsIssuesListTracker.get(position).issueID);
                notifyDataSetChanged();
            }

            if(status == 3){
                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
                gridDownloadStatusButton.setText(downloadStatusText);
                updateTheProgressBar(allDownloadsIssuesListTracker.get(position).issueID);
                notifyDataSetChanged();
            }

            if(status == 4){
                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
                gridDownloadStatusButton.setText(downloadStatusText);
                progressBar.setProgress(0);
            }

            if(status == -1){
                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
                gridDownloadStatusButton.setText(downloadStatusText);
                progressBar.setProgress(0);
            }

            if(status == 0){
                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
                gridDownloadStatusButton.setText(downloadStatusText);
                progressBar.setProgress(100);
            }

            gridDownloadStatusButton.setTag(position); // save the gridview index
            gridDownloadStatusButton.setOnClickListener(this);

            ImageView popup = (ImageView) grid.findViewById(R.id.moreDownloadOptionsMenuButton);
            popup.setOnClickListener(this);

            return grid;
        }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    public void updateAdapter(){
        notifyDataSetChanged();
    }

    public void updateButtonState(int buttonState){
        if(buttonState == 0){
            progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
            progressBar.setProgress(100);
        }

        Button gridDownloadStatusButton = (Button) grid.findViewById(R.id.gridDownloadStatusButton);
        if(buttonState == 0){
            String downloadStatusText = Magazine.STATUS_VIEW;
            gridDownloadStatusButton.setText(downloadStatusText);
        }
        gridDownloadAdapter.notifyDataSetChanged();
    }

    public void updateButtonViewAdapter(String buttonState){

        Button gridDownloadStatusButton = (Button) grid.findViewById(R.id.gridDownloadStatusButton);
        gridDownloadStatusButton.setText(buttonState);
        gridDownloadAdapter.notifyDataSetChanged();
    }

    public void updateTheProgressBar(int issueId) {

                progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
                final int limit = 100;
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        while (run) {
                            try {
                                if(jumpTime == limit){
                                    run = false;
                                }
                                sleep(66666);
                                jumpTime += 1;
                                if (progressBar != null)
                                    progressBar.setProgress(jumpTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.gridDownloadStatusButton){
            // Based on Button status.
            int pos = (int) cardView.getTag();

            String issueId = String.valueOf(allDownloadsIssuesListTracker.get(pos).issueID);

            documentKey = getIssueDocumentKey(allDownloadsIssuesListTracker.get(pos).issueID);

            Log.d(TAG,"Document Key is : " +documentKey);

            Intent intent = new Intent(getActivity(),NewIssueView.class);
            intent.putExtra("issueId",issueId);
            intent.putExtra("documentKey",documentKey);
            startActivity(intent);


        }

        if(v.getId() == R.id.moreDownloadOptionsMenuButton){
            showPopup(v, (Integer) cardView.getTag());
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


    public void showPopup(View v, int listItemPopupPosition) {
        listMenuItemPosition = listItemPopupPosition;
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.alldownloadsoptionsmenu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.download_menu_pause:

//                MainActivity mainActivity = new MainActivity();
//                mainActivity.stopDownload();
//                getActivity().stopService(new Intent(getActivity(), PMService.class));
//                updateTheProgressBar();
                DownloadsManager.getInstance().downLoadPaused();

                allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED;
                Button gridDownloadStatusButton1 = (Button) grid.findViewById(R.id.gridDownloadStatusButton);
                String downloadStatusText1 = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED);
                gridDownloadStatusButton1.setText(downloadStatusText1);

                notifyDataSetChanged();
                gridDownloadAdapter.notifyDataSetChanged();

                break;

            case R.id.download_menu_resume:

                DownloadsManager.getInstance().downLoadResume();
                allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_IN_PROGRESS;
                Button gridDownloadStatusButton = (Button) grid.findViewById(R.id.gridDownloadStatusButton);
                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_IN_PROGRESS);
                gridDownloadStatusButton.setText(downloadStatusText);

                notifyDataSetChanged();
                gridDownloadAdapter.notifyDataSetChanged();

                break;

            case R.id.download_menu_delete:


                try {

                    // Deleting Thumbnail Images
                    Log.d(TAG, "Thumb nail image before delete is : " + DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
                            (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)));
                    DownloadsManager.getInstance().downLoadPaused();
                    deleteThumbnail(DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
                            (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)));

                    if(DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
                            (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)) != null)
                    Log.d(TAG, "Thumb nail image after delete is : " + allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID);


                    // Deleting all the downloaded pages
                    AllDownloadsDataSet mDownloadReader = new AllDownloadsDataSet(BaseApp.getContext());
                    AllDownloadsIssueTracker allDownloadsTracker = mDownloadReader.getAllDownloadsTrackerForIssue(mDownloadReader.getReadableDatabase(),
                            String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                    mDownloadReader.close();


                    if(allDownloadsTracker != null) {

                        SingleIssueDownloadDataSet mDbDownloadTableReader = new SingleIssueDownloadDataSet(BaseApp.getContext());
                        mDbDownloadTableReader.dropUniqueDownloadsTable(mDbDownloadTableReader.getWritableDatabase(), allDownloadsTracker.uniqueIssueDownloadTable);
                        mDbDownloadTableReader.close();
                    }

                    // Deleting the all download data set table

                    AllDownloadsDataSet allDownloadsDataSet = new AllDownloadsDataSet(BaseApp.getContext());
                    Log.d(TAG, "Menu Item List position is  : " + listMenuItemPosition);
                    Log.d(TAG, "Menu Item List issueID is  : " + allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID);
                    allDownloadsDataSet.deleteIssueFromTable(allDownloadsDataSet.getWritableDatabase(),
                            String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                    allDownloadsDataSet.close();

                    allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_FAILED;
                    allDownloadsIssuesListTracker.remove(allDownloadsIssuesListTracker.get(listMenuItemPosition));

                }catch (Exception e){
                    e.printStackTrace();
                }

                gridDownloadAdapter.notifyDataSetChanged();
                break;

            default:
                break;

        }
        return true;
    }
}


    /*
 This is defined in the allDownloadsFragment layout xml
*/



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

    public void deleteThumbnail(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
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
