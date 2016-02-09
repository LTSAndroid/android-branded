package com.pixelmags.android.pixelmagsapp.service;

import android.os.AsyncTask;

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
import java.util.Comparator;
import java.util.PriorityQueue;

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

    // Queue will prioritise any page that has it's priority set
    PriorityQueue<DownloadSinglePageThread> pageThreadQueue;

    // the tasks and parameters that run the task queues
    QueueProcessorAsyncTask mQueueProcessorTask;
    boolean queueTaskCompleted = true;


    private DownloadsManager() {
        // Private prevent any other class from instantiating the DownloadManager

        pageThreadQueue = new PriorityQueue<DownloadSinglePageThread>(10, new Comparator<DownloadSinglePageThread>() {

            public int compare(DownloadSinglePageThread page1, DownloadSinglePageThread page2) {

                return (page1.getPriority() == page2.getPriority()) ? (Integer.valueOf(page1.getPageNo()).compareTo(page2.getPageNo()))
                        : (page1.getPriority() ? -1 : 1);

            }
        });

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
                        createIssueThreads(issueToDownload);
                    }
                }
            }

            return true;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    private void createIssueThreads(AllDownloadsIssueTracker issueToDownload){

        // get all the pages that have to be downloaded
        SingleIssueDownloadDataSet mDbDownloadReader = new SingleIssueDownloadDataSet(BaseApp.getContext());
        ArrayList<SingleDownloadIssueTracker> pagesForSingleDownloadTable = mDbDownloadReader.getSingleIssuePagesPendingDownload(mDbDownloadReader.getReadableDatabase(), issueToDownload.uniqueIssueDownloadTable);
        mDbDownloadReader.close();

        // create threads for each of them and insert them into the priority queue
        if(pagesForSingleDownloadTable != null){

            for(int i=0; i< pagesForSingleDownloadTable.size();i++) {

                DownloadSinglePageThread pageThread = new DownloadSinglePageThread(issueToDownload, pagesForSingleDownloadTable.get(i), false);
                pageThreadQueue.add(pageThread);

            }

            launchQueueTask();

        }





        // check if any process tried to interrupt Threads
        if(!interrupted){

            // block any other issues to be downloaded at same time
            // prevent multiple launches of the Issue download
            // send a notification to the UI after each Thread, Page download
            // Mark Issue as download complete
            // On Issue download complete, recheck if further issues need to be downloaded

        }

    }


    // process the threads in batches.
    public void launchQueueTask(){

        if(queueTaskCompleted){
            // launch the queue task again

            mQueueProcessorTask = new QueueProcessorAsyncTask();
            mQueueProcessorTask.execute((String) null);

        } // else do nothing as the queue will continue to process until it is empty


    }

    public boolean pauseDownload(){

        return false;
    }

    public boolean stopDownload(){

        return false;
    }


    /**
     *
     * Represents an asynchronous task used to process the downloads table.
     *
     */
    public class QueueProcessorAsyncTask extends AsyncTask<String, String, Boolean> {

        int MAX_THREADS = 5;
        boolean runQueue = true;

        QueueProcessorAsyncTask() {
            queueTaskCompleted = false;
        }

        public void interruptQueueProcessorAsyncTask(){
            // use later to pause, resume threads

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {

                queueTaskCompleted = false;

                while(runQueue){

                    DownloadSinglePageThread testEmpty = pageThreadQueue.peek(); // returns null if the queue is empty

                    if(testEmpty == null) {
                        runQueue = false;
                        break;
                    }

                    ArrayList<Thread> allDownloadThreads = new ArrayList<Thread>();

                    for (int i = 0; i < MAX_THREADS; i++) {

                        // peek to check if the queue is empty
                        DownloadSinglePageThread executeThread = pageThreadQueue.poll();

                        if(executeThread == null) {
                            break;
                        }

                        Thread t1 = new Thread(executeThread);
                        allDownloadThreads.add(t1);
                    }

                    // start the threads
                    for (Thread thread : allDownloadThreads){
                        thread.start();
                    }

                    // wait for them to be completed
                    try{

                        for (Thread joinThread : allDownloadThreads){
                            joinThread.join();
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    // while loop ends
                }
                return true;

            }catch (Exception e){
                e.printStackTrace();
            }

            return false;

        }

        protected void onPostExecute(Boolean result) {

            System.out.println("<< Queue Tasks Completed >>");
            queueTaskCompleted = true;
        }

        @Override
        protected void onCancelled() {
            mQueueProcessorTask = null;
        }
    }

}
