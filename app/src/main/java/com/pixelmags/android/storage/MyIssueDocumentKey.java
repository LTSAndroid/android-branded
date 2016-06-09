package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pixelmags.android.datamodels.IssueDocumentKey;

import java.util.ArrayList;

/**
 * Created by likith.ts on 5/23/2016.
 */
public class MyIssueDocumentKey  extends BrandedSQLiteHelper {

    private String TAG = "MyIssueDocumentKey";

    public MyIssueDocumentKey(Context context) {
        super(context);
    }

    public void createTableMyIssuesDocumentKey(SQLiteDatabase db){
        db.execSQL(MyIssuesDocumentKeyEntry.CREATE_MY_ISSUES_DOCUMENT_TABLE);
    }

    public void dropMyIssuesDocumentKeyTable(SQLiteDatabase db){
        db.execSQL(MyIssuesDocumentKeyEntry.DROP_MY_ISSUES_DOCUMENT_TABLE);
    }


    public void insert_my_issues_documentKey(SQLiteDatabase db, String issueId, String magazineId, String documentKey, boolean tableExists){

        // Do batch inserts using Transcations. This is to vastly increase the speed of DB writes

        try{
            // Start the transaction
            db.beginTransaction();

            // clear out any previous values by rebuilding table
//            dropMyIssuesDocumentKeyTable(db);

            if(!tableExists){
                createTableMyIssuesDocumentKey(db);
            }

                ContentValues insertValues = new ContentValues();
                insertValues.put(MyIssuesDocumentKeyEntry.COLUMN_ISSUE_ID, issueId);
                insertValues.put(MyIssuesDocumentKeyEntry.COLUMN_MAGAZINE_ID, magazineId);
                insertValues.put(MyIssuesDocumentKeyEntry.COLUMN_DOCUMENT_KEY, documentKey );  // SQLite does not support boolean so convert to int

                db.insert(MyIssuesDocumentKeyEntry.MY_ISSUES_DOCUMENT_TABLE_NAME, null, insertValues);


            //End and close the transaction
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

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
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }


    public ArrayList<IssueDocumentKey> getMyIssuesDocumentKey(SQLiteDatabase db){

        ArrayList<IssueDocumentKey> myIssuesArray = null;

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    MyIssuesDocumentKeyEntry.COLUMN_ISSUE_ID,
                    MyIssuesDocumentKeyEntry.COLUMN_MAGAZINE_ID,
                    MyIssuesDocumentKeyEntry.COLUMN_DOCUMENT_KEY
            };

            // Specify the sort order
            String sortOrder = MyIssuesDocumentKeyEntry.COLUMN_ISSUE_ID + " DESC";

            Cursor queryCursor = db.query(
                    MyIssuesDocumentKeyEntry.MY_ISSUES_DOCUMENT_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    null,                                           // The columns for the WHERE clause
                    null,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if(queryCursor != null ){

                myIssuesArray = new ArrayList<IssueDocumentKey>();

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {
                    // Extract data.
                    IssueDocumentKey myIssueDocumentKey = new IssueDocumentKey();

                    myIssueDocumentKey.issueID = queryCursor.getInt(queryCursor.getColumnIndex(MyIssuesDocumentKeyEntry.COLUMN_ISSUE_ID));
                    myIssueDocumentKey.magazineID = queryCursor.getInt(queryCursor.getColumnIndex(MyIssuesDocumentKeyEntry.COLUMN_MAGAZINE_ID));
                    myIssueDocumentKey.documentKey = queryCursor.getString(queryCursor.getColumnIndex(MyIssuesDocumentKeyEntry.COLUMN_DOCUMENT_KEY));

                    myIssuesArray.add(myIssueDocumentKey);

                }

                queryCursor.close();
            }

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return myIssuesArray;
    }


    /* Inner class that defines the table contents */
    public static class MyIssuesDocumentKeyEntry {

        public static final String MY_ISSUES_DOCUMENT_TABLE_NAME = BrandedSQLiteHelper.TABLE_DOCUMENT_KEY;
        public static final String COLUMN_ISSUE_ID = "id";
        public static final String COLUMN_MAGAZINE_ID = "magazine_id";
        public static final String COLUMN_DOCUMENT_KEY = "document_key";

        public static final String CREATE_MY_ISSUES_DOCUMENT_TABLE = "CREATE TABLE "
                + MY_ISSUES_DOCUMENT_TABLE_NAME
                + "("
                + COLUMN_ISSUE_ID + " TEXT,"
                + COLUMN_MAGAZINE_ID + " TEXT,"
                + COLUMN_DOCUMENT_KEY + " TEXT"
                + ")";

        public static final String DROP_MY_ISSUES_DOCUMENT_TABLE = "DROP TABLE IF EXISTS " + MY_ISSUES_DOCUMENT_TABLE_NAME;

    }

}
