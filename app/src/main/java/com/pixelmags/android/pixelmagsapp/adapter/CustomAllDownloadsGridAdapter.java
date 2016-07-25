package com.pixelmags.android.pixelmagsapp.adapter;

/**
 * Created by likith.ts on 7/16/2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.pixelmags.android.storage.BrandedSQLiteHelper;
import com.pixelmags.android.storage.MyIssueDocumentKey;
import com.pixelmags.android.storage.SingleIssueDownloadDataSet;
import com.pixelmags.android.ui.IssueDetailsFragment;
import com.pixelmags.android.ui.uicomponents.MultiStateButton;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.util.ArrayList;

/**
 *  A custom GridView to display the Downloaded Issues.
 *
 */

public class CustomAllDownloadsGridAdapter extends BaseAdapter implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private Activity activity;
    private ProgressBar progressBar;
    private static final int PROGRESS = 0*1;
    private int mProgressStatus = 0;
    private int listMenuItemPosition;
    private CardView cardView;
    private int downloadStatus;
    private String documentKey;
    ArrayList<IssueDocumentKey> issueDocumentKeys;
    private boolean run = true;
    private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker;
    private View grid;
    private int jumpTime = 0;
    private String TAG = "CustomAllDownloadsGridAdapter";
    private FragmentManager fragmentManager;
    private  MultiStateButton gridDownloadStatusButton;
    private ArrayList<ListObject> listObject = new ArrayList<ListObject>();


    public CustomAllDownloadsGridAdapter(Activity activity,ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker, FragmentManager fragmentManager) {
        this.activity = activity;
        this.allDownloadsIssuesListTracker = allDownloadsIssuesListTracker;
        this.fragmentManager = fragmentManager;
    }


    public CustomAllDownloadsGridAdapter(ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker){
        this.allDownloadsIssuesListTracker = allDownloadsIssuesListTracker;
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

            grid = new View(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            grid=inflater.inflate(R.layout.all_downloads_custom_grid_layout, parent, false);

        }else{
            grid = (View)convertView;
        }

        // Set the magazine image
        cardView = (CardView) grid.findViewById(R.id.card_view);
        cardView.setTag(allDownloadsIssuesListTracker.get(position).issueID);

        if(listObject.size() == 0){
            listObject.add(new ListObject(position,allDownloadsIssuesListTracker.get(position).issueID));
        }else{
            for(int i=0; i<listObject.size(); i++){
                if(listObject.get(i).issueId != allDownloadsIssuesListTracker.get(position).issueID){
                    if(i+1 == listObject.size()){
                        listObject.add(new ListObject(position,allDownloadsIssuesListTracker.get(position).issueID));
                    }
                }
            }
        }



        if(allDownloadsIssuesListTracker.get(position).thumbnailBitmap != null){

            ImageView imageView = (ImageView) grid.findViewById(R.id.gridDownloadedIssueImage);
            imageView.setImageBitmap(allDownloadsIssuesListTracker.get(position).thumbnailBitmap);
            //imageView.setImageBitmap(bmp);

            imageView.setTag(new Integer(position));
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    gridIssueImageClicked((Integer) v.getTag());

                }
            });
        }

        if(allDownloadsIssuesListTracker.get(position).issueTitle != null) {
            TextView issueTitleText = (TextView) grid.findViewById(R.id.gridDownloadedTitleText);
            issueTitleText.setText(allDownloadsIssuesListTracker.get(position).issueTitle);
        }

        progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
        progressBar.setTag(allDownloadsIssuesListTracker.get(position).issueID);

        gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
        final int status = allDownloadsIssuesListTracker.get(position).downloadStatus;
        downloadStatus = status;
        gridDownloadStatusButton.setTag(new Integer(position));
        grid.setTag(new Integer(position));

        AllDownloadsDataSet mDbReader_download = new AllDownloadsDataSet(BaseApp.getContext());

        AllDownloadsIssueTracker test = mDbReader_download.getAllDownloadsTrackerForIssue(mDbReader_download.getReadableDatabase()
                ,String.valueOf(allDownloadsIssuesListTracker.get(position).issueID));
        int previousProgressCount = test.progressCompleted;

        mDbReader_download.close();

        if(status == 1 || status == 2 || status == 6){

            gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
            allDownloadsIssuesListTracker.get(position).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;
            updateTheProgressBar(allDownloadsIssuesListTracker.get(position).issueID);

        }

        if(status == 3){
            String pausedStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
            gridDownloadStatusButton.setAsDownload(pausedStatusText);
                progressBar.setProgress(previousProgressCount);
        }

        if(status == 4 || status == -1){
            String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
            gridDownloadStatusButton.setText(downloadStatusText);
            progressBar.setProgress(previousProgressCount);
        }

        if(status == 0){
            gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
            progressBar.setProgress(previousProgressCount);
        }

        gridDownloadStatusButton.setOnClickListener(this);

        ImageView popup = (ImageView) grid.findViewById(R.id.moreDownloadOptionsMenuButton);
        popup.setTag(position);
        popup.setOnClickListener(this);

        return grid;
    }



    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    public void updateTheProgressBar(int issueIdRecived) {

        Log.d(TAG, "position of progress : "+allDownloadsIssuesListTracker.size());
        Log.d(TAG,"Issue came from parameter is : "+issueIdRecived);

        for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){

            Log.d(TAG,"Inside the for loop of update progress bar");

                if(allDownloadsIssuesListTracker.get(i).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW){

                    run = true;
                    Log.d(TAG,"Inside the Progress bar if condition");
                    final int limit = 100;
                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            while (run) {
                                try {
                                    synchronized (this) {

                                        if (activity == null)
                                            return;

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (jumpTime == limit) {
                                                    run = false;
                                                }
                                                jumpTime += 1;
                                                if (progressBar != null) {
                                                    progressBar.setProgress(jumpTime);
                                                }
                                            }
                                        });
                                        wait(6600);
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    t.start();

                }
        }

    }

    public void updateButtonState(int buttonState) {
        if (buttonState == 0) {
            progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
            progressBar.setProgress(100);
            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
            String StatusText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW);
            gridDownloadStatusButton.setAsView(StatusText);
        } else if (buttonState == 6) {
            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
            gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);

        } else if (buttonState == 3) {
            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
            String pauseText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED);
            gridDownloadStatusButton.setAsView(pauseText);
        } else if (buttonState == 1 || buttonState == 2) {
            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
            String StatusText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW);
            gridDownloadStatusButton.setAsView(StatusText);
        }

    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.gridMultiStateButton){

            // Based on Button status.
            int pos = (int) v.getTag();
            final int status = allDownloadsIssuesListTracker.get(pos).downloadStatus;

            if(status == 4){

                new AlertDialog.Builder(activity)
                        .setTitle("Issue download is in queue!")
                        .setMessage("You can view your Issue once download start.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }else if(status == 6 || status == 0){

                String issueId = String.valueOf(allDownloadsIssuesListTracker.get(pos).issueID);

                documentKey = getIssueDocumentKey(allDownloadsIssuesListTracker.get(pos).issueID);

                Log.d(TAG,"Document Key is : " +documentKey);

                Intent intent = new Intent(activity,NewIssueView.class);
                intent.putExtra("issueId",issueId);
                intent.putExtra("documentKey",documentKey);
                activity.startActivity(intent);
            }else if(status == 1 || status == 2){
                String issueId = String.valueOf(allDownloadsIssuesListTracker.get(pos).issueID);

                documentKey = getIssueDocumentKey(allDownloadsIssuesListTracker.get(pos).issueID);

                Intent intent = new Intent(activity,NewIssueView.class);
                intent.putExtra("issueId",issueId);
                intent.putExtra("documentKey",documentKey);
                activity.startActivity(intent);
            }else if(status == -1){
                new AlertDialog.Builder(activity)
                        .setTitle("Error while downloading the issue.")
                        .setMessage("Please trying downloading once again")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }


        }

        if(v.getId() == R.id.moreDownloadOptionsMenuButton){
            int pos = (int) v.getTag();
            showPopup(v,pos);
        }

    }

    public void gridIssueImageClicked(int position){

        navigateToIssueDetails(position);

    }

    private void navigateToIssueDetails(int position) {

        String issueID = String.valueOf(allDownloadsIssuesListTracker.get(position).issueID);

        Fragment fragment = IssueDetailsFragment.newInstance(issueID, Config.Magazine_Number);
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit();

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
        PopupMenu popup = new PopupMenu(activity, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.alldownloadsoptionsmenu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.download_menu_pause:

                DownloadsManager.getInstance().downLoadPaused();

                // Setting Issue to pause in All Download Data set Table
                Log.d(TAG, "Jumb Time is : " + jumpTime);
                AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                mDbReader.setIssueToPaused(mDbReader.getWritableDatabase(), allDownloadsIssuesListTracker.get(listMenuItemPosition), jumpTime);
                mDbReader.close();

                allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED;
                allDownloadsIssuesListTracker.get(listMenuItemPosition).progressCompleted = jumpTime;
                MultiStateButton gridDownloadStatusButton1 = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
                String pausedStatusText1 = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED);
                gridDownloadStatusButton1.setAsDownload(pausedStatusText1);

                for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
                    int progressBarTag = (int) progressBar.getTag();
                    if(progressBarTag == allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID){
                        progressBar.setProgress(jumpTime);
                        run = false;
                    }
                }

                AllDownloadsIssueTracker nextIssueToDownload = nextIssueInQueue();
                if(nextIssueToDownload != null){
                    AllDownloadsDataSet mDbWriter = new AllDownloadsDataSet(BaseApp.getContext());

                    // set the Issue as downloading within the AllDownloadTable
                    boolean issueUpdated = mDbWriter.setIssueToInProgress(mDbWriter.getWritableDatabase(), nextIssueToDownload,0);
                    mDbWriter.close();

                    for(int j=0;j<listObject.size(); j++ ){

                        if(listObject.get(j).issueId == nextIssueToDownload.issueID){
                            int positionOfNewDownload = listObject.get(j).position;
                            allDownloadsIssuesListTracker.get(positionOfNewDownload).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;

//                            updateTheProgressBar(nextIssueToDownload.issueID);
                            notifyDataSetChanged();
                        }

                    }



                }

                notifyDataSetChanged();

                break;

            case R.id.download_menu_resume:

                DownloadsManager.getInstance().downLoadResume();

                AllDownloadsDataSet mDbReader_download = new AllDownloadsDataSet(BaseApp.getContext());

                AllDownloadsIssueTracker previousDownload = mDbReader_download.getAllDownloadsTrackerForIssue(mDbReader_download.getReadableDatabase(),
                        String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                int previousProgressCount = previousDownload.progressCompleted;
                Log.d(TAG,"Previous Progress Count : "+previousProgressCount);
                mDbReader_download.setIssueToInProgress(mDbReader_download.getReadableDatabase(), allDownloadsIssuesListTracker.get(listMenuItemPosition),previousProgressCount);
                mDbReader_download.close();

                allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;
                MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
                String resumeStatusText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW);
                gridDownloadStatusButton.setAsView(resumeStatusText);

                for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){

                    int progressBarTag = (int) progressBar.getTag();
                    if(progressBarTag == allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID){
                        jumpTime = previousProgressCount;
                        updateTheProgressBar(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID);
                        run = true;
                    }
                }

                notifyDataSetChanged();

                break;

            case R.id.download_menu_delete:


                try {

                    // Deleting Thumbnail Images
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
                    allDownloadsDataSet.deleteIssueFromTable(allDownloadsDataSet.getWritableDatabase(),
                            String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                    allDownloadsDataSet.close();

//                    allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_NONE;
                    allDownloadsIssuesListTracker.remove(allDownloadsIssuesListTracker.get(listMenuItemPosition));

                }catch (Exception e){
                    e.printStackTrace();
                }

                notifyDataSetChanged();
                break;

            default:
                break;

        }
        return true;
    }

    public void deleteThumbnail(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }


    public AllDownloadsIssueTracker nextIssueInQueue(){

        AllDownloadsIssueTracker issueToDownload = null;

        try{

            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
            boolean isExists = mDbReader.isTableExists(mDbReader.getReadableDatabase(), BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS);
            if(isExists) {
                issueToDownload = mDbReader.getNextIssueInQueue(mDbReader.getReadableDatabase(), Config.Magazine_Number);
                mDbReader.close();
            }else{
                mDbReader.close();
            }

            if(issueToDownload != null){
                Log.d(TAG,"Next Issue to download is : "+issueToDownload.issueID);
            }



        }catch(Exception e){
            e.printStackTrace();
        }

        return issueToDownload;
    }


    public class ListObject{
        private int position;
        private int issueId;

        public ListObject(int position, int issueId){
            this.position = position;
            this.issueId = issueId;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getIssueId() {
            return issueId;
        }

        public void setIssueId(int issueId) {
            this.issueId = issueId;
        }
    }

}