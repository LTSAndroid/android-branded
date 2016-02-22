package com.pixelmags.android.download;

import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.IssueDataSet;
import com.pixelmags.android.util.BaseApp;

/**
 * Created by austincoutinho on 04/02/16.
 *
 * This class is used to insert an Issue, etc in the Download Queue
 *
 */
public class QueueDownload {


    public QueueDownload(){

    }

    public boolean insertIssueInDownloadQueue(String issueId) {

        try{

            IssueDataSet mDbReader = new IssueDataSet(BaseApp.getContext());
            Issue mIssue = mDbReader.getIssue(mDbReader.getReadableDatabase(), issueId);
            mDbReader.close();

            if(mIssue != null){

                AllDownloadsDataSet mDownloadsDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                boolean result = mDownloadsDbReader.issueDownloadPreChecksAndDownload(mDownloadsDbReader.getWritableDatabase(), mIssue);
                mDownloadsDbReader.close();

                if(result){

                    //move the issue thumbnail inside the Issue Download Thumbnail folder
                    DownloadThumbnails.copyThumbnailOfIssueDownloaded(String.valueOf(mIssue.issueID));

                    //TODO :- Remove this thumbnail on delete

                }

                return result;

            }




        }catch(Exception e){
            e.printStackTrace();
        }


        return false;

    }


}
