package com.pixelmags.android.pixelmagsapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pixelmags.android.IssueView.NewIssueView;
import com.pixelmags.android.bean.DataTransfer;
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
import com.pixelmags.android.ui.DownloadFragment;
import com.pixelmags.android.ui.IssueDetailsFragment;
import com.pixelmags.android.ui.uicomponents.MultiStateButton;
import com.pixelmags.android.util.BaseApp;
import com.pixelmags.android.util.GetInternetStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by likith.ts on 11/13/2016.
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.myViewHolder> {

    public static boolean isStart = false;
    public static DownloadAdapter.myViewHolder holderCopy;
    public int currentIssueDownloadingPosition = 0;
    ArrayList<IssueDocumentKey> issueDocumentKeys;
    DataTransfer dataTransfer;
    private Activity activity;
    private FragmentManager fragmentManager;
    private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker;
    private int status;
    private int previousProgressCount;
    private String documentKey;
    private int listMenuItemPosition;
    private String TAG = "Download Adapter";
    private int issueIdCopy = 0;
    private int copyOfJumpTime = 0;
    private ArrayList<ListObject> listObject = new ArrayList<ListObject>();


    public DownloadAdapter(Activity activity, ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker,
                           FragmentManager fragmentManager) {
        this.activity = activity;
        this.allDownloadsIssuesListTracker = allDownloadsIssuesListTracker;
        this.fragmentManager = fragmentManager;

    }

    public static void stopTimer(){

        if(holderCopy != null){
            if(holderCopy.timer != null){
                holderCopy.timer.cancel();
            }
        }

    }

    @Override
    public DownloadAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_download_list_layout,parent,false);
        return new  myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DownloadAdapter.myViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
//        holder.progressBar.setTag(position);
        holder.cardView.setTag(position);
        holder.gridDownloadStatusButton.setTag(position);
        holder.popup.setTag(position);
        holder.cardView.setId(position);
        holder.gridDownloadStatusButton.setId(position);
        holder.progressBar.setId(position);
        holder.popup.setId(position);

        holderCopy = holder;

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

            holder.imageView.setImageBitmap(allDownloadsIssuesListTracker.get(position).thumbnailBitmap);
            holder.imageView.setTag(Integer.valueOf(position));
            holder.imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    gridIssueImageClicked((Integer) v.getTag());

                }
            });
        }

        if(allDownloadsIssuesListTracker.get(position).issueTitle != null) {
            holder.issueTitleText.setText(allDownloadsIssuesListTracker.get(position).issueTitle);
        }


        status = allDownloadsIssuesListTracker.get(position).downloadStatus;

        if(status == 3){
            String pausedStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
            holder.gridDownloadStatusButton.setAsDownload(pausedStatusText);
            previousProgressCount = allDownloadsIssuesListTracker.get(position).progressCompleted;
            holder.progressBar.setTag(position);
            holder.progressBar.setProgress(previousProgressCount);
            String progressCount = String.valueOf(previousProgressCount) +"%";
            holder.progressPercentage.setText(progressCount);

        }

        if(status == 4 || status == -1){
            String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
            holder.gridDownloadStatusButton.setText(downloadStatusText);
            previousProgressCount = allDownloadsIssuesListTracker.get(position).progressCompleted;
            holder.progressBar.setTag(position);
            holder.progressBar.setProgress(previousProgressCount);
            String progressCount = String.valueOf(previousProgressCount) +"%";
            holder.progressPercentage.setText(progressCount);
        }

        if(status == 0){
            holder.gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
            holder.progressBar.setTag(position);
            holder.progressBar.setProgress(100);
            String progressCount = "100%";
            holder.progressPercentage.setText(progressCount);
        }

        if(status == 1 || status == 2 || status == 6){
            holder.gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
            DownloadsManager.downloading = true;

            holder.progressBar.setTag(position);

            holder.count = 0;
            holder.jumpTime = 0;
            copyOfJumpTime = 0;


            Log.d("PauseState","Previous Progress Count of Current Issue is 1 : "+holder.copyOfOnResumeCount);

            if(holder.copyOfOnResumeCount == 0){
                AllDownloadsDataSet mDbReader_download = new AllDownloadsDataSet(BaseApp.getContext());
                AllDownloadsIssueTracker test = mDbReader_download.getAllDownloadsTrackerForIssue(mDbReader_download.getReadableDatabase()
                        ,String.valueOf(allDownloadsIssuesListTracker.get(position).issueID));
                mDbReader_download.close();
                holder.previousProgressCountCurrentIssue = test.progressCompleted;
            }else{
                holder.previousProgressCountCurrentIssue = holder.copyOfOnResumeCount;
            }



            Log.d("PauseState","Previous Progress Count of Current Issue is 2 : "+holder.previousProgressCountCurrentIssue);

            currentIssueDownloadingPosition = (int) holder.progressBar.getTag();
            issueIdCopy = allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).issueID;
            DataTransfer.issueId = issueIdCopy;

            isStart = true;
            holder.timer = new Timer();
            holder.timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    if(isStart) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.d(TAG,"Timer JumpTime is : "+holder.jumpTime);

                                if (holder.jumpTime != 100) {

                                    if (holder.count == 0) {

                                        GetInternetStatus getInternetStatus = new GetInternetStatus(activity);
                                        if (getInternetStatus.isNetworkAvailable()) {
                                            int counts = holder.previousProgressCountCurrentIssue + holder.jumpTime;
                                            holder.progressBar.setProgress(counts);
                                            holder.progressPercentage.setText(String.valueOf(counts) + "%");
                                            holder.jumpTime = counts;
                                            copyOfJumpTime = holder.jumpTime;
                                            DataTransfer.count = holder.jumpTime;
                                            Log.d("PauseState","Copy of jump time is :"+ DataTransfer.count);
                                            holder.count++;
                                        } else {
                                            isStart = false;
                                            holder.timer.cancel();
                                            holder.timer.purge();
//                                        getInternetStatus.showAlertDialog();
                                        }

                                    } else {

                                        GetInternetStatus getInternetStatus = new GetInternetStatus(activity);
                                        if (getInternetStatus.isNetworkAvailable()) {
                                            if(holder.jumpTime < 99) {
                                                copyOfJumpTime = holder.jumpTime;
                                                Log.d("PauseState","Copy of jump time is 2 :"+holder.jumpTime);
                                                holder.progressBar.setProgress(holder.jumpTime);
                                                holder.progressPercentage.setText(String.valueOf(holder.jumpTime) + "%");

                                                DataTransfer.count = holder.jumpTime;

                                            }

                                            if (DownloadsManager.downloadIssueCompleted) {
                                                DownloadsManager.downloadIssueCompleted = false;
                                                isStart = false;

                                                allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).downloadStatus
                                                        = AllDownloadsDataSet.DOWNLOAD_STATUS_COMPLETED;
                                                allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).progressCompleted = 100;

                                                AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                                                boolean completeUpdated = mDbReader.setIssueToCompleted(mDbReader.getWritableDatabase(),
                                                        String.valueOf(allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).issueID), 100);

                                                AllDownloadsIssueTracker currentProgressIssue =
                                                        mDbReader.getIssueDownloadInProgress(mDbReader.getWritableDatabase(), Config.Magazine_Number);
                                                mDbReader.close();
                                                if (currentProgressIssue != null) {

                                                    for (int j = 0; j < listObject.size(); j++) {

                                                        if (listObject.get(j).issueId == currentProgressIssue.issueID) {

                                                            int positionOfNewDownload = listObject.get(j).position;
                                                            allDownloadsIssuesListTracker.get(positionOfNewDownload).downloadStatus =
                                                                    AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;
                                                        }
                                                    }

                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        public void run() {

                                                            //notifyDataSetChanged();
                                                            DownloadFragment.updateAdapter();

                                                            holder.count = 0;
                                                            holder.jumpTime = 0;
                                                            copyOfJumpTime = 0;
                                                            holder.copyOfOnResumeCount = 0;
                                                            DownloadsManager.downloadIssueCompleted = false;
//
                                                        }
                                                    }, 3000);

                                                } else {
                                                    //notifyDataSetChanged();
                                                    DownloadFragment.updateAdapter();
                                                }
                                            }

                                        } else {
                                            isStart = false;
                                            holder.timer.cancel();
                                            holder.timer.purge();
                                        }

                                    }

                                    if (allDownloadsIssuesListTracker.size() == 0) {

                                        if (DownloadsManager.downloadIssueCompleted) {
                                            allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).downloadStatus
                                                    = AllDownloadsDataSet.DOWNLOAD_STATUS_COMPLETED;
                                            allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).progressCompleted = 100;

                                            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                                            boolean completeUpdated = mDbReader.setIssueToCompleted(mDbReader.getWritableDatabase(),
                                                    String.valueOf(allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).issueID), 100);

//                                        notifyDataSetChanged();
                                            DownloadFragment.updateAdapter();
                                            holder.count = 0;
                                            holder.jumpTime = 0;
                                            copyOfJumpTime = 0;
                                            holder.copyOfOnResumeCount = 0;
                                            DownloadsManager.downloadIssueCompleted = false;
                                            isStart = false;
                                        }
                                    }

                                    holder.jumpTime++;
                                    holder.progressBar.setProgress(holder.jumpTime);
                                    holder.progressPercentage.setText(String.valueOf(holder.jumpTime) + "%");
                                }

                            }
                        });
                    }

                }

                private void runOnUiThread(Runnable action) {
                    if (Thread.currentThread() != holder.mUiThread) {
                        holder.mHandler.post(action);
                    } else {
                        action.run();
                    }

                }


            }, 2000, 2000);
        }


        holder.gridDownloadStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Based on Button status.
                int pos = (int) view.getTag();
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

                    Intent intent = new Intent(activity,NewIssueView.class);
                    intent.putExtra("issueId",issueId);
                    intent.putExtra("documentKey",documentKey);
//                    activity.startActivity(intent);
                    activity.startActivityForResult(intent, 10001);
                }else if(status == 1 || status == 2){
                    String issueId = String.valueOf(allDownloadsIssuesListTracker.get(pos).issueID);

                    documentKey = getIssueDocumentKey(allDownloadsIssuesListTracker.get(pos).issueID);

                    SaveToDB(DataTransfer.count, DataTransfer.issueId);
                    DownloadAdapter.stopTimer();

                    Intent intent = new Intent(activity,NewIssueView.class);
                    intent.putExtra("issueId",issueId);
                    intent.putExtra("documentKey",documentKey);
//                    activity.startActivity(intent);
                    activity.startActivityForResult(intent, 10001);
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
        });


        holder.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                listMenuItemPosition = pos;
                PopupMenu popupMenu = new PopupMenu(activity, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.alldownloadsoptionsmenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {

                            case R.id.download_menu_pause:

                                final int status = allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus;

                                if(status != 3 && status != 0){

                                    DownloadsManager.downloading = false;
                                    DownloadsManager.getInstance().downLoadPaused();

                                    Log.d("PauseState","Jump Time when issue is going to pause state is : "+holder.jumpTime);

                                    for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
                                        int progressBarTag = (int) holder.progressBar.getTag();
                                        if(progressBarTag == listMenuItemPosition){
                                            holder.progressBar.setProgress(holder.jumpTime);
                                            holder.progressPercentage.setText(String.valueOf(holder.jumpTime) + "%");
                                            isStart = false;
                                            if(holder.timer != null)
                                            holder.timer.cancel();
                                            holder.timer.purge();

                                        }
                                    }

                                    // Setting Issue to pause in All Download Data set Table
                                    AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                                    Log.d("PauseState","Issue Id went to pause state is : "+ allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID);
                                    boolean pauseUpdated = mDbReader.setIssueToPaused(mDbReader.getWritableDatabase(),
                                            String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID), holder.jumpTime);
                                    mDbReader.close();

                                    Log.d("PauseState","Pause count Updated in database is : "+holder.jumpTime);

                                    allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED;
                                    allDownloadsIssuesListTracker.get(listMenuItemPosition).progressCompleted = holder.jumpTime;

                                    holder.jumpTime = 0;
//                                    copyOfJumpTime = 0;

                                    // Finding any issue which is in QUEUE State


                                    AllDownloadsIssueTracker nextIssueToDownload = nextIssueInQueue();
                                    if(nextIssueToDownload != null){
                                        AllDownloadsDataSet mDbWriter = new AllDownloadsDataSet(BaseApp.getContext());

                                        // set the Issue as downloading within the AllDownloadTable
                                        boolean issueUpdated = mDbWriter.setIssueToInProgress(mDbWriter.getWritableDatabase(), String.valueOf(nextIssueToDownload.issueID),0);
                                        mDbWriter.close();

                                        for(int j=0;j<listObject.size(); j++ ){

                                            if(listObject.get(j).issueId == nextIssueToDownload.issueID){
                                                int positionOfNewDownload = listObject.get(j).position;
                                                allDownloadsIssuesListTracker.get(positionOfNewDownload).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;
//                                                notifyDataSetChanged();
                                                DownloadFragment.updateAdapter();
                                            }

                                        }

                                    }else{

                                        Log.d(TAG,"Download Manager Downloading status in else block "+ DownloadsManager.downloadStatus());
                                        DownloadsManager.downloading = false;
                                        DownloadsManager.getInstance().downLoadPaused();
//                                        notifyDataSetChanged();
                                        DownloadFragment.updateAdapter();
                                    }

                                }

                                break;

                            case R.id.download_menu_resume:

                                final int statusResume = allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus;

                                if(statusResume != 1 && statusResume != 2 && statusResume != 6 && statusResume != 0){

                                    if(!DownloadsManager.downloadStatus()){

                                        DownloadsManager.downloading = true;
                                        DownloadsManager.getInstance().downLoadResume();

                                        AllDownloadsDataSet mDbReader_download = new AllDownloadsDataSet(BaseApp.getContext());
                                        AllDownloadsIssueTracker previousDownload = mDbReader_download.getAllDownloadsTrackerForIssue(mDbReader_download.getWritableDatabase(),
                                                String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                                        int previousProgressCountOnResume = previousDownload.progressCompleted;
                                        Log.d("PauseState","Previous Progress count on resume is : "+previousProgressCountOnResume);
                                        holder.copyOfOnResumeCount = previousProgressCountOnResume;
                                        mDbReader_download.setIssueToInProgress(mDbReader_download.getReadableDatabase(),
                                                String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID),previousProgressCountOnResume);
                                        mDbReader_download.close();

                                        allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;

                                        Log.d(TAG,"All Download Issue List Tracker : "+allDownloadsIssuesListTracker.size());
                                        Log.d(TAG,"Previous Progress Count In Resume : "+listMenuItemPosition);

                                        for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
                                            int progressBarTag = (int) holder.progressBar.getTag();
                                            if(progressBarTag == listMenuItemPosition){
                                                holder.jumpTime = previousProgressCountOnResume;

                                            }
                                        }

//                                        notifyDataSetChanged();
                                        DownloadFragment.updateAdapter();

                                    }

                                    else{
                                        Toast.makeText(activity,"Please pause current downloading issue to resume another one",Toast.LENGTH_LONG).show();
                                    }

//                                    else{
//
//                                        DownloadsManager.downloading = false;
//                                        DownloadsManager.getInstance().downLoadPaused();
//
//                                        for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
//                                            int progressBarTag = (int) holder.progressBar.getTag();
//                                            if(progressBarTag == currentIssueDownloadingPosition){
//                                                holder.progressBar.setProgress(copyOfJumpTime);
//                                                holder.progressPercentage.setText(String.valueOf(copyOfJumpTime) + "%");
//                                                isStart = false;
//                                                if(copyOfJumpTime!=0) {
//                                                    holder.timer.cancel();
//                                                    holder.timer = null;
//                                                }
//                                            }
//
//
//                                            // Setting Issue to pause in All Download Data set Table
//                                            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
//                                            boolean pauseUpdated = mDbReader.setIssueToPaused(mDbReader.getWritableDatabase(), String.valueOf(allDownloadsIssuesListTracker.get(i).issueID),
//                                                    copyOfJumpTime);
//                                            mDbReader.close();
//
//                                            allDownloadsIssuesListTracker.get(i).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED;
//                                            allDownloadsIssuesListTracker.get(i).progressCompleted = copyOfJumpTime;
//
//                                            Log.d(TAG,"Jump Time when updating issue to pause state is 2 : "+copyOfJumpTime);
//
//                                            holder.count =0;
//                                            holder.jumpTime = 0;
//                                            copyOfJumpTime = 0;
//
////                                            if(allDownloadsIssuesListTracker.get(i).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW){
////                                                holder.progressBar.setProgress(holder.jumpTime);
////                                                holder.progressPercentage.setText(String.valueOf(holder.jumpTime) + "%");
////                                                isStart = false;
////                                                holder.timer.cancel();
////
////                                                Log.d(TAG,"Jump Time when updating issue to pause state is : "+holder.jumpTime);
////
////                                                // Setting Issue to pause in All Download Data set Table
////                                                AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
////                                                boolean pauseUpdated = mDbReader.setIssueToPaused(mDbReader.getWritableDatabase(), String.valueOf(allDownloadsIssuesListTracker.get(i).issueID),
////                                                        holder.jumpTime);
////                                                mDbReader.close();
////
////                                                allDownloadsIssuesListTracker.get(i).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED;
////                                                allDownloadsIssuesListTracker.get(i).progressCompleted = holder.jumpTime;
////
////                                                Log.d(TAG,"Jump Time when updating issue to pause state is 2 : "+holder.jumpTime);
////
////                                                holder.count =0;
////                                                holder.jumpTime = 0;
////                                                copyOfJumpTime = 0;
////
////                                                DownloadsManager.downloading = false;
////                                                DownloadsManager.getInstance().downLoadPaused();
////                                                DownloadFragment.updateAdapter();
////
////                                            }
//
//                                        }
//
//
////                                        Log.d(TAG,"Jump Time when updating issue to pause state is : "+jumpTimeOnPause);
////
////                                        // Setting Issue to pause in All Download Data set Table
////                                        AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
////                                        boolean pauseUpdated = mDbReader.setIssueToPaused(mDbReader.getWritableDatabase(), String.valueOf(allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).issueID),
////                                                jumpTimeOnPause);
////                                        mDbReader.close();
////
////                                        allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED;
////                                        allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).progressCompleted = jumpTimeOnPause;
////
////                                        Log.d(TAG,"Jump Time when updating issue to pause state is 2 : "+copyOfJumpTime);
////
////                                        holder.count =0;
////                                        holder.jumpTime = 0;
////                                        copyOfJumpTime = 0;
////                                        jumpTimeOnPause = 0;
////
////                                        DownloadFragment.updateAdapter();
//
//                                        // Making other issue resume
//
//                                        DownloadsManager.getInstance().downLoadResume();
//
//                                        AllDownloadsDataSet mDbReader_download = new AllDownloadsDataSet(BaseApp.getContext());
//                                        AllDownloadsIssueTracker previousDownload = mDbReader_download.getAllDownloadsTrackerForIssue(mDbReader_download.getWritableDatabase(),
//                                                String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
//                                        int previousProgressCountOnResume = previousDownload.progressCompleted;
//                                        Log.d(TAG,"Previous Count of paused issue going to resume state is : "+previousProgressCountOnResume);
//                                        holder.copyOfOnResumeCount = previousProgressCountOnResume;
//                                        mDbReader_download.setIssueToInProgress(mDbReader_download.getReadableDatabase(), String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID),
//                                                holder.copyOfOnResumeCount);
//                                        mDbReader_download.close();
//
//                                        allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;
//                                        allDownloadsIssuesListTracker.get(listMenuItemPosition).progressCompleted = holder.copyOfOnResumeCount;
//
//                                        for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
//                                            int progressBarTag = (int) holder.progressBar.getTag();
//                                            if(progressBarTag == listMenuItemPosition){
//                                                holder.jumpTime = holder.copyOfOnResumeCount;
//                                                isStart = true;
//                                            }
//                                        }
//
////                                      notifyDataSetChanged();
//                                        DownloadFragment.updateAdapter();
//
//                                    }

                                }

                                break;

                            case R.id.download_menu_delete:


                                try {

                                    // Deleting Thumbnail Images
                                    DownloadsManager.getInstance().downLoadPaused();

                                    if(issueIdCopy == allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID){
                                        DownloadsManager.downloading = false;
//                                        copyOfJumpTime = 0;

                                        AllDownloadsIssueTracker nextIssueToDownload = nextIssueInQueue();
                                        if(nextIssueToDownload != null){
                                            AllDownloadsDataSet mDbWriter = new AllDownloadsDataSet(BaseApp.getContext());

                                            // set the Issue as downloading within the AllDownloadTable
                                            boolean issueUpdated = mDbWriter.setIssueToInProgress(mDbWriter.getWritableDatabase(), String.valueOf(nextIssueToDownload.issueID),0);
                                            mDbWriter.close();

                                            for(int j=0;j<listObject.size(); j++ ){

                                                if(listObject.get(j).issueId == nextIssueToDownload.issueID){
                                                    int positionOfNewDownload = listObject.get(j).position;
                                                    allDownloadsIssuesListTracker.get(positionOfNewDownload).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;
//                                                    notifyDataSetChanged();
                                                    DownloadFragment.updateAdapter();
                                                }

                                            }

                                        }else{
                                            isStart = false;
                                        }
                                    }


                                    AllDownloadsDataSet mDbReader_current = new AllDownloadsDataSet(BaseApp.getContext());
                                    mDbReader_current.updateProgressCountOfIssue(mDbReader_current.getWritableDatabase(),
                                            String.valueOf(issueIdCopy), holder.jumpTime);
                                    mDbReader_current.close();

                                    deleteThumbnail(DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
                                            (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)));

                                    if(DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
                                            (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)) != null);


                                    // Deleting all the downloaded pages
                                    AllDownloadsDataSet mDownloadReader = new AllDownloadsDataSet(BaseApp.getContext());
                                    AllDownloadsIssueTracker allDownloadsTracker = mDownloadReader.getAllDownloadsTrackerForIssue(mDownloadReader.getWritableDatabase(),
                                            String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                                    mDownloadReader.close();


                                    if(allDownloadsTracker != null) {

                                        SingleIssueDownloadDataSet mDbDownloadTableReader = new SingleIssueDownloadDataSet(BaseApp.getContext());
                                        mDbDownloadTableReader.dropUniqueDownloadsTable(mDbDownloadTableReader.getWritableDatabase(),
                                                allDownloadsTracker.uniqueIssueDownloadTable);
                                        mDbDownloadTableReader.close();
                                    }

                                    // Deleting the all download data set table

                                    AllDownloadsDataSet allDownloadsDataSet = new AllDownloadsDataSet(BaseApp.getContext());
                                    allDownloadsDataSet.deleteIssueFromTable(allDownloadsDataSet.getWritableDatabase(),
                                            String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                                    allDownloadsDataSet.close();

//                    allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_NONE;
                                    allDownloadsIssuesListTracker.remove(allDownloadsIssuesListTracker.get(listMenuItemPosition));

                                    Log.d("PauseState","Count when issue is deleted : "+issueIdCopy);
                                    Log.d("PauseState","Download  Manager Downloading is : "+DownloadsManager.downloading);

                                    if(DownloadsManager.downloading){
                                        AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                                        mDbReader_current.updateProgressCountOfIssue(mDbReader_current.getWritableDatabase(),
                                                String.valueOf(issueIdCopy), copyOfJumpTime);
                                        mDbReader_current.close();

                                        copyOfJumpTime = 0;
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }

//                                notifyDataSetChanged();
                                DownloadFragment.updateAdapter();
                                break;

                            default:
                                break;

                        }


                        return true;
                    }
                });
                popupMenu.show();

            }
        });

    }

    public void SaveToDB(int count, int issue) {

        AllDownloadsDataSet mDbReader_current = new AllDownloadsDataSet(BaseApp.getContext());
        mDbReader_current.updateProgressCountOfIssue(mDbReader_current.getWritableDatabase(),
                String.valueOf(issue), count);
        mDbReader_current.close();

    }

    @Override
    public int getItemCount() {
        return allDownloadsIssuesListTracker.size();
    }


//    public static int issueId(){
//        return issueIdCopy;
//    }

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

    public void deleteThumbnail(String path){
        if(path != null){

            File file = new File(path);
            if(file.exists()){
                file.delete();
            }

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

    public class myViewHolder extends RecyclerView.ViewHolder{
        public final android.os.Handler mHandler = new android.os.Handler();
        public ProgressBar progressBar;
        public TextView progressPercentage;
        public Timer timer;
        public Thread mUiThread;
        public int jumpTime =0;
        public int count = 0;
        public int copyOfOnResumeCount=0;
        public int previousProgressCountCurrentIssue = 0;
        private TextView issueTitleText;
        private ImageView imageView;
        private ImageView popup;
        private CardView cardView;
        private MultiStateButton gridDownloadStatusButton;
//        public int currentIssueDownloadingPosition = 0;



        public myViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.issue_image);
            issueTitleText = (TextView) itemView.findViewById(R.id.issue_title);
            gridDownloadStatusButton = (MultiStateButton) itemView.findViewById(R.id.multi_state_button);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            progressPercentage = (TextView) itemView.findViewById(R.id.progress_percentage);
            popup = (ImageView) itemView.findViewById(R.id.moreDownloadOptionsMenuButton);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

        }
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
