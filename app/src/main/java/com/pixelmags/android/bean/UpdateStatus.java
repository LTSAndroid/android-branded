package com.pixelmags.android.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.MyIssue;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.AllIssuesDataSet;
import com.pixelmags.android.storage.BrandedSQLiteHelper;
import com.pixelmags.android.storage.MyIssuesDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by sejeeth on 29/3/18.
 */

public class UpdateStatus {
    private  ArrayList<Magazine> magazinesList;
    ArrayList<AllDownloadsIssueTracker> allDownloadsTracker;


    public UpdateStatus(ArrayList<Magazine> allList){
       this.magazinesList = allList;

    }
    public void filterMagazineStatus(){

            magazinesList = null; // clear the list


            AllIssuesDataSet mDbHelper = new AllIssuesDataSet(BaseApp.getContext());
            magazinesList = mDbHelper.getAllIssuesOnly(mDbHelper.getReadableDatabase());
            mDbHelper.close();

            if(magazinesList != null){
                for(int i=0; i<magazinesList.size(); i++){

                    Log.d("Update Status","Type of issue is : "+magazinesList.get(i).type);

                    if (magazinesList.get(i).paymentProvider != null &&
                            magazinesList.get(i).paymentProvider.trim().equalsIgnoreCase("free")) {
                        magazinesList.get(i).status = Magazine.STATUS_DOWNLOAD;
                    }

                    if(magazinesList.get(i).isIssueOwnedByUser){
                        magazinesList.get(i).status = Magazine.STATUS_DOWNLOAD;
                    }



                }
            }else{
                magazinesList = null;
            }


            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
            if (mDbReader != null) {

                boolean isExists = mDbReader.isTableExists(mDbReader.getReadableDatabase(), BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS);
                if (isExists) {
                    allDownloadsTracker = mDbReader.getDownloadIssueList(mDbReader.getReadableDatabase(), Config.Magazine_Number);

                    for (int j = 0; j < magazinesList.size(); j++) {
                        for (int k = 0; k < allDownloadsTracker.size(); k++) {
                            if (allDownloadsTracker.get(k).issueID == magazinesList.get(j).id) {

                                if (allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_QUEUED) {
                                    magazinesList.get(j).status = Magazine.STATUS_QUEUE;
                                } else if (allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED) {
                                    magazinesList.get(j).status = Magazine.STATUS_VIEW;
                                }
                            }
                        }
                    }

                    mDbReader.close();
                } else {
                    mDbReader.close();
                }
            }else{
                mDbReader.close();
            }


            // retrieve any user issues
            ArrayList<MyIssue> myIssueArray = null;

            if (UserPrefs.getUserLoggedIn()) {
                MyIssuesDataSet myIssuesDbReader = new MyIssuesDataSet(BaseApp.getContext());
                myIssueArray = myIssuesDbReader.getMyIssues(myIssuesDbReader.getReadableDatabase());
                myIssuesDbReader.close();

            }


            if (magazinesList != null) {

                for (int i = 0; i < magazinesList.size(); i++) {
                    //Log.d(TAG,"Magazine List is : "+magazinesList.get(i));
                    if (magazinesList.get(i).isThumbnailDownloaded) {
                        magazinesList.get(i).thumbnailBitmap = loadImageFromStorage(magazinesList.get(i).thumbnailDownloadedInternalPath);
                    }

                    // update the issue owned field
                    // check if the issue is already owned by user
                    if (myIssueArray != null) {
                        for (int issueCount = 0; issueCount < myIssueArray.size(); issueCount++) {
                            MyIssue issue = myIssueArray.get(issueCount);
                            if (issue.issueID == magazinesList.get(i).id) {
                                magazinesList.get(i).isIssueOwnedByUser = true;

                                if (allDownloadsTracker == null) {
                                    magazinesList.get(i).status = Magazine.STATUS_DOWNLOAD;

                                } else {

                                    for (int j = 0; j < magazinesList.size(); j++) {
                                        for (int k = 0; k < allDownloadsTracker.size(); k++) {
                                            if (allDownloadsTracker.get(k).issueID == magazinesList.get(j).id) {
                                                if (allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_QUEUED) {
                                                    magazinesList.get(j).status = Magazine.STATUS_QUEUE;
                                                } else if (allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW) {
                                                    magazinesList.get(j).status = Magazine.STATUS_VIEW;
                                                } else if (allDownloadsTracker.get(k).downloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW) {
                                                    magazinesList.get(j).status = Magazine.STATUS_VIEW;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }





                    if (allDownloadsTracker != null) {
                        for (int downloadCount = 0; downloadCount < allDownloadsTracker.size(); downloadCount++) {

                            AllDownloadsIssueTracker singleDownload = allDownloadsTracker.get(downloadCount);
                            if (singleDownload.issueID == magazinesList.get(i).id) {
                                magazinesList.get(i).currentDownloadStatus = singleDownload.downloadStatus;
                                if (magazinesList.get(i).currentDownloadStatus == 0) {
                                    magazinesList.get(i).status = Magazine.STATUS_VIEW;
                                } else if (magazinesList.get(i).currentDownloadStatus == 4) {
                                    magazinesList.get(i).status = Magazine.STATUS_QUEUE;
                                } else if (magazinesList.get(i).currentDownloadStatus == 1) {
                                    magazinesList.get(i).status = String.valueOf(AllDownloadsDataSet.DOWNLOAD_STATUS_IN_PROGRESS);
                                } else if (magazinesList.get(i).currentDownloadStatus == 3) {
                                    magazinesList.get(i).status = Magazine.STATUS_VIEW;
                                }
                            }
                        }
                    }

                }


               /* if(progressBar != null){
                    progressBar.dismiss();
                }*/

            }else{
               /* if(progressBar != null){
                    progressBar.dismiss();
                }*/
            }


    }

    public ArrayList<Magazine> getFinalStatusList(){
        return magazinesList;

    }

    private Bitmap loadImageFromStorage(String path)
    {
        Log.d("","Path of the loaded Image from storage is : "+path);
        Bitmap issueThumbnail = null;
        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            issueThumbnail = BitmapFactory.decodeStream(inputStream);

            try{

            }finally {
                inputStream.close();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return issueThumbnail;

    }
}
