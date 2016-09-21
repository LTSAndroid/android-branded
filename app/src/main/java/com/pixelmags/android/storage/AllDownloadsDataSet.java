package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;

import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.util.BaseApp;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 20/01/16.
 *
 * Every download requested is registered in this table. Each row contains the Issue requested
 * and the information associted with that issue relevant to it's download.
 *
 * e.g. a single row is associated withan issue and all contains what the current download status
 * of that issue is.
 *
 */

public class AllDownloadsDataSet extends BrandedSQLiteHelper {


    public static int DOWNLOAD_STATUS_FAILED = -1;
    public static int DOWNLOAD_STATUS_COMPLETED = 0;
    public static int DOWNLOAD_STATUS_IN_PROGRESS = 1;
    public static int DOWNLOAD_STATUS_STARTED = 2;
    public static int DOWNLOAD_STATUS_PAUSED = 3;
    public static int DOWNLOAD_STATUS_QUEUED = 4;
    public static int DOWNLOAD_STATUS_NONE = 5;// when no download as yet has been requested
    public static int DOWNLOAD_STATUS_VIEW = 6;


    public static String getDownloadStatusText(int status){

        String statusText = " ";

        if (status == -1) {
            statusText = BaseApp.getContext().getString(R.string.download_status_failed);
        }
        if(status == 4){
            statusText = BaseApp.getContext().getString(R.string.download_status_in_queue);
        }if(status == 1 || status == 2){
            statusText = BaseApp.getContext().getString(R.string.download_status_started);
        }if(status == 3){
            statusText = BaseApp.getContext().getString(R.string.download_status_paused);
        }if(status == 0){
            statusText = BaseApp.getContext().getString(R.string.download_status_completed);
        }

        if(status == 6){
            statusText = BaseApp.getContext().getString(R.string.download_status_view);
        }


        return statusText;
    }

    public AllDownloadsDataSet(Context context) {
        super(context);
    }

    public void createTableAllDownloads(SQLiteDatabase db){
        db.execSQL(AllDownloadsEntry.CREATE_ALL_DOWNLOADS_TABLE);
    }

    public void dropAllDownloadsTable(SQLiteDatabase db){

        //TODO :- IMPORTANT : Before you drop the All_Downloads table drop each individual table contained within the COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE
        db.execSQL(AllDownloadsEntry.DROP_ALL_DOWNLOADS_TABLE);
    }

    private boolean insert_issue_for_download(SQLiteDatabase db, ContentValues insertValues){


        try{
            // Start the transaction
            db.beginTransaction();

            // clear out any previous values by rebuilding table
            createTableAllDownloads(db);


            db.insertWithOnConflict(AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME, null, insertValues, SQLiteDatabase.CONFLICT_REPLACE);


            //End and close the transaction
            db.setTransactionSuccessful();
            db.endTransaction();


            return true;

        }catch (Exception e){
            System.out.println(e.getStackTrace());
            return false;
        }

    }


    // Added to delete the entry from table

    public void deleteIssueFromTable(SQLiteDatabase db, String issueId){

        try{
            // Start the transaction
            db.beginTransaction();

            db.delete(AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME, AllDownloadsEntry.COLUMN_ISSUE_ID + "=?",new String[] {issueId});

            //End and close the transaction
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

    }

    public boolean issueDownloadPreChecksAndDownload(SQLiteDatabase db, Issue downloadIssue){

        // check if the issue is already inserted for download else reinsert it into the queue


        try {

            Time now = new Time();
            now.setToNow();


            long priorityValue = now.toMillis(false);
            int downloadStatus = DOWNLOAD_STATUS_QUEUED;
            int progressCount = 0;


            try {

                // Define a projection i.e specify columns to retrieve
                String[] projection = {
                        AllDownloadsEntry.COLUMN_ISSUE_ID,
                        AllDownloadsEntry.COLUMN_PRIORITY,
                        AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS,
                        AllDownloadsEntry.COLUMN_PROGRESS_STATE
                };


                String whereClause = AllDownloadsEntry.COLUMN_ISSUE_ID+"=?";
                String [] whereArgs = {String.valueOf(downloadIssue.issueID)};

                Cursor queryCursor = db.query(
                        AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME,    // The table to query
                        projection,                                     // The columns to return
                        whereClause,                                           // The columns for the WHERE clause
                        whereArgs,                                           // The values for the WHERE clause
                        null,
                        null,
                        null                                       // The sort order
                );

                if (queryCursor != null) {

                    //queryCursor.getCount();
                    while (queryCursor.moveToNext()) {

                        priorityValue = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PRIORITY));
                        downloadStatus = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS));
                        progressCount = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PROGRESS_STATE));

                    }

                    queryCursor.close();
                }


            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }


            if(DOWNLOAD_STATUS_FAILED == -1){
                // queue issue again for download if it had failed
                downloadStatus = DOWNLOAD_STATUS_QUEUED;
                progressCount = 0;
            }

            String uniqueIssueDownloadTable = BrandedSQLiteHelper.TABLE_UNIQUE_ISSUE_DOWNLOAD_TABLE_PREFIX + downloadIssue.issueID;

            ContentValues insertValues = new ContentValues();
            insertValues.put(AllDownloadsEntry.COLUMN_ISSUE_ID, downloadIssue.issueID);
            insertValues.put(AllDownloadsEntry.COLUMN_MAGAZINE_ID, downloadIssue.magazineID);
            insertValues.put(AllDownloadsEntry.COLUMN_ISSUE_TITLE, downloadIssue.title);
            insertValues.put(AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE, uniqueIssueDownloadTable);
            insertValues.put(AllDownloadsEntry.COLUMN_PRIORITY, priorityValue);
            insertValues.put(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS, downloadStatus);
            insertValues.put(AllDownloadsEntry.COLUMN_PROGRESS_STATE, progressCount);


            boolean insertResult = insert_issue_for_download(db, insertValues);

            return insertResult;

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            db.close();
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        db.close();
        cursor.close();
        return count > 0;
    }

    public ArrayList<AllDownloadsIssueTracker> getDownloadIssueList(SQLiteDatabase db, String magazineID){

        ArrayList<AllDownloadsIssueTracker> allDownloadsIssueTrackers = null;

         try {

                // Define a projection i.e specify columns to retrieve
                String[] projection = {
                        AllDownloadsEntry.COLUMN_ISSUE_ID,
                        AllDownloadsEntry.COLUMN_MAGAZINE_ID,
                        AllDownloadsEntry.COLUMN_ISSUE_TITLE,
                        AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE,
                        AllDownloadsEntry.COLUMN_PRIORITY,
                        AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS,
                        AllDownloadsEntry.COLUMN_PROGRESS_STATE
                };


             // Specify the sort order
             String sortOrder = AllDownloadsEntry.COLUMN_PRIORITY + " DESC";

             String whereClause = AllDownloadsEntry.COLUMN_MAGAZINE_ID+"=?";
             String [] whereArgs = {magazineID};

                Cursor queryCursor = db.query(
                        AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME,    // The table to query
                        projection,                                     // The columns to return
                        whereClause,                                           // The columns for the WHERE clause
                        whereArgs,                                           // The values for the WHERE clause
                        null,
                        null,
                        sortOrder                                       // The sort order
                );

                if (queryCursor != null) {


                    allDownloadsIssueTrackers = new ArrayList<AllDownloadsIssueTracker>();

                    //queryCursor.getCount();
                    while (queryCursor.moveToNext()) {

                        AllDownloadsIssueTracker issue = new AllDownloadsIssueTracker();

                        issue.issueID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_ISSUE_ID));
                        issue.magazineID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_MAGAZINE_ID));
                        issue.issueTitle = queryCursor.getString(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_ISSUE_TITLE));
                        issue.uniqueIssueDownloadTable = queryCursor.getString(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE));
                        issue.priority = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PRIORITY));
                        issue.downloadStatus = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS));
                        issue.progressCompleted = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PROGRESS_STATE));

                        allDownloadsIssueTrackers.add(issue);

                    }

                    queryCursor.close();
                }


        }catch (Exception e){
            e.printStackTrace();
        }

        return allDownloadsIssueTrackers;
    }


    public AllDownloadsIssueTracker getAllDownloadsTrackerForIssue(SQLiteDatabase db, String issueId){

        AllDownloadsIssueTracker allDownloadsTrackerForIssue = null;

        try {

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    AllDownloadsEntry.COLUMN_ISSUE_ID,
                    AllDownloadsEntry.COLUMN_MAGAZINE_ID,
                    AllDownloadsEntry.COLUMN_ISSUE_TITLE,
                    AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE,
                    AllDownloadsEntry.COLUMN_PRIORITY,
                    AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS,
                    AllDownloadsEntry.COLUMN_PROGRESS_STATE
            };


            // Specify the sort order
            String sortOrder = AllDownloadsEntry.COLUMN_PRIORITY + " ASC";

            String whereClause = AllDownloadsEntry.COLUMN_ISSUE_ID+"=?";
            String [] whereArgs = {issueId};

            Cursor queryCursor = db.query(
                    AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    whereClause,                                           // The columns for the WHERE clause
                    whereArgs,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if (queryCursor != null) {

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {

                    allDownloadsTrackerForIssue = new AllDownloadsIssueTracker();

                    allDownloadsTrackerForIssue.issueID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_ISSUE_ID));
                    allDownloadsTrackerForIssue.magazineID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_MAGAZINE_ID));
                    allDownloadsTrackerForIssue.issueTitle = queryCursor.getString(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_ISSUE_TITLE));
                    allDownloadsTrackerForIssue.uniqueIssueDownloadTable = queryCursor.getString(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE));
                    allDownloadsTrackerForIssue.priority = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PRIORITY));
                    allDownloadsTrackerForIssue.downloadStatus = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS));
                    allDownloadsTrackerForIssue.progressCompleted = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PROGRESS_STATE));
                }

                queryCursor.close();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return allDownloadsTrackerForIssue;
    }


    public AllDownloadsIssueTracker getIssueDownloadInProgress(SQLiteDatabase db, String magazineID){

        AllDownloadsIssueTracker issueDownloadInProgress = null;

        try {

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    AllDownloadsEntry.COLUMN_ISSUE_ID,
                    AllDownloadsEntry.COLUMN_MAGAZINE_ID,
                    AllDownloadsEntry.COLUMN_ISSUE_TITLE,
                    AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE,
                    AllDownloadsEntry.COLUMN_PRIORITY,
                    AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS,
                    AllDownloadsEntry.COLUMN_PROGRESS_STATE
            };


            // Specify the sort order
            String sortOrder = AllDownloadsEntry.COLUMN_PRIORITY + " DESC";

            String whereClause = AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS+"=?";
            String [] whereArgs = {String.valueOf(DOWNLOAD_STATUS_IN_PROGRESS)};

            Cursor queryCursor = db.query(
                    AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    whereClause,                                           // The columns for the WHERE clause
                    whereArgs,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if (queryCursor != null) {

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {

                    issueDownloadInProgress = new AllDownloadsIssueTracker();

                    issueDownloadInProgress.issueID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_ISSUE_ID));
                    issueDownloadInProgress.magazineID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_MAGAZINE_ID));
                    issueDownloadInProgress.issueTitle = queryCursor.getString(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_ISSUE_TITLE));
                    issueDownloadInProgress.uniqueIssueDownloadTable = queryCursor.getString(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE));
                    issueDownloadInProgress.priority = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PRIORITY));
                    issueDownloadInProgress.downloadStatus = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS));
                    issueDownloadInProgress.progressCompleted = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PROGRESS_STATE));
                }

                queryCursor.close();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return issueDownloadInProgress;
    }

    public AllDownloadsIssueTracker getNextIssueInQueue(SQLiteDatabase db, String magazineID){

        AllDownloadsIssueTracker issueToDownloadNext = null;

        try {

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    AllDownloadsEntry.COLUMN_ISSUE_ID,
                    AllDownloadsEntry.COLUMN_MAGAZINE_ID,
                    AllDownloadsEntry.COLUMN_ISSUE_TITLE,
                    AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE,
                    AllDownloadsEntry.COLUMN_PRIORITY,
                    AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS,
                    AllDownloadsEntry.COLUMN_PROGRESS_STATE
            };


            // Specify the sort order
            String sortOrder = AllDownloadsEntry.COLUMN_PRIORITY + " DESC";

            String whereClause = AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS+"=?";
            String [] whereArgs = {String.valueOf(DOWNLOAD_STATUS_QUEUED)};

            String limit = "1";

            Cursor queryCursor = db.query(
                    AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    whereClause,                                           // The columns for the WHERE clause
                    whereArgs,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder,                                  // sort order
                    limit                                       // fetches only the first row in the result
            );

            if (queryCursor != null) {

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {

                    issueToDownloadNext = new AllDownloadsIssueTracker();

                    issueToDownloadNext.issueID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_ISSUE_ID));
                    issueToDownloadNext.magazineID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_MAGAZINE_ID));
                    issueToDownloadNext.issueTitle = queryCursor.getString(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_ISSUE_TITLE));
                    issueToDownloadNext.uniqueIssueDownloadTable = queryCursor.getString(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE));
                    issueToDownloadNext.priority = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PRIORITY));
                    issueToDownloadNext.downloadStatus = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS));
                    issueToDownloadNext.progressCompleted = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PROGRESS_STATE));
                }

                queryCursor.close();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return issueToDownloadNext;
    }

//    public boolean setIssueToInProgress(SQLiteDatabase db, AllDownloadsIssueTracker dIssue, int progress){
//
//        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_IN_PROGRESS, progress);
//
//    }
//
//    public boolean setIssueToPaused(SQLiteDatabase db, AllDownloadsIssueTracker dIssue, int progress){
//
//        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_PAUSED, progress);
//
//    }
//
//    public boolean setIssueToView(SQLiteDatabase db, AllDownloadsIssueTracker dIssue, int progress){
//
//        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_VIEW, progress);
//
//    }
//
//    public boolean setIssueToFailed(SQLiteDatabase db, AllDownloadsIssueTracker dIssue, int progress){
//
//        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_FAILED, progress);
//
//    }
//
//    public boolean setIssueToCompleted(SQLiteDatabase db, AllDownloadsIssueTracker dIssue,int progress){
//
//        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_COMPLETED, progress);
//
//    }


    public boolean setIssueToInProgress(SQLiteDatabase db, String dIssue, int progress){

        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_IN_PROGRESS, progress);

    }

    public boolean setIssueToPaused(SQLiteDatabase db, String dIssue, int progress){

        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_PAUSED, progress);

    }

    public boolean setIssueToView(SQLiteDatabase db, String dIssue, int progress){

        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_VIEW, progress);

    }

    public boolean setIssueToFailed(SQLiteDatabase db, String dIssue, int progress){

        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_FAILED, progress);

    }

    public boolean setIssueToCompleted(SQLiteDatabase db, String dIssue,int progress){

        return updateDownloadStatusOfIssue(db, dIssue, DOWNLOAD_STATUS_COMPLETED, progress);

    }

//    public boolean updateProgressCountOfIssue(SQLiteDatabase db, AllDownloadsIssueTracker dIssue, int progress){
//
//        try{
//
//            ContentValues updateValues = new ContentValues();
//            updateValues.put(AllDownloadsEntry.COLUMN_PROGRESS_STATE, progress);
//
//            String whereClause = AllDownloadsEntry.COLUMN_ISSUE_ID+"=?";
//            String [] whereArgs = {String.valueOf(dIssue.issueID)};
//
//            db.update(
//                    AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME,
//                    updateValues,
//                    whereClause,
//                    whereArgs
//            );
//
//
//            return true;
//
//        }catch(Exception e){
//
//        }
//
//        return false;
//    }


//    public boolean updateDownloadStatusOfIssue(SQLiteDatabase db, AllDownloadsIssueTracker dIssue, int download_status, int progress){
//
//        try{
//
//            ContentValues updateValues = new ContentValues();
//            Log.d("AllDownloadDataSet","Progress count is : "+progress);
//            updateValues.put(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS, download_status);
//            updateValues.put(AllDownloadsEntry.COLUMN_PROGRESS_STATE, progress);
//
//            String whereClause = AllDownloadsEntry.COLUMN_ISSUE_ID+"=?";
//            String [] whereArgs = {String.valueOf(dIssue.issueID)};
//
//            db.update(
//                    AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME,
//                    updateValues,
//                    whereClause,
//                    whereArgs
//                    );
//
//
//            return true;
//
//        }catch(Exception e){
//
//        }
//
//        return false;
//    }


    public boolean updateProgressCountOfIssue(SQLiteDatabase db, String dIssue, int progress){

        try{

            ContentValues updateValues = new ContentValues();
            updateValues.put(AllDownloadsEntry.COLUMN_PROGRESS_STATE, progress);

            String whereClause = AllDownloadsEntry.COLUMN_ISSUE_ID+"=?";
            String [] whereArgs = {String.valueOf(dIssue)};

            db.update(
                    AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME,
                    updateValues,
                    whereClause,
                    whereArgs
            );


            return true;

        }catch(Exception e){

        }

        return false;
    }


    public boolean updateDownloadStatusOfIssue(SQLiteDatabase db, String dIssue, int download_status, int progress){

        try{

            ContentValues updateValues = new ContentValues();
            Log.d("AllDownloadDataSet","Progress count is : "+progress);
            updateValues.put(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS, download_status);
            updateValues.put(AllDownloadsEntry.COLUMN_PROGRESS_STATE, progress);

            String whereClause = AllDownloadsEntry.COLUMN_ISSUE_ID+"=?";
            String [] whereArgs = {String.valueOf(dIssue)};

            db.update(
                    AllDownloadsEntry.ALL_DOWNLOADS_TABLE_NAME,
                    updateValues,
                    whereClause,
                    whereArgs
            );

            return true;

        }catch(Exception e){

        }

        return false;
    }


    /* Inner class that defines the table contents */
    public static class AllDownloadsEntry {

        public static final String ALL_DOWNLOADS_TABLE_NAME = BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS;
        public static final String COLUMN_ISSUE_ID = "issue_id";
        public static final String COLUMN_MAGAZINE_ID = "magazine_id";
        public static final String COLUMN_ISSUE_TITLE = "issue_title";
        public static final String COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE = "issue_download_tracker_table"; // the tablename under which the issue is stored.
        public static final String COLUMN_PRIORITY = "download_priority";
        public static final String COLUMN_DOWNLOAD_STATUS = "download_status";
        public static final String COLUMN_PROGRESS_STATE = "progress_status";

        public static final String CREATE_ALL_DOWNLOADS_TABLE = "CREATE TABLE IF NOT EXISTS "
                + ALL_DOWNLOADS_TABLE_NAME
                + "("
                + COLUMN_ISSUE_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_MAGAZINE_ID + " INTEGER,"
                + COLUMN_ISSUE_TITLE + " TEXT,"
                + COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE + " TEXT,"
                + COLUMN_PRIORITY + " INTEGER,"
                + COLUMN_DOWNLOAD_STATUS + " INTEGER,"
                + COLUMN_PROGRESS_STATE + " INTEGER"
                + ")";

        public static final String DROP_ALL_DOWNLOADS_TABLE = "DROP TABLE IF EXISTS " + ALL_DOWNLOADS_TABLE_NAME;

    }

}
