package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pixelmags.android.datamodels.PreviewImage;

import java.util.ArrayList;

/**
 * Created by Likith.Ts on 31/05/17.
 *
 * SingleIssuePreviewDataSet Contains all the preview pages of an issue and every row would contain all the information associated
 * with a single page.
 */
public class SingleIssuePreviewDataSet extends BrandedSQLiteHelper{

    private String TAG = "SingleIssuePreviewDataSet";

    public SingleIssuePreviewDataSet(Context context) {
        super(context);
    }

    private void createTablePreviewSingleIssueDownload(SQLiteDatabase db, String UNIQUE_ISSUE_DOWNLOAD_TABLE_NAME){

        String CREATE_UNIQUE_PREVIEW_ISSUE_DOWNLOAD_TABLE = "CREATE TABLE IF NOT EXISTS "
                + UNIQUE_ISSUE_DOWNLOAD_TABLE_NAME
                + "("
                + SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_URL + " TEXT,"
                + SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_WIDTH + " INTEGER,"
                + SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_HEIGHT + " INTEGER"
                + ")";

        db.execSQL(CREATE_UNIQUE_PREVIEW_ISSUE_DOWNLOAD_TABLE);

    }

    public void dropUniquePreviewDownloadsTable(SQLiteDatabase db, String UNIQUE_ISSUE_DOWNLOAD_TABLE){

        String DROP_ALL_DOWNLOADS_TABLE = "DROP TABLE IF EXISTS " + UNIQUE_ISSUE_DOWNLOAD_TABLE;

        db.execSQL(DROP_ALL_DOWNLOADS_TABLE);
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

    public void createDownloadTableForPreviewIssue(SQLiteDatabase db, String uniqueDownloadTable){

        try{
            createTablePreviewSingleIssueDownload(db, uniqueDownloadTable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public boolean initFormationOfSingleIssueDownloadTable(SQLiteDatabase db,
                                                           String uniqueTableName,
                                                           ArrayList<PreviewImage> previewImageArrayList)
    {
        try{
            // Start the transaction
            db.beginTransaction();

            // get all the previous stored values
            ArrayList<PreviewImage> previousPreviewIssueDownloadTable = getUniqueSingleIssueDownloadTable(db, uniqueTableName);

            Log.d(TAG,"Previous Preview Issue Download Table is : "+previousPreviewIssueDownloadTable);

            if(previousPreviewIssueDownloadTable == null){
                // if no previous table exists insert the new values
                System.out.println("<<< Unique table does not exist ... creating - "+ uniqueTableName +" >>>");

                // create table if not exists
                createDownloadTableForPreviewIssue(db, uniqueTableName);

                // insert all the values
                insertValuesIntoSinglePreviewDownloadTable(db, previewImageArrayList, uniqueTableName);

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

    public ArrayList<PreviewImage> getUniqueSingleIssueDownloadTable(SQLiteDatabase db, String uniqueDownloadTable){

        ArrayList<PreviewImage> currentSinglePreviewIssueDownload = null;

        try {

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_ID,
                    SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_URL,
                    SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_WIDTH,
                    SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_HEIGHT
            };


//            // Specify the sort order
            String sortOrder =  SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_ID + " ASC";

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

                currentSinglePreviewIssueDownload = new ArrayList<PreviewImage>();

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {

                    PreviewImage issueTracker = new PreviewImage();

                    issueTracker.previewImageURL = queryCursor.getString(queryCursor.getColumnIndex(SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_URL));
                    issueTracker.imageWidth = queryCursor.getInt(queryCursor.getColumnIndex(SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_WIDTH));
                    issueTracker.imageHeight = queryCursor.getInt(queryCursor.getColumnIndex(SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_HEIGHT));

                    currentSinglePreviewIssueDownload.add(issueTracker);

                }

                queryCursor.close();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return currentSinglePreviewIssueDownload;
    }

    public void insertValuesIntoSinglePreviewDownloadTable(SQLiteDatabase db, ArrayList<PreviewImage> previewImages, String tableName){

        if(previewImages != null){
            for(int i=0; i< previewImages.size();i++) {
                PreviewImage previewImage= previewImages.get(i);
                updateIssuePreviewPageEntry(db, previewImage, tableName);
            }
        }

    }

    public boolean updateIssuePreviewPageEntry(SQLiteDatabase db, PreviewImage previewImage, String tableName){

        try{

            ContentValues updateValues = new ContentValues();
            updateValues.put(SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_URL, previewImage.previewImageURL);
            updateValues.put(SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_WIDTH, previewImage.imageWidth);
            updateValues.put(SinglePreviewIssueDownloadEntry.COLUMN_PREVIEW_IMAGE_HEIGHT, previewImage.imageHeight);

            db.insertWithOnConflict(tableName, null, updateValues, SQLiteDatabase.CONFLICT_REPLACE);

            return true;

        }catch(Exception e){

        }

        return false;
    }

    /* Inner class that defines the table contents */
    public static class SinglePreviewIssueDownloadEntry {

        public static final String COLUMN_PREVIEW_ID = "preview_id";
        public static final String COLUMN_PREVIEW_IMAGE_URL = "image_url";
        public static final String COLUMN_PREVIEW_IMAGE_WIDTH = "image_width";
        public static final String COLUMN_PREVIEW_IMAGE_HEIGHT = "image_height";

    }
}
