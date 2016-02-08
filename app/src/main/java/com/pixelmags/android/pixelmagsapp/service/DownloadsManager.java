package com.pixelmags.android.pixelmagsapp.service;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.PageTypeImage;
import com.pixelmags.android.datamodels.SingleDownloadIssueTracker;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.IssueDataSet;
import com.pixelmags.android.storage.SingleIssueDownloadDataSet;
import com.pixelmags.android.util.BaseApp;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 29/01/16.
 *
 * Class is a Singleton
 *
 * This class is responsible for managing all downloads
 *  - Check if any in queue
 *  - Assign the next issue to be in queue
 *  - Start, Stop, Pause download
 *  - Report Download progress
 *
 */
public class DownloadsManager {

    private static int DONE = 0;
    private static int PROCESSING = 1;

    private static int DOWNLOAD_MANAGER_STATUS;

    private boolean mPendingRequest = false;

    private static DownloadsManager instance = null;

    private boolean interrupted = false;

    private DownloadsManager() {
        // Prevent any other class to instantiate it
    }

    public static DownloadsManager getInstance() {
        if(instance == null) {
            instance = new DownloadsManager();
        }
        return instance;
    }

    public int getDownloadManagerStatus(){
        return DOWNLOAD_MANAGER_STATUS;
    }

    public void setRequestPending(){
        mPendingRequest = true;
    }

    public boolean processDownloadsTable(){

        AllDownloadsIssueTracker issueToDownload = null;

        try{
            issueToDownload = nextIssueInQueue();
            if(issueToDownload != null)
            {
                return startDownload(issueToDownload);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return false; // return false if nothing to process
    }

    public AllDownloadsIssueTracker nextIssueInQueue(){

        AllDownloadsIssueTracker issueToDownload = null;

        try{
            issueToDownload = fetchAnyDownloadRunning();
            if(issueToDownload != null){

                System.out.println("<<< ISSUE DOWNLOAD IN PROGRESS : "+ issueToDownload.issueTitle +" >>>");

                return issueToDownload;
            }

            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
            issueToDownload = mDbReader.getNextIssueInQueue(mDbReader.getReadableDatabase(), Config.Magazine_Number);

            if(issueToDownload != null){
                System.out.println("<<< NEXT ISSUE TO DOWNLOAD "+ issueToDownload.issueTitle +" >>>");
            }
            mDbReader.close();


        }catch(Exception e){
            e.printStackTrace();
        }

        return issueToDownload;
    }


    public AllDownloadsIssueTracker fetchAnyDownloadRunning(){

        AllDownloadsIssueTracker issueDownloadInProgress = null;

        try{
            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
            issueDownloadInProgress = mDbReader.getIssueDownloadInProgress(mDbReader.getReadableDatabase(), Config.Magazine_Number);
            mDbReader.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        return issueDownloadInProgress;
    }


    public boolean startDownload(AllDownloadsIssueTracker issueToDownload){

        // start the threaded download here
        System.out.println("<<< START DOWNLOAD NOW FOR ISSUE "+ issueToDownload.issueTitle +" >>>");

        try{
            AllDownloadsDataSet mDbWriter = new AllDownloadsDataSet(BaseApp.getContext());

            // set the Issue as downloading within the AllDownloadTable
            boolean issueUpdated = mDbWriter.setIssueToInProgress(mDbWriter.getWritableDatabase(), issueToDownload);
            mDbWriter.close();

            if(issueUpdated){
                // get the Issue pages
                IssueDataSet mDbReader = new IssueDataSet(BaseApp.getContext());
                Issue issueWithPageData = mDbReader.getIssue(mDbReader.getReadableDatabase(), String.valueOf(issueToDownload.issueID));
                mDbReader.close();

                // and recreate / reload the table.
                if(issueWithPageData!=null){

                    ArrayList<SingleDownloadIssueTracker> pagesForSingleDownloadTable = new ArrayList<SingleDownloadIssueTracker>();

                    for(int i=0; i< issueWithPageData.pages.size();i++) {

                        PageTypeImage page = (PageTypeImage) issueWithPageData.pages.get(i);
                        PageTypeImage.PageDetails pageDetails = page.getPageDetails(PageTypeImage.MediaType.LARGE);

                        SingleDownloadIssueTracker pageTracker = new SingleDownloadIssueTracker(pageDetails, i);
                        pagesForSingleDownloadTable.add(pageTracker);
                    }

                    SingleIssueDownloadDataSet mDbDownloadTableWriter = new SingleIssueDownloadDataSet(BaseApp.getContext());
                    boolean result = mDbDownloadTableWriter.initFormationOfSingleIssueDownloadTable(mDbDownloadTableWriter.getWritableDatabase(), issueToDownload, pagesForSingleDownloadTable);
                    mDbDownloadTableWriter.close();

                    if(result){
                        processDownloadForIssue(issueToDownload);
                    }
                }
            }



            return true;

        }catch(Exception e){
            e.printStackTrace();
        }


        // For those not downloaded, create an arraylist of threads in page order
        // process the threads in batches.
        // send a notification to the UI after each Thread, Page download
        // Update the UniqueDownloadTable of the Issue after each batch
        // On Issue download complete, recheck if further issues need to be downloaded

        return false;
    }

    public void processDownloadForIssue(AllDownloadsIssueTracker issueToDownload){


        // check if any process tried to interrupt Threads
        if(!interrupted){

            System.out.println("<< We are ready to initiate the threads for downloads for issue "+issueToDownload.issueID +" >>");

        }

    }

    public boolean pauseDownload(){

        return false;
    }

    public boolean stopDownload(){

        return false;
    }

}
