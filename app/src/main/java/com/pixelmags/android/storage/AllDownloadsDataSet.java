package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

import com.pixelmags.android.datamodels.DownloadedIssue;
import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.MySubscription;
import com.pixelmags.android.util.BaseApp;
import com.pixelmags.android.pixelmagsapp.R;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 20/01/16.
 *
 */

public class AllDownloadsDataSet extends BrandedSQLiteHelper {

    public static int DOWNLOAD_STATUS_FAILED = -1;
    public static int DOWNLOAD_STATUS_COMPLETED = 0;
    public static int DOWNLOAD_STATUS_PAUSED = 1;
    public static int DOWNLOAD_STATUS_IN_PROGRESS = 2;
    public static int DOWNLOAD_STATUS_STARTED = 3;
    public static int DOWNLOAD_STATUS_QUEUED = 4;


    public static String getDownloadStatusText(int status){

        String statusText = " ";

        if (status == -1) {
            statusText = BaseApp.getContext().getString(R.string.download_status_failed);
        }
        if(status == 4){
            statusText = BaseApp.getContext().getString(R.string.download_status_in_queue);
        }if(status == 3 || status == 2){
            statusText = BaseApp.getContext().getString(R.string.download_status_started);
        }if(status == 1){
            statusText = BaseApp.getContext().getString(R.string.download_status_paused);
        }if(status == 0){
            statusText = BaseApp.getContext().getString(R.string.download_status_completed);
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


    public boolean issueDownloadPreChecksAndDownload(SQLiteDatabase db, Issue downloadIssue){

        // check if the issue is already inserted for download else reinsert it into the queue


        try {

            Time now = new Time();
            now.setToNow();


            long priorityValue = now.toMillis(false);
            int downloadStatus = DOWNLOAD_STATUS_QUEUED;


            try {

                // Define a projection i.e specify columns to retrieve
                String[] projection = {
                        AllDownloadsEntry.COLUMN_ISSUE_ID,
                        AllDownloadsEntry.COLUMN_PRIORITY,
                        AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS
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

                    }

                    queryCursor.close();
                }


            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }


            if(DOWNLOAD_STATUS_FAILED == -1){
                // queue issue again for download if it had failed
                downloadStatus = DOWNLOAD_STATUS_QUEUED;
            }

            String uniqueIssueDownloadTable = BrandedSQLiteHelper.TABLE_UNIQUE_ISSUE_DOWNLOAD_TABLE_PREFIX + downloadIssue.issueID;

            ContentValues insertValues = new ContentValues();
            insertValues.put(AllDownloadsEntry.COLUMN_ISSUE_ID, downloadIssue.issueID);
            insertValues.put(AllDownloadsEntry.COLUMN_MAGAZINE_ID, downloadIssue.magazineID);
            insertValues.put(AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE, uniqueIssueDownloadTable);
            insertValues.put(AllDownloadsEntry.COLUMN_PRIORITY, priorityValue);
            insertValues.put(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS, downloadStatus);


            boolean insertResult = insert_issue_for_download(db, insertValues);

            return insertResult;

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<DownloadedIssue> getDownloadIssueList(SQLiteDatabase db, String magazineID){

        ArrayList<DownloadedIssue> downloadedIssues = null;

         try {

                // Define a projection i.e specify columns to retrieve
                String[] projection = {
                        AllDownloadsEntry.COLUMN_ISSUE_ID,
                        AllDownloadsEntry.COLUMN_MAGAZINE_ID,
                        AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE,
                        AllDownloadsEntry.COLUMN_PRIORITY,
                        AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS
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


                    downloadedIssues = new ArrayList<DownloadedIssue>();

                    //queryCursor.getCount();
                    while (queryCursor.moveToNext()) {

                        DownloadedIssue issue = new DownloadedIssue();

                        issue.issueID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_ISSUE_ID));
                        issue.magazineID = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_MAGAZINE_ID));
                        issue.uniqueIssueDownloadTable = queryCursor.getString(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE));
                        issue.priority = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_PRIORITY));
                        issue.downloadStatus = queryCursor.getInt(queryCursor.getColumnIndex(AllDownloadsEntry.COLUMN_DOWNLOAD_STATUS));

                        downloadedIssues.add(issue);

                    }

                    queryCursor.close();
                }


        }catch (Exception e){
            e.printStackTrace();
        }

        return downloadedIssues;
    }


    /* Inner class that defines the table contents */
    public static class AllDownloadsEntry {

        public static final String ALL_DOWNLOADS_TABLE_NAME = BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS;
        public static final String COLUMN_ISSUE_ID = "issue_id";
        public static final String COLUMN_MAGAZINE_ID = "magazine_id";
        public static final String COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE = "issue_download_tracker_table"; // the tablename under which the issue is stored.
        public static final String COLUMN_PRIORITY = "download_priority";
        public static final String COLUMN_DOWNLOAD_STATUS = "download_status";

        public static final String CREATE_ALL_DOWNLOADS_TABLE = "CREATE TABLE IF NOT EXISTS "
                + ALL_DOWNLOADS_TABLE_NAME
                + "("
                + COLUMN_ISSUE_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_MAGAZINE_ID + " INTEGER,"
                + COLUMN_UNIQUE_ISSUE_DOWNLOAD_TABLE + " TEXT,"
                + COLUMN_PRIORITY + " INTEGER,"
                + COLUMN_DOWNLOAD_STATUS + " INTEGER"
                + ")"; ;

        public static final String DROP_ALL_DOWNLOADS_TABLE = "DROP TABLE IF EXISTS " + ALL_DOWNLOADS_TABLE_NAME;

    }

}
