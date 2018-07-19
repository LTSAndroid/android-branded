package com.pixelmags.android.pixelmagsapp.service;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.PageTypeImage;
import com.pixelmags.android.datamodels.SingleDownloadIssueTracker;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.BrandedSQLiteHelper;
import com.pixelmags.android.storage.IssueDataSet;
import com.pixelmags.android.storage.SingleIssueDownloadDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

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

    // Queue will hold and process all issues pages once they have completed page that has it's priority set
    public static Queue<SingleDownloadIssueTracker> pageDownloadProcessedQueue;
    public static int noOfIssuePageSize;
    // the tasks and parameters that run the task queues
    //QueueProcessorAsyncTask mQueueProcessorTask;
    public static boolean queueTaskCompleted = true;
    public static boolean downloading = false;
    public static boolean downloadIssueCompleted = false;
    // Queue will prioritise any page that has it's priority set
    static PriorityQueue<DownloadSinglePageThreadStatic> pageThreadQueue;
    static boolean queueTaskPaused = false;
    static boolean queueTaskResumed = false;
    private static String TAG = "DownloadsManager";
    private static int DONE = 0;
    private static int PROCESSING = 1;
    private static int DOWNLOAD_MANAGER_STATUS;
    private static DownloadsManager dmInstance = null;
    private String TAG1 = "CustomAllDownloadsGridAdapters";
    private boolean mPendingRequest = false;
    private boolean interrupted = false;
    private int totalPages;
    private double jumpPages = 0;
    private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker = null;
//    public static int issueIdCurrent = 0;



    private DownloadsManager() {
        // Private prevent any other class from instantiating the DownloadManager

        pageThreadQueue = new PriorityQueue<DownloadSinglePageThreadStatic>(10, new Comparator<DownloadSinglePageThreadStatic>() {

            public int compare(DownloadSinglePageThreadStatic page1, DownloadSinglePageThreadStatic page2) {

                return (page1.getPriority() == page2.getPriority()) ? (Integer.valueOf(page1.getPageNo()).compareTo(page2.getPageNo()))
                        : (page1.getPriority() ? -1 : 1);

            }
        });

        pageDownloadProcessedQueue = new LinkedList<SingleDownloadIssueTracker>();

    }

    public static DownloadsManager getInstance() {
        if(dmInstance == null) {
            dmInstance = new DownloadsManager();
        }
        return dmInstance;
    }

    public static boolean downloadStatus(){
        return downloading;
    }

    /**
     *
     * Represents an asynchronous task used to process the downloads table.
     *
     */
    /*
    public class QueueProcessorAsyncTask extends AsyncTask<String, String, Boolean> {

        int MAX_THREADS = 3;
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

                    DownloadSinglePageThreadStatic testEmpty = pageThreadQueue.peek(); // returns null if the queue is empty

                    if(testEmpty == null) {
                        runQueue = false;
                        break;
                    }

                    ArrayList<Thread> allDownloadThreads = new ArrayList<Thread>();

                    for (int i = 0; i < MAX_THREADS; i++) {

                        // peek to check if the queue is empty
                        DownloadSinglePageThreadStatic executeThread = pageThreadQueue.poll();

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

    */


    public static void updateAllPostDownloadsTable(AllDownloadsIssueTracker issueInQueue){

        processPostDownloadQueue();
        validateDownloadedIssue(issueInQueue);

    }

    public static void validateDownloadedIssue(AllDownloadsIssueTracker issueInQueue){

        try {
            // check if all the downloads were processsed
            SingleIssueDownloadDataSet mDbDownloadTableReader = new SingleIssueDownloadDataSet(BaseApp.getContext());
            int count = mDbDownloadTableReader.getCountOfPagesPendingDownload(mDbDownloadTableReader.getReadableDatabase(), String.valueOf(issueInQueue.uniqueIssueDownloadTable));
            mDbDownloadTableReader.close();
            Magazine magazine = new Magazine();
            if (count == 0){

                downloadIssueCompleted = true;

                // mark download as complete
                AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
                mDbReader.setIssueToCompleted(mDbReader.getReadableDatabase(), String.valueOf(issueInQueue.issueID),100);
                mDbReader.close();

                downloading = false;

                issueInQueue.downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_COMPLETED;


            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void  processPostDownloadQueue(){

        SQLiteDatabase db = null;
        try{

            boolean run = true;

            SingleIssueDownloadDataSet mDbDownloadTableWriter = new SingleIssueDownloadDataSet(BaseApp.getContext());

            // update as transaction
            db = mDbDownloadTableWriter.getWritableDatabase();
            db.beginTransaction();

            while(run){
                SingleDownloadIssueTracker page = pageDownloadProcessedQueue.poll();

                if(page == null){
                    // if queue is empty
                    run = false;
                    break;
                }

                boolean result = mDbDownloadTableWriter.updateIssuePageEntry(db, page, page.uniqueTable);

            }

            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            System.out.println("PROCESSED Post download Batch");

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(db != null){
                db.close();
            }
        }
    }

    public int getDownloadManagerStatus(){
        return DOWNLOAD_MANAGER_STATUS;
    }

    public void setRequestPending(){
        mPendingRequest = true;
    }

    public boolean processDownloadsTable(){

//        Log.d(TAG,"Process Download Table : "+queueTaskCompleted);
//        Log.d(TAG,"Process Download Table queueTaskPaused :"+queueTaskPaused);
        if(!queueTaskCompleted && !queueTaskPaused){
            // queue is running, so just leave a notification and do nothing
            // priority downloads should take a different route
            setRequestPending();
            return true;
        }


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
            boolean isExists = mDbReader.isTableExists(mDbReader.getWritableDatabase(), BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS);
            if(isExists) {
                issueToDownload = mDbReader.getNextIssueInQueue(mDbReader.getReadableDatabase(), Config.Magazine_Number);
                mDbReader.close();
            }else{
                mDbReader.close();
            }

            if(issueToDownload != null){
                System.out.println("<<< NEXT ISSUE TO DOWNLOAD " + issueToDownload.issueTitle + " >>>");
            }



        }catch(Exception e){
            e.printStackTrace();
        }

        return issueToDownload;
    }

    public AllDownloadsIssueTracker fetchAnyDownloadRunning(){

        AllDownloadsIssueTracker issueDownloadInProgress = null;

        try{
            AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
            boolean isExists = mDbReader.isTableExists(mDbReader.getReadableDatabase(), BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS);
            if(isExists) {
                issueDownloadInProgress = mDbReader.getIssueDownloadInProgress(mDbReader.getReadableDatabase(), Config.Magazine_Number);
                mDbReader.close();
            }else{
                mDbReader.close();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return issueDownloadInProgress;
    }

    public boolean startDownload(AllDownloadsIssueTracker issueToDownload){

        // start the threaded download here
        System.out.println("<<< START DOWNLOAD FOR ISSUE : " + issueToDownload.issueTitle + " >>>");


        try{

            AllDownloadsDataSet mDbWriter = new AllDownloadsDataSet(BaseApp.getContext());

            // set the Issue as downloading within the AllDownloadTable
            boolean issueUpdated = mDbWriter.setIssueToInProgress(mDbWriter.getWritableDatabase(), String.valueOf(issueToDownload.issueID),0);
            mDbWriter.close();



            if(issueUpdated){

                // get the Issue pages
                IssueDataSet mDbReader = new IssueDataSet(BaseApp.getContext());
                Issue issueWithPageData = mDbReader.getIssue(mDbReader.getReadableDatabase(), String.valueOf(issueToDownload.issueID));
                mDbReader.close();

                // and recreate / reload the table.
                if(issueWithPageData!=null){

                    ArrayList<SingleDownloadIssueTracker> pagesForSingleDownloadTable = new ArrayList<SingleDownloadIssueTracker>();

                    totalPages = issueWithPageData.pages.size();

//                    Log.d(TAG1,"Total pages is : "+totalPages);
                    jumpPages = (double) totalPages/100;
//                    Log.d(TAG1,"Jump Pages is : "+jumpPages);
                    jumpPages = Math.ceil(jumpPages);
                    int totalPages = (int) jumpPages;
//                    Log.d(TAG1,"Jump Pages with math ceil is : "+  jumpPages);
                    UserPrefs.setIssueId(String.valueOf(issueToDownload.issueID), String.valueOf(totalPages));
//                    updateCount = (int) jumpPages;
//                    Log.d(TAG1,"Updated count is : "+updateCount);

                    for(int i=0; i< issueWithPageData.pages.size();i++) {

                        PageTypeImage page = (PageTypeImage) issueWithPageData.pages.get(i);
                        PageTypeImage.PageDetails pageDetails = page.getPageDetails(PageTypeImage.MediaType.LARGE);
                        SingleDownloadIssueTracker pageTracker = new SingleDownloadIssueTracker(pageDetails, page.getPageNo(),page.getRegions());
                        Log.e("Region IN==>",page.getRegions());
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

    private void createIssueThreads(final AllDownloadsIssueTracker issueToDownload){

        // get all the pages that have to be downloaded
        SingleIssueDownloadDataSet mDbDownloadReader = new SingleIssueDownloadDataSet(BaseApp.getContext());
        ArrayList<SingleDownloadIssueTracker> pagesForSingleDownloadTable = mDbDownloadReader.getSingleIssuePagesPendingDownload(mDbDownloadReader.getReadableDatabase(), issueToDownload.uniqueIssueDownloadTable);
        mDbDownloadReader.close();

        // create threads for each of them and insert them into the priority queue
        if(pagesForSingleDownloadTable != null){

            noOfIssuePageSize = pagesForSingleDownloadTable.size();

            for(int i=0; i< pagesForSingleDownloadTable.size();i++) {

                DownloadSinglePageThreadStatic pageThread = new DownloadSinglePageThreadStatic();
                pageThread.setProcessingValues(issueToDownload, pagesForSingleDownloadTable.get(i), false);
                pageThreadQueue.add(pageThread);

            }

            launchQueueTask(issueToDownload);

        }


        // check if any process tried to interrupt Threads
        if(!interrupted){

            // block any other issues to be downloaded at same time
            // prevent multiple launches of the Issue download
            // Mark Issue as download complete
            // On Issue download complete, recheck if further issues need to be downloaded


            // send a notification to the UI after each Thread, Page download
            // restart download on launch at the right time
            // pause download
            // resume download
            // delete download
            // make issue page as priority


        }

    }

    // process the threads in batches.
    public void launchQueueTask(AllDownloadsIssueTracker issueTracker){

        if(queueTaskCompleted  || queueTaskPaused){
            // launch the queue task again

            // Setting new Issue in Progress


            QueueProcessorThread qThread = new QueueProcessorThread(issueTracker);
            Thread t1 = new Thread(qThread);
            t1.start();

        } // else do nothing as the queue will continue to process until it is empty


    }

    public boolean pauseDownload(){

        return false;
    }

    public boolean stopDownload(){

        return false;
    }

    public void downLoadPaused(){
        try {
            QueueProcessorThread queueProcessorThread = new QueueProcessorThread();
            if (queueProcessorThread != null)
                queueProcessorThread.setPaused();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void downLoadResume(){

        try {
            QueueProcessorThread queueProcessorThread = new QueueProcessorThread();
            if (queueProcessorThread != null)
                queueProcessorThread.setResume();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * Represents an Queue Thread used to process the downloads table.
     *
     */
    public static class QueueProcessorThread implements Runnable {

        public static volatile boolean mPaused;
        public static Object mPausedLock;
        int MAX_THREADS = 1;
        boolean runQueue = true;
        int POSTDOWNLOADBUFFER = 10;
        AllDownloadsIssueTracker issueInQueue;

        QueueProcessorThread(){
            Log.d(TAG,"Inside the constructor of Queue processor thread");
        }

        QueueProcessorThread(AllDownloadsIssueTracker issueTracker){
            issueInQueue = issueTracker;
            mPaused = false;
            mPausedLock = new Object();
        }

        @Override
        public void run() {

            try {

                    queueTaskCompleted = false;

                    int completeCounter = 0;

                    while (runQueue) {

                        DownloadSinglePageThreadStatic testEmpty = pageThreadQueue.peek(); // returns null if the queue is empty

                        if (testEmpty == null) {
                            runQueue = false;
                            break;
                        }

                        ArrayList<Thread> allDownloadThreads = new ArrayList<Thread>();

                        for (int i = 0; i < MAX_THREADS; i++) {

                            // peek to check if the queue is empty
                            DownloadSinglePageThreadStatic executeThread = pageThreadQueue.poll();
                            if (executeThread == null) {
                                break;
                            }

                            Thread t1 = new Thread(executeThread);
                            allDownloadThreads.add(t1);

                        }

                        // start the threads
                        for (Thread thread : allDownloadThreads) {
                            thread.start();
                        }

                        // wait for them to be completed
                        try {

                            for (Thread joinThread : allDownloadThreads) {
                                joinThread.join();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // increment the download counter
                        completeCounter++;

                        // process downloads, i.e save results into db after every X downloads

                        if (completeCounter >= POSTDOWNLOADBUFFER) {
                            processPostDownloadQueue();
                            completeCounter = 0; // reset the counter

                        }

                        // while loop ends

                        synchronized (mPausedLock) {
                            Log.d(TAG," MPause value is : " +mPaused);

                            while (mPaused) {

                                try {

                                    queueTaskPaused = true;


//                                    AllDownloadsDataSet mDbWriter = new AllDownloadsDataSet(BaseApp.getContext());
//
//                                    // set the Issue as Pause within the AllDownloadTable
//                                    Log.d(TAG,"Issue which is paused is : "+issueInQueue);
//                                    mDbWriter.setIssueToPaused(mDbWriter.getWritableDatabase(), issueInQueue);
//                                    mDbWriter.close();

                                    // continue processing table for next download
                                    getInstance().processDownloadsTable();  // Added to check if any Issue is there in queue to start downloading

                                    mPausedLock.wait();

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
            }catch (Exception e){
                e.printStackTrace();
            }

            queueTaskCompleted = true;

            // update all the Issue download completion entries here
            updateAllPostDownloadsTable(issueInQueue);

            // continue processing table for next download
            getInstance().processDownloadsTable();

        }

        public void setPaused(){
            synchronized (mPausedLock){
                mPaused = true;

            }
        }

        public void setResume() {
            synchronized (mPausedLock) {
                mPaused = false;
                queueTaskPaused = false;


//                AllDownloadsDataSet mDbWriter = new AllDownloadsDataSet(BaseApp.getContext());
//
//                // set the Issue as Pause within the AllDownloadTable
//                mDbWriter.setIssueToInProgress(mDbWriter.getWritableDatabase(), issueInQueue);
//                mDbWriter.close();


                mPausedLock.notifyAll();
            }
        }

    }


    public class DownloadSinglePageThreadStatic implements Runnable {

        // form Issues/(Magazine_number)/(issue number)/PDF/

        private static final String ISSUE_DIR_PREFIX_1 = "/Issues/"+ Config.Magazine_Number+"/";
        private static final String ISSUE_DIR_PREFIX_PDF = "/PDF";
        FileOutputStream fileOutput;
        InputStream in;
        private boolean isPriority;
        private boolean isDownloaded;
        private AllDownloadsIssueTracker issueAllDownloadsTracker;
        private SingleDownloadIssueTracker pageSingleDownloadTracker;

        public void setProcessingValues(AllDownloadsIssueTracker allDownloadsTracker, SingleDownloadIssueTracker pageTracker, boolean setAsPriority){

            this.issueAllDownloadsTracker = allDownloadsTracker;
            this.pageSingleDownloadTracker = pageTracker;
            this.isPriority = setAsPriority; // this is the value that will be used in the Comparator to download the image as priority
            this.isDownloaded = false;

//            issueIdCurrent = allDownloadsTracker.issueID;

        }

        public boolean getPriority(){
            return isPriority;
        }

        public int getPageNo(){
            return pageSingleDownloadTracker.pageNo;
        }

        private String getPageFileName(){

            String fileName = String.valueOf(pageSingleDownloadTracker.pageNo)+".jpg";
//            String fileName = String.valueOf(pageSingleDownloadTracker.pageNo)+".png";
            return fileName;
        }

        @Override
        public void run() {

            try {

//                            System.out.println("Download :: downloading page ---- " + pageSingleDownloadTracker.pageNo);
                            downloading = true;

                            in = new URL(pageSingleDownloadTracker.urlPdfLarge).openStream();

                            if (in != null) {

                                ContextWrapper cw = new ContextWrapper(BaseApp.getContext());
                                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

                                String pageImageDir = ISSUE_DIR_PREFIX_1 + issueAllDownloadsTracker.issueID + ISSUE_DIR_PREFIX_PDF;

                                File folder = new File(directory.getAbsolutePath() + pageImageDir);
                                folder.mkdirs();

                                // Create page image file, specifying the path, and the filename which we want to save the file as.
                                File pageImage = new File(folder, getPageFileName());


                                //this will be used to write the downloaded data into the file we created
                                fileOutput = new FileOutputStream(pageImage);

                                //create a buffer
                                byte[] buffer = new byte[1024];
                                int bufferLength = 0; //used to store a temporary size of the buffer

                                //read through the input buffer and write the contents to the file
                                while ((bufferLength = in.read(buffer)) > 0) {
                                    //add the data in the buffer to the file in the file output stream (the file on the sd card)
                                    fileOutput.write(buffer, 0, bufferLength);
                                }
                                //close the output stream when done

//                                Handled the same in finally block

//                                fileOutput.close();
//                                in.close();


                                registerDownloadAsComplete(pageImage.getAbsolutePath());
                            }
            } catch (IOException e){
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {

                if(in != null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(fileOutput != null){
                    try {
                        fileOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }


        // Update the UniqueDownloadTable of the Issue after each page download
        private void registerDownloadAsComplete(String downloadPath){

            this.isDownloaded = true;
            pageSingleDownloadTracker.downloadStatusPdfLarge = SingleIssueDownloadDataSet.DOWNLOAD_STATUS_COMPLETED;
            pageSingleDownloadTracker.downloadedLocationPdfLarge = downloadPath;

            // for post download processing
            pageSingleDownloadTracker.uniqueTable = issueAllDownloadsTracker.uniqueIssueDownloadTable;


            try{

                pageDownloadProcessedQueue.add(pageSingleDownloadTracker);

                // Note : DO NOT update db after every page as that locks the db out for a long time.

//                System.out.println("Download Complete :: " + pageSingleDownloadTracker.pageNo);

            }catch(Exception e){
                e.printStackTrace();
            }

        }


    }


}
