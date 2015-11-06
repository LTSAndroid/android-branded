package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.Page;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 06/11/15.
 */
public class IssueDataSet extends BrandedSQLiteHelper{

    public IssueDataSet(Context context) {
        super(context);
    }

    public void createTableIssueData(SQLiteDatabase db){
        db.execSQL(IssueEntry.CREATE_ISSUE_TABLE);
    }

    public void dropIssueTableData(SQLiteDatabase db){
        db.execSQL(IssueEntry.DROP_ISSUE_TABLE);
    }

    public void deleteIssueEntry(SQLiteDatabase db, String IssueId){

        // delete the issue entry from ISSUE table,
        // IMPORTANT -  Corresponding PAGE DATA table needs to be deleted first

    }

    public String getPageDataTableName(Issue issue){
        return TABLE_PAGE_DATA_PREFIX + issue.issueID;
    }



    /* Inner class that defines the ISSUE table contents */
    public static class IssueEntry {

        public static final String ISSUE_TABLE_NAME = BrandedSQLiteHelper.TABLE_ISSUE_DATA;
        public static final String COLUMN_ISSUE_ID = "issue_id";
        public static final String COLUMN_MAGAZINE_ID = "magazine_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_ISSUE_DATE = "issue_date";
        public static final String COLUMN_PAGE_COUNT = "page_count";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_LAST_MODIFIED = "last_modified";
        public static final String COLUMN_MEDIA_FORMAT = "media_format";
        public static final String COLUMN_PAGE_DATA_TABLE = "page_data_table";

        public static final String CREATE_ISSUE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + ISSUE_TABLE_NAME
                + "("
                + COLUMN_ISSUE_ID + " INTEGER PRIMARY KEY ,"      // There should only be one record of an Issue
                + COLUMN_MAGAZINE_ID + " INTEGER,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_THUMBNAIL_URL + " TEXT,"
                + COLUMN_ISSUE_DATE + " TEXT,"
                + COLUMN_PAGE_COUNT + " INTEGER,"
                + COLUMN_CREATED + " TEXT,"
                + COLUMN_LAST_MODIFIED + " TEXT,"
                + COLUMN_MEDIA_FORMAT + " TEXT,"
                + COLUMN_PAGE_DATA_TABLE + " TEXT"
                + ")"; ;

        public static final String DROP_ISSUE_TABLE = "DROP TABLE IF EXISTS " + ISSUE_TABLE_NAME;

    }

    /* Inner class that defines the PAGE DATA contents */
    public static class PageDataEntry {

        public static final String COLUMN_PAGE_NO = "page_no";
        public static final String COLUMN_PAGE_ID = "page_id";
        public static final String COLUMN_PAGE_JSON = "page_json";

    }


    public void insertIssueData(SQLiteDatabase db, Issue issue){

        // Do batch inserts using Transcations. This is to vastly increase the speed of DB writes

        try{
            // Start the transaction
            db.beginTransaction();

            ContentValues insertValues = new ContentValues();
            insertValues.put(IssueEntry.COLUMN_ISSUE_ID, issue.issueID);
            insertValues.put(IssueEntry.COLUMN_MAGAZINE_ID, issue.magazineID);
            insertValues.put(IssueEntry.COLUMN_TITLE, issue.title);
            insertValues.put(IssueEntry.COLUMN_THUMBNAIL_URL, issue.thumbnailURL);
            insertValues.put(IssueEntry.COLUMN_ISSUE_DATE, issue.issueDate);
            insertValues.put(IssueEntry.COLUMN_PAGE_COUNT, issue.pageCount);
            insertValues.put(IssueEntry.COLUMN_CREATED, issue.created);
            insertValues.put(IssueEntry.COLUMN_LAST_MODIFIED, issue.lastModified);
            insertValues.put(IssueEntry.COLUMN_MEDIA_FORMAT , issue.media_format);
            insertValues.put(IssueEntry.COLUMN_PAGE_DATA_TABLE , getPageDataTableName(issue));

            try{ // to catch an Primary Key Violation

                // run create table if exists
                createTableIssueData(db);

                // TODO : check if a record exits for an issue and act appropriately

                // insert the record
                db.insert(IssueEntry.ISSUE_TABLE_NAME, null, insertValues);

            }catch(Exception e){
                e.printStackTrace();
            }


            //End and close the transaction
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

    }

    public Issue getIssue(SQLiteDatabase db, String issueId){

        Issue issueData = new Issue();

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    IssueEntry.COLUMN_ISSUE_ID,
                    IssueEntry.COLUMN_MAGAZINE_ID,
                    IssueEntry.COLUMN_TITLE,
                    IssueEntry.COLUMN_THUMBNAIL_URL,
                    IssueEntry.COLUMN_ISSUE_DATE,
                    IssueEntry.COLUMN_PAGE_COUNT,
                    IssueEntry.COLUMN_CREATED,
                    IssueEntry.COLUMN_LAST_MODIFIED,
                    IssueEntry.COLUMN_MEDIA_FORMAT,
                    IssueEntry.COLUMN_PAGE_DATA_TABLE
             };

            // Specify the sort order
            String sortOrder = IssueEntry.COLUMN_ISSUE_ID + " DESC";

            String whereClause = IssueEntry.COLUMN_ISSUE_ID+"=?";
            String [] whereArgs = {issueId};

            Cursor queryCursor = db.query(
                    IssueEntry.ISSUE_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    whereClause,                                         // The columns for the WHERE clause
                    whereArgs,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if(queryCursor != null ){

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {

                    // Extract data.

                    issueData.issueID = queryCursor.getInt(queryCursor.getColumnIndex(IssueEntry.COLUMN_ISSUE_ID));
                    issueData.magazineID = queryCursor.getInt(queryCursor.getColumnIndex(IssueEntry.COLUMN_MAGAZINE_ID));
                    issueData.title = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_TITLE));
                    issueData.thumbnailURL = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_THUMBNAIL_URL));
                    issueData.issueDate = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_ISSUE_DATE));
                    issueData.pageCount = queryCursor.getInt(queryCursor.getColumnIndex(IssueEntry.COLUMN_PAGE_COUNT));
                    issueData.created = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_CREATED));
                    issueData.lastModified = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_LAST_MODIFIED));
                    issueData.media_format = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_MEDIA_FORMAT));

                    String pagesTable = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_PAGE_DATA_TABLE));

                    issueData.pages = getPagesFromTable(pagesTable);

                }

                queryCursor.close();
                db.close();
            }


        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return issueData;
    }


    private ArrayList<Page> getPagesFromTable(String pagesTableName){

        ArrayList<Page> pages = new ArrayList<Page>();

        return pages;

    }


}

