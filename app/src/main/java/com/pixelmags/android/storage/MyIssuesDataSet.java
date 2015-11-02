package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pixelmags.android.datamodels.MyIssue;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 01/11/15.
 */
public class MyIssuesDataSet  extends BrandedSQLiteHelper {


    public MyIssuesDataSet(Context context) {
        super(context);
    }

    public void createTableMyIssues(SQLiteDatabase db){
        db.execSQL(MyIssuesEntry.CREATE_MY_ISSUES_TABLE);
    }

    public void dropMyIssuesTable(SQLiteDatabase db){
        db.execSQL(MyIssuesEntry.DROP_MY_ISSUES_TABLE);
    }


    public void insert_my_issues(SQLiteDatabase db, ArrayList<MyIssue> myIssuesArray){

        // Do batch inserts using Transcations. This is to vastly increase the speed of DB writes

        try{
            // Start the transaction
            db.beginTransaction();

            // clear out any previous values by rebuilding table
            dropMyIssuesTable(db);
            createTableMyIssues(db);


            for(int i=0; i< myIssuesArray.size();i++){

                MyIssue myIssue = myIssuesArray.get(i);

                ContentValues insertValues = new ContentValues();
                insertValues.put(MyIssuesEntry.COLUMN_ISSUE_ID, myIssue.issueID);
                insertValues.put(MyIssuesEntry.COLUMN_MAGAZINE_ID, myIssue.magazineID);
                insertValues.put(MyIssuesEntry.COLUMN_REMOVE_FROM_SALE, (myIssue.removeFromSale) ? 1 : 0 );  // SQLite does not support boolean so convert to int

                db.insert(MyIssuesEntry.MY_ISSUES_TABLE_NAME, null, insertValues);

            }

            //End and close the transaction
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

    }


    public ArrayList<MyIssue> getAllSubscriptions(SQLiteDatabase db){

        ArrayList<MyIssue> myIssuesArray = null;

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    MyIssuesEntry.COLUMN_ISSUE_ID,
                    MyIssuesEntry.COLUMN_MAGAZINE_ID,
                    MyIssuesEntry.COLUMN_REMOVE_FROM_SALE
            };

            // Specify the sort order
            String sortOrder = MyIssuesEntry.COLUMN_ISSUE_ID + " DESC";

            Cursor queryCursor = db.query(
                    MyIssuesEntry.MY_ISSUES_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    null,                                           // The columns for the WHERE clause
                    null,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if(queryCursor != null ){

                myIssuesArray = new ArrayList<MyIssue>();

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {
                    // Extract data.
                    MyIssue myIssue = new MyIssue();

                    myIssue.issueID = queryCursor.getInt(queryCursor.getColumnIndex(MyIssuesEntry.COLUMN_ISSUE_ID));
                    myIssue.magazineID = queryCursor.getInt(queryCursor.getColumnIndex(MyIssuesEntry.COLUMN_MAGAZINE_ID));
                    myIssue.removeFromSale = (queryCursor.getInt(queryCursor.getColumnIndex(MyIssuesEntry.COLUMN_REMOVE_FROM_SALE)) == 1) ? true : false;

                    myIssuesArray.add(myIssue);

                }

                queryCursor.close();
                db.close();
            }

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return myIssuesArray;
    }


    /* Inner class that defines the table contents */
    public static class MyIssuesEntry {

        public static final String MY_ISSUES_TABLE_NAME = BrandedSQLiteHelper.TABLE_MY_ISSUES;
        public static final String COLUMN_ISSUE_ID = "id";
        public static final String COLUMN_MAGAZINE_ID = "magazine_id";
        public static final String COLUMN_REMOVE_FROM_SALE = "remove_from_sale";

        public static final String CREATE_MY_ISSUES_TABLE = "CREATE TABLE "
                + MY_ISSUES_TABLE_NAME
                + "("
                + COLUMN_ISSUE_ID + " INTEGER,"
                + COLUMN_MAGAZINE_ID + " INTEGER,"
                + COLUMN_REMOVE_FROM_SALE + " INTEGER"
                + ")";

        public static final String DROP_MY_ISSUES_TABLE = "DROP TABLE IF EXISTS " + MY_ISSUES_TABLE_NAME;

    }

}
