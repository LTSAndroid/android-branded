package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pixelmags.android.datamodels.Bookmark;

import java.util.ArrayList;

/**
 * Created by Likith.Ts on 10/05/17.
 */
public class BookmarkDataSet extends BrandedSQLiteHelper{


    public BookmarkDataSet(Context context) {
        super(context);
    }

    public void createTableIssueData(SQLiteDatabase db){
        db.execSQL(IssueBookMarkEntry.CREATE_ISSUE_BOOKMARK_TABLE);
    }

    public void dropIssueTableData(SQLiteDatabase db){
        db.execSQL(IssueBookMarkEntry.DROP_ISSUE_BOOKMARK_TABLE);
    }

    public void insertIssueBookmarkData(SQLiteDatabase db, Bookmark bookmark){

        // Do batch inserts using Transcations. This is to vastly increase the speed of DB writes

        try{
            // Start the transaction
            db.beginTransaction();

            ContentValues insertValues = new ContentValues();
            insertValues.put(IssueBookMarkEntry.COLUMN_ISSUE_ID, bookmark.issueID);
            insertValues.put(IssueBookMarkEntry.COLUMN_PAGE_IMAGE, bookmark.pageImage);
            insertValues.put(IssueBookMarkEntry.COLUMN_PAGE_NUMBER, bookmark.pageNumber);


            try{ // to catch an Primary Key Violation

                // run create table if exists
                createTableIssueData(db);

                db.insert(IssueBookMarkEntry.ISSUE_TABLE_NAME, null, insertValues);

                /*
                To update record with unique key insert with insertWithOnConflict with CONFLICT_IGNORE and then update the table.
                 Useful when you want to update just a single column

                int id = (int) yourdb.insertWithOnConflict("your_table", null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                                yourdb.update("your_table", initialValues, "_id=?", new String[] {1});
                }


                */



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


    public ArrayList<Bookmark> getIssueBookmark(SQLiteDatabase db, String issue_Id){

        ArrayList<Bookmark> bookmarkData  = new ArrayList<Bookmark>();;

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    IssueBookMarkEntry.COLUMN_ISSUE_ID,
                    IssueBookMarkEntry.COLUMN_PAGE_IMAGE,
                    IssueBookMarkEntry.COLUMN_PAGE_NUMBER
            };

            // Specify the sort order
            String sortOrder = IssueBookMarkEntry.COLUMN_ISSUE_ID + " DESC";

            System.out.println("<< COLUMN_ISSUE_ID :: "+ issue_Id +" >>");

            String whereClause = IssueBookMarkEntry.COLUMN_ISSUE_ID+"=?";
            String [] whereArgs = {issue_Id};

            Cursor queryCursor = db.query(
                    IssueBookMarkEntry.ISSUE_TABLE_NAME,    // The table to query
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
                    int issueId = queryCursor.getInt(queryCursor.getColumnIndex(IssueBookMarkEntry.COLUMN_ISSUE_ID));
                    int pageNumber = queryCursor.getInt(queryCursor.getColumnIndex(IssueBookMarkEntry.COLUMN_PAGE_NUMBER));
                    byte[] pageImage = queryCursor.getBlob(queryCursor.getColumnIndex(IssueBookMarkEntry.COLUMN_PAGE_IMAGE));

                    Bookmark bookmarkObject = new Bookmark(issueId,pageNumber,pageImage);

                    bookmarkData.add(bookmarkObject);
                }

                queryCursor.close();
            }


        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return bookmarkData;
    }


    public int deleteBookmark(SQLiteDatabase db, String issue_Id, String pageNo){

        try{
            db.beginTransaction();
            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    IssueBookMarkEntry.COLUMN_ISSUE_ID,
                    IssueBookMarkEntry.COLUMN_PAGE_IMAGE,
                    IssueBookMarkEntry.COLUMN_PAGE_NUMBER
            };

            // Specify the sort order
            String sortOrder = IssueBookMarkEntry.COLUMN_ISSUE_ID + " DESC";

            System.out.println("<< COLUMN_ISSUE_ID :: "+ issue_Id +" >>");

            String whereClause = IssueBookMarkEntry.COLUMN_ISSUE_ID+"=? AND "+IssueBookMarkEntry.COLUMN_PAGE_NUMBER+"=?";
            String [] whereArgs = {issue_Id,pageNo};

            int count = db.delete(IssueBookMarkEntry.ISSUE_TABLE_NAME, whereClause, whereArgs);

            db.setTransactionSuccessful();
            db.endTransaction();

            return count;

//            Cursor queryCursor = db.query(
//                    IssueBookMarkEntry.ISSUE_TABLE_NAME,    // The table to query
//                    projection,                                     // The columns to return
//                    whereClause,                                         // The columns for the WHERE clause
//                    whereArgs,                                           // The values for the WHERE clause
//                    null,
//                    null,
//                    sortOrder                                       // The sort order
//            );


//            if(queryCursor != null ){


//                //queryCursor.getCount();
//                while (queryCursor.moveToNext()) {
//
//                    // Extract data.
//                    int issueId = queryCursor.getInt(queryCursor.getColumnIndex(IssueBookMarkEntry.COLUMN_ISSUE_ID));
//                    int pageNumber = queryCursor.getInt(queryCursor.getColumnIndex(IssueBookMarkEntry.COLUMN_PAGE_NUMBER));
//
//                    Bookmark bookmarkObject = new Bookmark(issueId,pageNumber);
//
//                    bookmarkData.remove(bookmarkObject);
//                }




//                queryCursor.close();
//            }


        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return 0;

    }


    public int deleteBookmarkByIssueId(SQLiteDatabase db, String issue_Id){

        try{
            db.beginTransaction();
            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    IssueBookMarkEntry.COLUMN_ISSUE_ID,
                    IssueBookMarkEntry.COLUMN_PAGE_IMAGE,
                    IssueBookMarkEntry.COLUMN_PAGE_NUMBER
            };

            // Specify the sort order
            String sortOrder = IssueBookMarkEntry.COLUMN_ISSUE_ID + " DESC";

            System.out.println("<< COLUMN_ISSUE_ID :: "+ issue_Id +" >>");

            String whereClause = IssueBookMarkEntry.COLUMN_ISSUE_ID+"=?";
            String [] whereArgs = {issue_Id};

            int count = db.delete(IssueBookMarkEntry.ISSUE_TABLE_NAME, whereClause, whereArgs);

            db.setTransactionSuccessful();
            db.endTransaction();

            return count;

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return 0;

    }


    /* Inner class that defines the ISSUE table contents */
    public static class IssueBookMarkEntry {

        public static final String ISSUE_TABLE_NAME = BrandedSQLiteHelper.TABLE_ISSUE_BOOKMARK_DATA;
        public static final String COLUMN_ISSUE_ID = "issue_id";
        public static final String COLUMN_PAGE_NUMBER = "page_np";
        public static final String COLUMN_PAGE_IMAGE = "page_image";


        public static final String CREATE_ISSUE_BOOKMARK_TABLE = "CREATE TABLE IF NOT EXISTS "
                + ISSUE_TABLE_NAME
                + "("
                + COLUMN_ISSUE_ID + " INTEGER ,"      // There should only be one record of an Issue
                + COLUMN_PAGE_IMAGE + " BLOB ,"
                + COLUMN_PAGE_NUMBER + " INTEGER"
                + ")";

        public static final String DROP_ISSUE_BOOKMARK_TABLE = "DROP TABLE IF EXISTS " + ISSUE_TABLE_NAME;

    }

}
