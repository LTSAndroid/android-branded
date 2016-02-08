package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.SingleDownloadIssueTracker;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.util.BaseApp;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 20/01/16.
 *
 */

public class SingleIssueDownloadDataSet extends BrandedSQLiteHelper {

    public static int DOWNLOAD_STATUS_FAILED = -1;
    public static int DOWNLOAD_STATUS_COMPLETED = 0;
    public static int DOWNLOAD_STATUS_PENDING = 1;


    public SingleIssueDownloadDataSet(Context context) {
        super(context);
    }

    private void createTableSingleIssueDownload(SQLiteDatabase db, String UNIQUE_ISSUE_DOWNLOAD_TABLENAME){

        String CREATE_UNIQUE_ISSUE_DOWNLOAD_TABLE = "CREATE TABLE IF NOT EXISTS "
                + UNIQUE_ISSUE_DOWNLOAD_TABLENAME
                + "("
                + SingleIssueDownloadEntry.COLUMN_PAGE_NO + " INTEGER PRIMARY KEY,"
                + SingleIssueDownloadEntry.COLUMN_URL_PDF_LARGE + " TEXT,"
                + SingleIssueDownloadEntry.COLUMN_MD5_CHECKSUM_LARGE + " TEXT,"
                + SingleIssueDownloadEntry.COLUMN_DOWNLOADED_LOCATION_PDF_LARGE + " TEXT,"
                + SingleIssueDownloadEntry.COLUMN_DOWNLOAD_STATUS_PDF_LARGE + " INTEGER"
                + ")"; ;

        db.execSQL(CREATE_UNIQUE_ISSUE_DOWNLOAD_TABLE);

    }

    private void dropUniqueDownloadsTable(SQLiteDatabase db, String UNIQUE_ISSUE_DOWNLOAD_TABLE){

        String DROP_ALL_DOWNLOADS_TABLE = "DROP TABLE IF EXISTS " + UNIQUE_ISSUE_DOWNLOAD_TABLE;

        db.execSQL(DROP_ALL_DOWNLOADS_TABLE);
    }

    public void createDownloadTableForIssue(SQLiteDatabase db, String uniqueDownloadTable){

        try{
            createTableSingleIssueDownload(db, uniqueDownloadTable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public boolean initFormationOfSingleIssueDownloadTable(SQLiteDatabase db,
                                                            AllDownloadsIssueTracker allDownloadTrackerUnit,
                                                            ArrayList<SingleDownloadIssueTracker> newIssueDownloadTable)
    {
        try{
            // Start the transaction
            db.beginTransaction();


            // get all the previous stored values
            ArrayList<SingleDownloadIssueTracker> previousIssueDownloadTable = getUniqueSingleIssueDownloadTable(db, allDownloadTrackerUnit.uniqueIssueDownloadTable);

            if(previousIssueDownloadTable == null){
                // if no previous table exists insert the new values

                // create table if not exists
                createDownloadTableForIssue(db, allDownloadTrackerUnit.uniqueIssueDownloadTable);

                // insert all the values
                insertValuesIntoSingleDownloadTable(db, newIssueDownloadTable, allDownloadTrackerUnit.uniqueIssueDownloadTable);

            }else{
                // compare every record and replace the ones which do not match

                if(newIssueDownloadTable.size() == previousIssueDownloadTable.size() && newIssueDownloadTable.size() != 0 ){

                    for(int i=0; i< newIssueDownloadTable.size();i++) {

                        SingleDownloadIssueTracker prev = previousIssueDownloadTable.get(i);
                        SingleDownloadIssueTracker current = newIssueDownloadTable.get(i);

                        if(current.md5ChecksumLarge == prev.md5ChecksumLarge ){

                            // the pages are the same so replace the values with stored values
                            newIssueDownloadTable.set(i, prev);

                        }else{
                            // do nothing i.e. keep the new values
                        }

                    }

                    // update all the values
                    insertValuesIntoSingleDownloadTable(db, newIssueDownloadTable, allDownloadTrackerUnit.uniqueIssueDownloadTable);


                }else {
                    // page insertion or deletion has occurred, re download the issue
                    dropUniqueDownloadsTable(db, allDownloadTrackerUnit.uniqueIssueDownloadTable);

                    // create table if not exists
                    createDownloadTableForIssue(db, allDownloadTrackerUnit.uniqueIssueDownloadTable);

                    // insert all the values
                    insertValuesIntoSingleDownloadTable(db, newIssueDownloadTable, allDownloadTrackerUnit.uniqueIssueDownloadTable);

                }
            }


            //End and close the transaction
            db.setTransactionSuccessful();
            db.endTransaction();


            return true;

        }catch (Exception e){
            System.out.println(e.getStackTrace());
            return false;
        }


    }


    public ArrayList<SingleDownloadIssueTracker> getUniqueSingleIssueDownloadTable(SQLiteDatabase db, String uniqueDownloadTable){

        ArrayList<SingleDownloadIssueTracker> currentSingleIssueDownload = null;

        try {

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    SingleIssueDownloadEntry.COLUMN_PAGE_NO,
                    SingleIssueDownloadEntry.COLUMN_URL_PDF_LARGE,
                    SingleIssueDownloadEntry.COLUMN_MD5_CHECKSUM_LARGE,
                    SingleIssueDownloadEntry.COLUMN_DOWNLOADED_LOCATION_PDF_LARGE,
                    SingleIssueDownloadEntry.COLUMN_DOWNLOAD_STATUS_PDF_LARGE
            };


            // Specify the sort order
            String sortOrder = SingleIssueDownloadEntry.COLUMN_PAGE_NO + " ASC";

            String whereClause = null;
            String [] whereArgs = null;

            Cursor queryCursor = db.query(
                    uniqueDownloadTable,    // The table to query
                    projection,                                     // The columns to return
                    whereClause,                                           // The columns for the WHERE clause
                    whereArgs,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if (queryCursor != null) {

                currentSingleIssueDownload = new ArrayList<SingleDownloadIssueTracker>();

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {

                    SingleDownloadIssueTracker issueTracker = new SingleDownloadIssueTracker();

                    issueTracker.pageNo = queryCursor.getInt(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_PAGE_NO));
                    issueTracker.urlPdfLarge = queryCursor.getString(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_URL_PDF_LARGE));
                    issueTracker.md5ChecksumLarge = queryCursor.getString(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_MD5_CHECKSUM_LARGE));
                    issueTracker.downloadedLocationPdfLarge = queryCursor.getString(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_DOWNLOADED_LOCATION_PDF_LARGE));
                    issueTracker.downloadStatusPdfLarge = queryCursor.getInt(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_DOWNLOAD_STATUS_PDF_LARGE));

                    currentSingleIssueDownload.add(issueTracker);

                }

                queryCursor.close();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return currentSingleIssueDownload;
    }

    public ArrayList<SingleDownloadIssueTracker> getSingleIssuePagesPendingDownload(SQLiteDatabase db, String uniqueDownloadTable){

        // fetch all the issues that still are pending download i.e. status = DOWNLOAD_STATUS_PENDING

        ArrayList<SingleDownloadIssueTracker> pageSingleIssueDownload = null;

        try {

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    SingleIssueDownloadEntry.COLUMN_PAGE_NO,
                    SingleIssueDownloadEntry.COLUMN_URL_PDF_LARGE,
                    SingleIssueDownloadEntry.COLUMN_MD5_CHECKSUM_LARGE,
                    SingleIssueDownloadEntry.COLUMN_DOWNLOADED_LOCATION_PDF_LARGE,
                    SingleIssueDownloadEntry.COLUMN_DOWNLOAD_STATUS_PDF_LARGE
            };


            // Specify the sort order
            String sortOrder = SingleIssueDownloadEntry.COLUMN_PAGE_NO + " ASC";

            String whereClause = SingleIssueDownloadEntry.COLUMN_DOWNLOAD_STATUS_PDF_LARGE+"=?";
            String [] whereArgs = {String.valueOf(DOWNLOAD_STATUS_PENDING)};

            Cursor queryCursor = db.query(
                    uniqueDownloadTable,    // The table to query
                    projection,                                     // The columns to return
                    whereClause,                                           // The columns for the WHERE clause
                    whereArgs,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if (queryCursor != null) {

                pageSingleIssueDownload = new ArrayList<SingleDownloadIssueTracker>();

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {

                    SingleDownloadIssueTracker issueTracker = new SingleDownloadIssueTracker();

                    issueTracker.pageNo = queryCursor.getInt(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_PAGE_NO));
                    issueTracker.urlPdfLarge = queryCursor.getString(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_URL_PDF_LARGE));
                    issueTracker.md5ChecksumLarge = queryCursor.getString(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_MD5_CHECKSUM_LARGE));
                    issueTracker.downloadedLocationPdfLarge = queryCursor.getString(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_DOWNLOADED_LOCATION_PDF_LARGE));
                    issueTracker.downloadStatusPdfLarge = queryCursor.getInt(queryCursor.getColumnIndex(SingleIssueDownloadEntry.COLUMN_DOWNLOAD_STATUS_PDF_LARGE));

                    pageSingleIssueDownload.add(issueTracker);

                }

                queryCursor.close();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return pageSingleIssueDownload;
    }

    public void insertValuesIntoSingleDownloadTable(SQLiteDatabase db, ArrayList<SingleDownloadIssueTracker> dIssueList, String tableName){

        if(dIssueList != null){
            for(int i=0; i< dIssueList.size();i++) {
                SingleDownloadIssueTracker issuePageEntry= dIssueList.get(i);
                updateIssuePageEntry(db,issuePageEntry,tableName);
            }
        }

    }


    public boolean updateIssuePageEntry(SQLiteDatabase db, SingleDownloadIssueTracker dIssue, String tableName){

        try{

            ContentValues updateValues = new ContentValues();
            updateValues.put(SingleIssueDownloadEntry.COLUMN_PAGE_NO, dIssue.pageNo);
            updateValues.put(SingleIssueDownloadEntry.COLUMN_URL_PDF_LARGE, dIssue.urlPdfLarge);
            updateValues.put(SingleIssueDownloadEntry.COLUMN_MD5_CHECKSUM_LARGE, dIssue.md5ChecksumLarge);
            updateValues.put(SingleIssueDownloadEntry.COLUMN_DOWNLOADED_LOCATION_PDF_LARGE, dIssue.downloadedLocationPdfLarge);
            updateValues.put(SingleIssueDownloadEntry.COLUMN_DOWNLOAD_STATUS_PDF_LARGE, dIssue.downloadStatusPdfLarge);

            db.insertWithOnConflict(tableName, null, updateValues, SQLiteDatabase.CONFLICT_REPLACE);

            return true;

        }catch(Exception e){

        }

        return false;
    }

    /* Inner class that defines the table contents */
    public static class SingleIssueDownloadEntry {

        public static final String COLUMN_PAGE_NO = "page_no";
        public static final String COLUMN_URL_PDF_LARGE = "url_pdf_large";
        public static final String COLUMN_MD5_CHECKSUM_LARGE = "md5_checksum_large";
        public static final String COLUMN_DOWNLOADED_LOCATION_PDF_LARGE = "downloaded_location_pdf_large";
        public static final String COLUMN_DOWNLOAD_STATUS_PDF_LARGE = "download_status_pdf_large";

    }

}
