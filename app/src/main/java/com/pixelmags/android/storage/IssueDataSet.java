package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pixelmags.android.datamodels.Issue;
import com.pixelmags.android.datamodels.Page;
import com.pixelmags.android.datamodels.PageTypeImage;
import com.pixelmags.android.ui.AllIssuesFragment;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 06/11/15.
 */
public class IssueDataSet extends BrandedSQLiteHelper{

    private String TAG = "IssueDataSet";

    public IssueDataSet(Context context) {
        super(context);
    }

    public void createTableIssueData(SQLiteDatabase db){
        db.execSQL(IssueEntry.CREATE_ISSUE_TABLE);
    }

    public void dropIssueTableData(SQLiteDatabase db){
        db.execSQL(IssueEntry.DROP_ISSUE_TABLE);
    }

    public void createTablePageData(SQLiteDatabase db, int issueID){
        db.execSQL(PageDataEntry.getSQLForCreatePageDataTable(issueID));
    }

    public void dropTablePageData(SQLiteDatabase db, int issueID){
        db.execSQL(PageDataEntry.getSQLForDropPageDataTable(issueID));
    }



    public void deleteIssueEntry(SQLiteDatabase db, String IssueId){

        // TODO :- delete the issue entry from ISSUE table,
        // IMPORTANT -  Corresponding PAGE DATA table needs to be deleted first

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
            insertValues.put(IssueEntry.COLUMN_PAGE_DATA_TABLE , PageDataEntry.getPageDataTableName(issue));

            try{ // to catch an Primary Key Violation

                // run create table if exists
                createTableIssueData(db);

                // Issue ID is unique in the table so use insertWithOnConflict with CONFLICT_IGNORE
                db.insertWithOnConflict(IssueEntry.ISSUE_TABLE_NAME, null, insertValues, SQLiteDatabase.CONFLICT_REPLACE);

                /*
                To update record with unique key insert with insertWithOnConflict with CONFLICT_IGNORE and then update the table.
                 Useful when you want to update just a single column

                int id = (int) yourdb.insertWithOnConflict("your_table", null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                                yourdb.update("your_table", initialValues, "_id=?", new String[] {1});
                }


                */

                // Once the issue data has been saved, create the Page Data
                saveEveryPage(db, issue);



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

    private void saveEveryPage(SQLiteDatabase db, Issue issue) {


        try{

            // run create table if exists
            dropTablePageData(db, issue.issueID);
            createTablePageData(db, issue.issueID);

            for(int i=0; i< issue.pages.size();i++) {
                Page page = issue.pages.get(i);
                Log.d(TAG,"Page is : " +issue.pages.get(i));

                ContentValues contentValues = new ContentValues();
                contentValues.put(PageDataEntry.COLUMN_PAGE_NO, page.getPageNo());
                contentValues.put(PageDataEntry.COLUMN_PAGE_ID, page.getPageID());
                contentValues.put(PageDataEntry.COLUMN_PAGE_JSON, page.getPageJSONData());

                db.insert(PageDataEntry.getPageDataTableName(issue.issueID), null, contentValues);

                // Check whether first page is downloaded and saved in db

                Log.d(TAG,"Number of pages size is : " +issue.pages.size());

                if(i==1){
                    Log.d(TAG,"After first page is downloaded");
                    AllIssuesFragment allIssuesFragment = new AllIssuesFragment();
                    allIssuesFragment.updateButtonState();
                }else if(i == issue.pages.size()){

                    Log.d(TAG,"After completely downloaded");
                    AllIssuesFragment allIssuesFragment = new AllIssuesFragment();
                    allIssuesFragment.updateButtonState();
                }

            }



        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Issue getIssue(SQLiteDatabase db, String issue_Id){

        Issue singleIssueData = null;

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

            System.out.println("<< COLUMN_ISSUE_ID :: "+ issue_Id +" >>");

            String whereClause = IssueEntry.COLUMN_ISSUE_ID+"=?";
            String [] whereArgs = {issue_Id};

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

                    singleIssueData = new Issue();

                    // Extract data.
                    singleIssueData.issueID = queryCursor.getInt(queryCursor.getColumnIndex(IssueEntry.COLUMN_ISSUE_ID));
                    singleIssueData.magazineID = queryCursor.getInt(queryCursor.getColumnIndex(IssueEntry.COLUMN_MAGAZINE_ID));
                    singleIssueData.title = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_TITLE));
                    singleIssueData.thumbnailURL = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_THUMBNAIL_URL));
                    singleIssueData.issueDate = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_ISSUE_DATE));
                    singleIssueData.pageCount = queryCursor.getInt(queryCursor.getColumnIndex(IssueEntry.COLUMN_PAGE_COUNT));
                    singleIssueData.created = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_CREATED));
                    singleIssueData.lastModified = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_LAST_MODIFIED));
                    singleIssueData.media_format = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_MEDIA_FORMAT));

                    String pagesTable = queryCursor.getString(queryCursor.getColumnIndex(IssueEntry.COLUMN_PAGE_DATA_TABLE));
                    singleIssueData.pages = getPagesFromTable(db, pagesTable);

                }

                queryCursor.close();
            }


        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return singleIssueData;
    }

    private ArrayList<Page> getPagesFromTable(SQLiteDatabase db, String pagesTableName){

        System.out.println("fetching PAGES from Table");

        ArrayList<Page> pages = new ArrayList<Page>();

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    PageDataEntry.COLUMN_PAGE_NO,
                    PageDataEntry.COLUMN_PAGE_ID,
                    PageDataEntry.COLUMN_PAGE_JSON
            };

            // Specify the sort order
            String sortOrder = PageDataEntry.COLUMN_PAGE_NO + " ASC";

            Cursor queryCursor = db.query(
                    pagesTableName,    // The table to query
                    projection,                                     // The columns to return
                    null,                                         // The columns for the WHERE clause
                    null,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if(queryCursor != null ){

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {

                    // Extract data.

                    int pageNo = queryCursor.getInt(queryCursor.getColumnIndex(PageDataEntry.COLUMN_PAGE_NO));
                    String pageId = queryCursor.getString(queryCursor.getColumnIndex(PageDataEntry.COLUMN_PAGE_ID));
                    String pageJson = queryCursor.getString(queryCursor.getColumnIndex(PageDataEntry.COLUMN_PAGE_JSON));

                    PageTypeImage page = new PageTypeImage(pageNo, pageId, pageJson);
                    pages.add(page);

                }

                queryCursor.close();
            }


        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }



        return pages;

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
                + ")";

        public static final String DROP_ISSUE_TABLE = "DROP TABLE IF EXISTS " + ISSUE_TABLE_NAME;

    }

    /* Inner class that defines the PAGE DATA contents */
    public static class PageDataEntry {

        public static final String COLUMN_PAGE_NO = "page_no";
        public static final String COLUMN_PAGE_ID = "page_id";
        public static final String COLUMN_PAGE_JSON = "page_json";

        // update page unit here

        public static String getPageDataTableName(int issueId){
            return TABLE_PAGE_DATA_PREFIX + issueId;
        }

        public static String getPageDataTableName(Issue issue){
            return TABLE_PAGE_DATA_PREFIX + issue.issueID;
        }


        public static String getSQLForCreatePageDataTable(int issueId){
            String createTable  ="CREATE TABLE IF NOT EXISTS "
                    + getPageDataTableName(issueId)
                    + "("
                    + COLUMN_PAGE_NO + " INTEGER PRIMARY KEY ,"      // There should only be one record of an Issue
                    + COLUMN_PAGE_ID + " TEXT,"
                    + COLUMN_PAGE_JSON + " TEXT"
                    + ")";

            return createTable;

        }

        public static String getSQLForDropPageDataTable(int issueId){

            return "DROP TABLE IF EXISTS " + getPageDataTableName(issueId);

        }

    }


}

