package com.pixelmags.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.pixelmags.android.ui.uicomponents.MultiStateButton;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class DownloadActivity extends ActionBarActivity {

    private ListView listView;
    private DownloadAdapter downloadAdapter;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;
    private GetAllDownloadedIssuesTask mGetAllDownloadedIssuesTask;
    private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker = null;
    private int index;
    private int previousCount;
    private String TAG = "Download Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        mGetAllDownloadedIssuesTask = new GetAllDownloadedIssuesTask(Config.Magazine_Number);
        mGetAllDownloadedIssuesTask.execute((String) null);

    }

    public void showListItem(){
        listView = (ListView) findViewById(R.id.list_view);
        downloadAdapter = new DownloadAdapter(DownloadActivity.this,allDownloadsIssuesListTracker);
        listView.setAdapter(downloadAdapter);
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

    public void updateTheProgressBar(int index, int count){

        Log.d(TAG,"Inside the update progress bar method");

        int first = listView.getFirstVisiblePosition();
        int last = listView.getLastVisiblePosition();
        if (index < first || index > last) {
            // just update your data set, UI will be updated automatically in next
            // getView() call
        } else {
            View convertView = listView.getChildAt(index - first);
            // this is the convertView that you previously returned in getView
            // just fix it (for example:)
            updateRow(count, convertView);
        }

//        View v = listView.getChildAt(index - listView.getFirstVisiblePosition());
        // Update ProgressBar
//        ProgressBar progress = (ProgressBar).findViewById(R.id.progressBar);
//        progress.setProgress(count);
//        count ++;
//
//        // Update Percentage
//        TextView percentage = (TextView) v.findViewById(R.id.downloadStatusCount);
//        percentage.setText(String.valueOf(count)+"%");

    }


//    public class DownloadAdapter extends BaseAdapter {
//
//        private Context mContext;
//        private int previousProgressCount;
//        private int pos;
//
//        public DownloadAdapter(Context context) {
//            mContext = context;
//        }
//
//        @Override
//        public int getCount() {
//            return allDownloadsIssuesListTracker.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return allDownloadsIssuesListTracker.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inflater = (LayoutInflater) mContext
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.list_item, null);
//            }
//
//            ImageView imageView = (ImageView) convertView.findViewById(R.id.gridDownloadedIssueImage);
//
//            if(allDownloadsIssuesListTracker.get(position).thumbnailBitmap != null){
//
//                imageView.setImageBitmap(allDownloadsIssuesListTracker.get(position).thumbnailBitmap);
//                imageView.setTag(new Integer(position));
//                imageView.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//
//                        issueImageClicked((Integer) v.getTag());
//
//                    }
//                });
//            }
//
//            TextView issueTitleText = (TextView) convertView.findViewById(R.id.gridDownloadedTitleText);
//
//            if(allDownloadsIssuesListTracker.get(position).issueTitle != null) {
//                issueTitleText.setText(allDownloadsIssuesListTracker.get(position).issueTitle);
//            }
//
//            MultiStateButton gridDownloadStatusButton = (MultiStateButton) convertView.findViewById(R.id.gridMultiStateButton);
//            ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
//            TextView progressPercentage = (TextView) convertView.findViewById(R.id.downloadStatusCount);
//            final int status = allDownloadsIssuesListTracker.get(position).downloadStatus;
//            if(status == 3){
//                String pausedStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
//                gridDownloadStatusButton.setAsDownload(pausedStatusText);
//                previousProgressCount = allDownloadsIssuesListTracker.get(position).progressCompleted;
//                progressBar.setProgress(previousProgressCount);
//                progressPercentage.setText(String.valueOf(previousProgressCount));
//            }
//
//            if(status == 4 || status == -1){
//                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
//                gridDownloadStatusButton.setText(downloadStatusText);
//                previousProgressCount = allDownloadsIssuesListTracker.get(position).progressCompleted;
//                progressBar.setProgress(previousProgressCount);
//                progressPercentage.setText(String.valueOf(previousProgressCount));
//            }
//
//            if(status == 0){
//                gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
//                progressBar.setTag(position);
//                progressBar.setProgress(100);
//                progressPercentage.setText(String.valueOf(100));
//            }
//
//            if(status == 1 || status == 2 || status == 6){
//                gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
//                DownloadsManager.downloading = true;
//                previousProgressCount = allDownloadsIssuesListTracker.get(position).progressCompleted;
//                updateProgress(position,previousProgressCount);
//
//            }
//
//
//            return convertView;
//        }
//
//        public void issueImageClicked(int position){
//
//            navigateToIssueDetails(position);
//
//        }
//
//        public void updateProgress(int pos, int count){
//            index = pos;
//            previousCount = count;
//            final Thread t = new Thread() {
//                @Override
//                public void run() {
//                    while (true) {
//                        try {
//                            synchronized (this) {
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//
//                                        updateTheProgressBar(index,previousCount);
//
//                                    }
//                                });
//                                wait(88888);
//                            }
//
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//            t.start();
//
//        }
//
//        private void navigateToIssueDetails(int position) {
//
//            String issueID = String.valueOf(allDownloadsIssuesListTracker.get(position).issueID);
//
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            Fragment fragment = IssueDetailsFragment.newInstance(issueID, Config.Magazine_Number);
//            fragmentManager.beginTransaction()
//                    .replace(R.id.main_fragment_container, fragment)
//                    .commit();
//
//        }
//
//    }

    public void updateRow(int counts, View v){

        ProgressBar progress = (ProgressBar) v.findViewById(R.id.progressBar);
        progress.setProgress(counts);
        counts ++;

        // Update Percentage
        TextView percentage = (TextView) v.findViewById(R.id.downloadStatusCount);
        percentage.setText(String.valueOf(counts)+"%");

    }

    public static class DownloadAdapter extends BaseAdapter implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private static final int PROGRESS = 0*1;
        private static int issueIdCopy = 0;
        private static int copyOfJumpTime = 0;
        ArrayList<IssueDocumentKey> issueDocumentKeys;
        private Activity activity;
        private ProgressBar progressBar;
        private ProgressBar progressBarCurrent;
        private int mProgressStatus = 0;
        private int listMenuItemPosition;
        private CardView cardView;
        private int downloadStatus;
        private String documentKey;
        private boolean run = true;
        private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker;
        private View grid;
        private int jumpTime = 0;
        private String TAG = "CustomAllDownloadsGridAdapter";
        private  MultiStateButton gridDownloadStatusButton;
        private ArrayList<ListObject> listObject = new ArrayList<ListObject>();
        private int previousProgressCount;
        private int previousProgressCountCurrentIssue;
        private int currentIssueDownloadingPosition;
        private int copyOfOnResumeCount = 0;
        private int count = 0;
        private TextView progressPercentage;
        private TextView progressPercentageCurrent;
        private int percentageCount = 0;
//    MyCountDownTimer myCountDownTimer;


        public DownloadAdapter(Activity activity,ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker) {
            this.activity = activity;
            this.allDownloadsIssuesListTracker = allDownloadsIssuesListTracker;
        }

        public static int updateProgressStateMenu(){
            return copyOfJumpTime;
        }

        public static int issueId(){
            return issueIdCopy;
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

//        if(convertView==null){

            grid = new View(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            grid=inflater.inflate(R.layout.all_downloads_custom_grid_layout, parent, false);

//        }else{
//            grid = (View)convertView;
//        }

            // Set the magazine image
            cardView = (CardView) grid.findViewById(R.id.card_view);
            cardView.setTag(position);

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

                        gridIssueImageClicked((Integer) v.getTag(),v);

                    }
                });
            }

            if(allDownloadsIssuesListTracker.get(position).issueTitle != null) {
                TextView issueTitleText = (TextView) grid.findViewById(R.id.gridDownloadedTitleText);
                issueTitleText.setText(allDownloadsIssuesListTracker.get(position).issueTitle);
            }
            gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
            final int status = allDownloadsIssuesListTracker.get(position).downloadStatus;
            downloadStatus = status;
            gridDownloadStatusButton.setTag(position);
            grid.setTag(position);

//        AllDownloadsDataSet mDbReader_download = new AllDownloadsDataSet(BaseApp.getContext());
//
//        boolean isExists = mDbReader_download.isTableExists(mDbReader_download.getReadableDatabase(), BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS);
////        boolean isExists = mDbReader_download.isTableExists(mDbReader_download.getReadableDatabase(), String.valueOf(allDownloadsIssuesListTracker.get(position).issueID));
//        if(isExists) {
//            AllDownloadsIssueTracker test = mDbReader_download.getAllDownloadsTrackerForIssue(mDbReader_download.getReadableDatabase()
//                    ,String.valueOf(allDownloadsIssuesListTracker.get(position).issueID));
//            previousProgressCount = test.progressCompleted;
//            mDbReader_download.close();
//            Log.d(TAG,"Previous progress count in onCreate method is : "+previousProgressCount);
//        }else{
//            mDbReader_download.close();
//        }

            if(status == 3){
                String pausedStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
                gridDownloadStatusButton.setAsDownload(pausedStatusText);
                progressPercentage = (TextView) grid.findViewById(R.id.downloadStatusCount);
                progressPercentage.setTag(position);
                progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
                progressBar.setTag(position);
                previousProgressCount = allDownloadsIssuesListTracker.get(position).progressCompleted;
                progressBar.setProgress(previousProgressCount);
                progressPercentage.setText(String.valueOf(previousProgressCount));
            }

            if(status == 4 || status == -1){
                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
                gridDownloadStatusButton.setText(downloadStatusText);
                progressPercentage = (TextView) grid.findViewById(R.id.downloadStatusCount);
                progressPercentage.setTag(position);
                progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
                progressBar.setTag(position);
                previousProgressCount = allDownloadsIssuesListTracker.get(position).progressCompleted;
                progressBar.setProgress(previousProgressCount);
                progressPercentage.setText(String.valueOf(previousProgressCount));
            }

            if(status == 0){
                gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
                progressPercentage = (TextView) grid.findViewById(R.id.downloadStatusCount);
                progressPercentage.setTag(position);
                progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
                progressBar.setTag(position);
                progressBar.setProgress(100);
                progressPercentage.setText(String.valueOf(100));
            }

            if(status == 1 || status == 2 || status == 6){
                gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
//            progressPercentageCurrent = (TextView) grid.findViewById(R.id.downloadStatusCount);
//            progressPercentageCurrent.setTag(position);
//            allDownloadsIssuesListTracker.get(position).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;
                DownloadsManager.downloading = true;
                run = true;
                updateTheProgressBar(allDownloadsIssuesListTracker.get(position).issueID,position);
//            myCountDownTimer = new MyCountDownTimer(10000, 100);
//            myCountDownTimer.start();

                progressPercentageCurrent = (TextView) grid.findViewById(R.id.downloadStatusCount);
                progressPercentageCurrent.setTag(position);

                Thread t = new Thread() {
                    @Override
                    public void run(){
                        while(true){
                            try{
                                synchronized (this){
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressPercentageCurrent.setText(String.valueOf(count));
                                            count ++;
                                        }
                                    });
                                    wait(8888);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
            }

            gridDownloadStatusButton.setOnClickListener(this);

            ImageView popup = (ImageView) grid.findViewById(R.id.moreDownloadOptionsMenuButton);
            popup.setTag(position);
            popup.setOnClickListener(this);

            return grid;
        }

        public void updateTheProgressBar(final int issueIdRecived, int pos) {

            count = 0;
            jumpTime = 0;

//        Log.d(TAG,"In Progress Copy of jump Time is : "+copyOfJumpTime);
//        if(copyOfJumpTime != 0){
//            previousProgressCountCurrentIssue = copyOfJumpTime;
//            Log.d(TAG,"First if condition is : "+previousProgressCountCurrentIssue);
//        }else

//            progressPercentageCurrent = (TextView) grid.findViewById(R.id.downloadStatusCount);
//            progressPercentageCurrent.setTag(pos);

            if(copyOfOnResumeCount == 0){
                AllDownloadsDataSet mDbReader_download = new AllDownloadsDataSet(BaseApp.getContext());
                AllDownloadsIssueTracker test = mDbReader_download.getAllDownloadsTrackerForIssue(mDbReader_download.getReadableDatabase()
                        ,String.valueOf(issueIdRecived));
                mDbReader_download.close();
                previousProgressCountCurrentIssue = test.progressCompleted;
//            previousProgressCountCurrentIssue = allDownloadsIssuesListTracker.get(pos).progressCompleted;
                Log.d(TAG,"Second else if condition is : "+previousProgressCountCurrentIssue);
            }else{
                previousProgressCountCurrentIssue = copyOfOnResumeCount;
                Log.d(TAG,"Third else condition is : "+previousProgressCountCurrentIssue);
            }

//        mDbReader_download.close();

            progressBarCurrent = (ProgressBar) grid.findViewById(R.id.progressBar);
            progressBarCurrent.setTag(pos);
            currentIssueDownloadingPosition = pos;
            issueIdCopy = allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).issueID;

            for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
                if(i == pos){
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
                                                if (progressBarCurrent != null) {
                                                    if(count == 0){
                                                        int counts = previousProgressCountCurrentIssue + jumpTime;
                                                        progressBarCurrent.setProgress(counts);
                                                        jumpTime = counts;
                                                        copyOfJumpTime = jumpTime;

                                                        count++;
                                                    }else{
                                                        copyOfJumpTime = jumpTime;
                                                        progressBarCurrent.setProgress(copyOfJumpTime);
//                                                        progressPercentageCurrent.setText(String.valueOf(copyOfJumpTime));

                                                        Log.d(TAG,"Download completed status is : "+DownloadsManager.downloadIssueCompleted);
                                                        if(DownloadsManager.downloadIssueCompleted){
                                                            Log.d(TAG,"Inside the if condition of download completed");

                                                            DownloadsManager.downloadIssueCompleted = false;
                                                            run = false;

                                                            allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).downloadStatus
                                                                    = AllDownloadsDataSet.DOWNLOAD_STATUS_COMPLETED;
                                                            allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).progressCompleted = 100;

                                                            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                                                            boolean completeUpdated = mDbReader.setIssueToCompleted(mDbReader.getWritableDatabase(),
                                                                    String.valueOf(allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).issueID), 100);

                                                            AllDownloadsIssueTracker currentProgressIssue =
                                                                    mDbReader.getIssueDownloadInProgress(mDbReader.getWritableDatabase(), Config.Magazine_Number);
                                                            mDbReader.close();
                                                            if(currentProgressIssue != null){

                                                                Log.d(TAG,"Current Issue in Progress is : "+currentProgressIssue.issueID);
                                                                Log.d(TAG,"Current Issue Status is : "+currentProgressIssue.downloadStatus);
                                                                Log.d(TAG,"List Object size is : "+listObject.size());

                                                                for(int j=0;j<listObject.size(); j++ ){

                                                                    Log.d(TAG,"List Object Issue Id is : "+listObject.get(j).issueId);
                                                                    Log.d(TAG,"Next Issue Id is : "+currentProgressIssue.issueID);

                                                                    if(listObject.get(j).issueId == currentProgressIssue.issueID){

                                                                        Log.d(TAG,"Inside the List Object Issue Id matching");
                                                                        int positionOfNewDownload = listObject.get(j).position;
                                                                        allDownloadsIssuesListTracker.get(positionOfNewDownload).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;
                                                                    }

                                                                }


                                                                Handler handler = new Handler();
                                                                handler.postDelayed(new Runnable() {
                                                                    public void run() {

                                                                        notifyDataSetChanged();

//                                                                    myCountDownTimer.cancel();

                                                                        count = 0;
                                                                        jumpTime = 0;
                                                                        copyOfJumpTime = 0;
                                                                        copyOfOnResumeCount = 0;
                                                                        run = true;
                                                                    }
                                                                }, 3000);

                                                            }else{
                                                                notifyDataSetChanged();

                                                            }

                                                        }

                                                    }

                                                    if(allDownloadsIssuesListTracker.size() == 0){

                                                        Log.d(TAG,"Inside the Size is zero and updating method");
                                                        if(DownloadsManager.downloadIssueCompleted){
                                                            Log.d(TAG,"Inside the of condition of size zero method");
                                                            allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).downloadStatus
                                                                    = AllDownloadsDataSet.DOWNLOAD_STATUS_COMPLETED;
                                                            allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).progressCompleted = 100;

                                                            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                                                            boolean completeUpdated = mDbReader.setIssueToCompleted(mDbReader.getWritableDatabase(),
                                                                    String.valueOf(allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).issueID), 100);

                                                            notifyDataSetChanged();
                                                            count = 0;
                                                            jumpTime = 0;
                                                            copyOfJumpTime = 0;
                                                            copyOfOnResumeCount = 0;
                                                            DownloadsManager.downloadIssueCompleted = false;
                                                            run = false;
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                        wait(8888);
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

        public void updateProgressCount(){
            if(allDownloadsIssuesListTracker.size() != 0 && allDownloadsIssuesListTracker.size() > currentIssueDownloadingPosition){
                AllDownloadsDataSet mDbReader_current = new AllDownloadsDataSet(BaseApp.getContext());
                mDbReader_current.updateProgressCountOfIssue(mDbReader_current.getWritableDatabase(),
                        String.valueOf(allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).issueID), jumpTime);
                mDbReader_current.close();

            }
        }

        public void updateButtonState(int buttonState) {
            if (buttonState == 0) {
                ProgressBar progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
                progressBar.setProgress(100);
                MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
                String StatusText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW);
                gridDownloadStatusButton.setAsView(StatusText);
            } else if (buttonState == 6) {
                MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
                gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
//            AllDownloadsFragment allDownloadsFragment = (AllDownloadsFragment) fragmentManager.findFragmentByTag("ALLDOWNLOADFRAGMENT");
//            if (allDownloadsFragment != null && allDownloadsFragment.isVisible()) {
//                notifyDataSetChanged();
//            }
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

        public void gridIssueImageClicked(int position, View v){

            navigateToIssueDetails(position, v);

        }

        private void navigateToIssueDetails(int position, View v) {

            String issueID = String.valueOf(allDownloadsIssuesListTracker.get(position).issueID);

            Fragment fragment = IssueDetailsFragment.newInstance(issueID, Config.Magazine_Number);
            FragmentActivity activity = (FragmentActivity)v.getContext();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
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

                    final int status = allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus;

                    if(status != 3 && status != 0){

                        DownloadsManager.downloading = false;
                        DownloadsManager.getInstance().downLoadPaused();

                        // Setting Issue to pause in All Download Data set Table
                        AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                        Log.d(TAG,"Issue Id went to pause state is : "+ allDownloadsIssuesListTracker.get(listMenuItemPosition));
                        boolean pauseUpdated = mDbReader.setIssueToPaused(mDbReader.getWritableDatabase(), String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID), jumpTime);
                        mDbReader.close();

                        Log.d(TAG,"Pause Updated is : "+pauseUpdated);
                        Log.d(TAG,"Jump Time when updating to table is : "+jumpTime);

                        allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED;
                        allDownloadsIssuesListTracker.get(listMenuItemPosition).progressCompleted = jumpTime;

                        for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
                            int progressBarTag = (int) progressBarCurrent.getTag();
                            if(progressBarTag == listMenuItemPosition){
                                Log.d(TAG,"Inside the if condition of the pause method");
                                progressBarCurrent.setProgress(jumpTime);
//                            progressPercentageCurrent.setText(String.valueOf(jumpTime));
                                run = false;
                            }
                        }

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
////                            updateButtonState(6);
//                            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
//                            String viewStatusText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW);
//                            gridDownloadStatusButton.setAsDownload(viewStatusText);
//                            int listPos = allDownloadsIssuesListTracker.size() - (j+1);
//                            Log.d(TAG,"Position of new download called is : "+listPos);
//                            updateTheProgressBar(nextIssueToDownload.issueID,listPos);
                                    notifyDataSetChanged();
                                }

                            }

                        }else{

                            Log.d(TAG,"Download Manager Downloading status in else block "+ DownloadsManager.downloadStatus());
                            DownloadsManager.downloading = false;
                            DownloadsManager.getInstance().downLoadPaused();
                            notifyDataSetChanged();
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
                            Log.d(TAG,"Issue Id went to resume state is : "+ String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                            AllDownloadsIssueTracker previousDownload = mDbReader_download.getAllDownloadsTrackerForIssue(mDbReader_download.getWritableDatabase(),
                                    String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                            int previousProgressCountOnResume = previousDownload.progressCompleted;
                            Log.d(TAG,"Previous Progress count on resume is : "+previousProgressCountOnResume);
                            copyOfOnResumeCount = previousProgressCountOnResume;
                            mDbReader_download.setIssueToInProgress(mDbReader_download.getReadableDatabase(), String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID),previousProgressCount);
                            mDbReader_download.close();

                            allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;

                            for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
                                ProgressBar progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
                                int progressBarTag = (int) progressBar.getTag();
                                if(progressBarTag == listMenuItemPosition){
                                    jumpTime = previousProgressCountOnResume;

                                }
                            }

                            notifyDataSetChanged();

                        }else{

                            DownloadsManager.getInstance().downLoadPaused();

                            // Setting Issue to pause in All Download Data set Table
                            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                            boolean pauseUpdated = mDbReader.setIssueToPaused(mDbReader.getWritableDatabase(), String.valueOf(allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).issueID), jumpTime);
                            mDbReader.close();

                            allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED;
                            allDownloadsIssuesListTracker.get(currentIssueDownloadingPosition).progressCompleted = jumpTime;

                            for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
                                if(progressBarCurrent != null){
                                    int progressBarTag = (int) progressBarCurrent.getTag();
                                    if(progressBarTag == currentIssueDownloadingPosition){
                                        progressBarCurrent.setProgress(jumpTime);
//                                progressPercentageCurrent.setText(String.valueOf(jumpTime));
                                        run = false;
                                    }
                                }

                            }

                            count =0;
                            jumpTime = 0;
                            copyOfJumpTime = 0;

                            // Making other issue resume

                            DownloadsManager.getInstance().downLoadResume();

                            AllDownloadsDataSet mDbReader_download = new AllDownloadsDataSet(BaseApp.getContext());
                            AllDownloadsIssueTracker previousDownload = mDbReader_download.getAllDownloadsTrackerForIssue(mDbReader_download.getWritableDatabase(),
                                    String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
                            int previousProgressCountOnResume = previousDownload.progressCompleted;
                            copyOfOnResumeCount = previousProgressCountOnResume;
                            mDbReader_download.setIssueToInProgress(mDbReader_download.getReadableDatabase(), String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID),previousProgressCount);
                            mDbReader_download.close();

                            allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;

                            for(int i=0; i<allDownloadsIssuesListTracker.size(); i++){
                                ProgressBar progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
                                int progressBarTag = (int) progressBar.getTag();
                                if(progressBarTag == listMenuItemPosition){
                                    jumpTime = previousProgressCountOnResume;

                                }
                            }

                            notifyDataSetChanged();

                        }

                    }

                    break;

                case R.id.download_menu_delete:


                    try {

                        // Deleting Thumbnail Images
                        DownloadsManager.getInstance().downLoadPaused();
                        if(issueIdCopy == allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID){
                            DownloadsManager.downloading = false;
                            copyOfJumpTime = 0;

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
                                        notifyDataSetChanged();
                                    }

                                }
                                Log.d(TAG,"Download Manager Downloading status is "+ DownloadsManager.downloadStatus());
                            }else{
                                run = false;
                            }
                        }

                        deleteThumbnail(DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
                                (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)));

                        if(DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
                                (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)) != null)
                            Log.d(TAG, "Thumb nail image after delete is : " + allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID);


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

            }catch (Exception e){
                e.printStackTrace();
            }
            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

            // update the UI

            showListItem();

        }

        @Override
        protected void onCancelled() {

        }
    }


}
