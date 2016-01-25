package com.pixelmags.android.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by austincoutinho on 10/10/15.
 */

public class BrandedSQLiteHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BrandedDatabase.db";

    // List of TABLES used in Branded DB
    public static final String TABLE_SUBSCRIPTIONS="Subscriptions";
    public static final String TABLE_MY_SUBSCRIPTIONS="My_Subscriptions";
    public static final String TABLE_ALL_ISSUES="All_Issues";
    public static final String TABLE_MY_ISSUES="My_Issues";
    public static final String TABLE_ISSUE_DATA="Issue_Data";
    public static final String TABLE_PAGE_DATA_PREFIX="Page_Data_";  // for every issue its pages are stored under the table Page_Data_(IssueId)
    public static final String TABLE_ALL_DOWNLOADS ="All_Downloads_Table";
    public static final String TABLE_UNIQUE_ISSUE_DOWNLOAD_TABLE_PREFIX ="Issue_Download_Table_";


    public BrandedSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        //Create Any Basic Tables required here
        //db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Discard the data and start over

        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public void dropAllTables(SQLiteDatabase db){

        // All Tables to be dropped here
        dropTable(db, TABLE_SUBSCRIPTIONS);
    }

    public void dropTable(SQLiteDatabase db, String TABLE){

        String dropTable = "DROP TABLE IF EXISTS" +TABLE;
        db.execSQL(dropTable);
    }

}
