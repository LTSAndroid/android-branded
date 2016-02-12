package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
 * SingleIssueDownloadDataSet Contains all the pages of an issue and every row would contain all the information associated
 * with a single page.
 *
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
                                                            ArrayList<SingleDownloadIssueTracker> newSingleIssueDownloadTable)
    {
        try{
            // Start the transaction
            db.beginTransaction();


            // get all the previous stored values
            ArrayList<SingleDownloadIssueTracker> previousIssueDownloadTable = getUniqueSingleIssueDownloadTable(db, allDownloadTrackerUnit.uniqueIssueDownloadTable);

            if(previousIssueDownloadTable == null){
                // if no previous table exists insert the new values
                System.out.println("<<< Unique table does not exist ... creating - "+ allDownloadTrackerUnit.uniqueIssueDownloadTable +" >>>");

                // create table if not exists
                createDownloadTableForIssue(db, allDownloadTrackerUnit.uniqueIssueDownloadTable);

                // insert all the values
                insertValuesIntoSingleDownloadTable(db, newSingleIssueDownloadTable, allDownloadTrackerUnit.uniqueIssueDownloadTable);

            }else {
                // compare every record and
                // replace the ones whose md5's do not match
                // keep the old ones whose md5's match
                // add in any new pages

                System.out.println("<<< Unique table exists ... comparing records >>>");

                for (int i = 0; i < newSingleIssueDownloadTable.size(); i++) {

                    SingleDownloadIssueTracker current = newSingleIssueDownloadTable.get(i);
                    SingleDownloadIssueTracker prev = null;

                    // get the corresponding page for current
                    for (int j = 0; j < previousIssueDownloadTable.size(); j++) {
                        SingleDownloadIssueTracker temp = previousIssueDownloadTable.get(j);
                        if (temp.pageNo == current.pageNo) {
                            prev = previousIssueDownloadTable.get(j);
                            break;
                        }
                    }

                    if (prev == null) {
                        // there is no corresponding page, so insert it as it is i.e leave the record alone
                       // System.out.println("<<< no corresponding page for pageNo - "+ current.pageNo +" >>>");
                    } else {

                        if (current.md5ChecksumLarge.equals(prev.md5ChecksumLarge)) {
                            // if their md5 matches replace the page data with the one already there
                            newSingleIssueDownloadTable.set(i, prev);
                            //System.out.println("<<< md5 matches for page " + newSingleIssueDownloadTable.get(i).pageNo + ". downloadStatus = " + newSingleIssueDownloadTable.get(i).downloadStatusPdfLarge + ">>>");

                        } else {

                            //System.out.println("<<< md5 Does not match for page " + newSingleIssueDownloadTable.get(i).pageNo);
                            // the md5 do not match so do nothing
                        }
                    }
                }

                // now update all the values
                insertValuesIntoSingleDownloadTable(db, newSingleIssueDownloadTable, allDownloadTrackerUnit.uniqueIssueDownloadTable);

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


    public int getTotalPages(SQLiteDatabase db, String uniqueDownloadTable){

        int numRows = 0;
        try{
            numRows = (int) DatabaseUtils.queryNumEntries(db, uniqueDownloadTable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return numRows;
    }


    public int getCountOfPagesPendingDownload(SQLiteDatabase db, String uniqueDownloadTable){

        // fetch count of all the issues that still are pending download i.e. status = DOWNLOAD_STATUS_PENDING

        int count = -1;

        try {

              Cursor cursor= db.rawQuery("SELECT COUNT (*) FROM " + uniqueDownloadTable + " WHERE " + SingleIssueDownloadEntry.COLUMN_DOWNLOAD_STATUS_PDF_LARGE + "=?",
                      new String[] {String.valueOf(DOWNLOAD_STATUS_PENDING)});

              if(null != cursor)

                  if(cursor.getCount() > 0){
                      cursor.moveToFirst();
                      count = cursor.getInt(0);

                  }
              cursor.close();

        }catch (Exception e){
                 e.printStackTrace();
        }

        return count;

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
