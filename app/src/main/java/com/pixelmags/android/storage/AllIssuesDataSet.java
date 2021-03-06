package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pixelmags.android.datamodels.Magazine;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 23/10/15.
 */
public class AllIssuesDataSet extends BrandedSQLiteHelper{

    private String TAG="AllIssueDateSet";
    public AllIssuesDataSet(Context context) {
        super(context);
    }

    public void createTableAllIssues(SQLiteDatabase db){
        db.execSQL(AllIssuesEntry.CREATE_ALL_ISSUES_TABLE);
    }

    public void dropAllIssuesTable(SQLiteDatabase db){
        db.execSQL(AllIssuesEntry.DROP_ALL_ISSUES_TABLE);
    }

    public boolean isTableExists(SQLiteDatabase db)
    {

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table",  AllIssuesEntry.ALL_ISSUES_TABLE_NAME});
        if (!cursor.moveToFirst()) {
            db.close();
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        db.close();
        cursor.close();
        return count > 0;
    }

    public void insert_all_issues_data(SQLiteDatabase db, ArrayList<Magazine> magazinesArray){

        // Do batch inserts using Transcations. This is to vastly increase the speed of DB writes

        try{
            // Start the transaction
            db.beginTransaction();


            // clear out any previous values by rebuilding table
            dropAllIssuesTable(db);
            createTableAllIssues(db);

            Log.d(TAG, "Magazine array is inside the insert method : " + magazinesArray);
            Log.d(TAG,"Magazine array size is inside the insert method : " +magazinesArray.size());
            for(int i=0; i< magazinesArray.size();i++){

                Magazine mag = magazinesArray.get(i);
                Log.d(TAG,"Magazine date is : " +mag.issueDate);




                ContentValues insertValues = new ContentValues();
                insertValues.put(AllIssuesEntry.COLUMN_ID, mag.id);
                insertValues.put(AllIssuesEntry.COLUMN_TITLE, mag.title);
                insertValues.put(AllIssuesEntry.COLUMN_SYNOPSIS, mag.synopsis);
                insertValues.put(AllIssuesEntry.COLUMN_ANDROID_STORE_SKU, mag.android_store_sku);
                insertValues.put(AllIssuesEntry.COLUMN_PRICE, mag.price);
                insertValues.put(AllIssuesEntry.COLUMN_TYPE, mag.type);
                insertValues.put(AllIssuesEntry.COLUMN_MANIFEST , mag.manifest);
                insertValues.put(AllIssuesEntry.COLUMN_THUMBNAIL_URL, mag.thumbnailURL);
                insertValues.put(AllIssuesEntry.COLUMN_IS_PUBLISHED, (mag.isPublished) ? 1 : 0 );
                insertValues.put(AllIssuesEntry.COLUMN_REMOVE_FROM_SALE, (mag.removeFromSale) ? 1 : 0 );  // SQLite does not support boolean so convert to int
                insertValues.put(AllIssuesEntry.COLUMN_AGE_RESTRICTION, mag.ageRestriction);
                insertValues.put(AllIssuesEntry.COLUMN_EXCLUDE_FROM_SUBSCRIPTION, mag.exclude_from_subscription);
                insertValues.put(AllIssuesEntry.COLUMN_INTERNAL_SAVED_URL, mag.thumbnailDownloadedInternalPath);
                insertValues.put(AllIssuesEntry.COLUMN_IS_THUMBNAIL_DOWNLOADED, (mag.isThumbnailDownloaded) ? 1 : 0 );
                insertValues.put(AllIssuesEntry.COLUMN_IS_ISSUE_OWNED, (mag.isIssueOwnedByUser) ? 1 : 0 );
                insertValues.put(AllIssuesEntry.COLUMN_MEDIA_FORMAT, mag.mediaFormat);
                insertValues.put(AllIssuesEntry.COLUMN_PAYMENT_PROVIDER, mag.paymentProvider);

                db.insert(AllIssuesEntry.ALL_ISSUES_TABLE_NAME, null, insertValues);

            }


            //End and close the transaction
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }


    }

    public ArrayList<Magazine> getAllIssues(SQLiteDatabase db){

        ArrayList<Magazine> magazinesArray = null;

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    AllIssuesEntry.COLUMN_ID,
                    AllIssuesEntry.COLUMN_TITLE,
                    AllIssuesEntry.COLUMN_SYNOPSIS,
                    AllIssuesEntry.COLUMN_ANDROID_STORE_SKU,
                    AllIssuesEntry.COLUMN_PRICE,
                    AllIssuesEntry.COLUMN_TYPE,
                    AllIssuesEntry.COLUMN_MANIFEST,
                    AllIssuesEntry.COLUMN_THUMBNAIL_URL,
                    AllIssuesEntry.COLUMN_IS_PUBLISHED,
                    AllIssuesEntry.COLUMN_REMOVE_FROM_SALE,
                    AllIssuesEntry.COLUMN_AGE_RESTRICTION,
                    AllIssuesEntry.COLUMN_EXCLUDE_FROM_SUBSCRIPTION,
                    AllIssuesEntry.COLUMN_INTERNAL_SAVED_URL,
                    AllIssuesEntry.COLUMN_IS_THUMBNAIL_DOWNLOADED,
                    AllIssuesEntry.COLUMN_IS_ISSUE_OWNED,
                    AllIssuesEntry.COLUMN_MEDIA_FORMAT,
                    AllIssuesEntry.COLUMN_PAYMENT_PROVIDER
            };

            // Specify the sort order
            String sortOrder = AllIssuesEntry.COLUMN_ID + " DESC";

            Cursor queryCursor = db.query(
                    AllIssuesEntry.ALL_ISSUES_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    null,                                           // The columns for the WHERE clause
                    null,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if(queryCursor != null ){

                magazinesArray = new ArrayList<Magazine>();

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {
                    // Extract data.
                    Magazine mag = new Magazine();

                    mag.id = queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_ID));
                    mag.title = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_TITLE));
                    mag.synopsis = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_SYNOPSIS));
                    mag.android_store_sku = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_ANDROID_STORE_SKU));
                    mag.price = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_PRICE));
                    mag.type = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_TYPE));
                    mag.manifest = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_MANIFEST));
                    mag.thumbnailURL = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_THUMBNAIL_URL));
                    mag.isPublished = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_IS_PUBLISHED)) == 1) ? true : false;
                    mag.removeFromSale = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_REMOVE_FROM_SALE)) == 1) ? true : false;
                    mag.ageRestriction = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_AGE_RESTRICTION));
                    mag.exclude_from_subscription = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_EXCLUDE_FROM_SUBSCRIPTION));
                    mag.thumbnailDownloadedInternalPath = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_INTERNAL_SAVED_URL));
                    mag.isThumbnailDownloaded = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_IS_THUMBNAIL_DOWNLOADED)) == 1) ? true : false;
                    mag.isIssueOwnedByUser = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_IS_ISSUE_OWNED)) == 1) ? true : false;
                    mag.mediaFormat = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_MEDIA_FORMAT));
                    mag.paymentProvider = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_PAYMENT_PROVIDER));

                    magazinesArray.add(mag);

                }

                queryCursor.close();

            }else{

                System.out.println("NULL result in getSingleIssue()");
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return magazinesArray;
    }

    public ArrayList<Magazine> getAllIssuesOnly(SQLiteDatabase db){

        ArrayList<Magazine> magazinesArray = null;

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    AllIssuesEntry.COLUMN_ID,
                    AllIssuesEntry.COLUMN_TITLE,
                    AllIssuesEntry.COLUMN_SYNOPSIS,
                    AllIssuesEntry.COLUMN_ANDROID_STORE_SKU,
                    AllIssuesEntry.COLUMN_PRICE,
                    AllIssuesEntry.COLUMN_TYPE,
                    AllIssuesEntry.COLUMN_MANIFEST,
                    AllIssuesEntry.COLUMN_THUMBNAIL_URL,
                    AllIssuesEntry.COLUMN_IS_PUBLISHED,
                    AllIssuesEntry.COLUMN_REMOVE_FROM_SALE,
                    AllIssuesEntry.COLUMN_AGE_RESTRICTION,
                    AllIssuesEntry.COLUMN_EXCLUDE_FROM_SUBSCRIPTION,
                    AllIssuesEntry.COLUMN_INTERNAL_SAVED_URL,
                    AllIssuesEntry.COLUMN_IS_THUMBNAIL_DOWNLOADED,
                    AllIssuesEntry.COLUMN_IS_ISSUE_OWNED,
                    AllIssuesEntry.COLUMN_MEDIA_FORMAT,
                    AllIssuesEntry.COLUMN_PAYMENT_PROVIDER
            };

            // Specify the sort order
            String sortOrder = AllIssuesEntry.COLUMN_ID + " DESC";

            String whereClause = AllIssuesEntry.COLUMN_TYPE+"=?";
            String[] whereArgs = {"issue"};

            Cursor queryCursor = db.query(
                    AllIssuesEntry.ALL_ISSUES_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    whereClause,                                           // The columns for the WHERE clause
                    whereArgs,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if(queryCursor != null ){

                magazinesArray = new ArrayList<Magazine>();

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {
                    // Extract data.
                    Magazine mag = new Magazine();

                    mag.id = queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_ID));
                    mag.title = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_TITLE));
                    mag.synopsis = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_SYNOPSIS));
                    mag.android_store_sku = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_ANDROID_STORE_SKU));
                    mag.price = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_PRICE));
                    mag.type = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_TYPE));
                    mag.manifest = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_MANIFEST));
                    mag.thumbnailURL = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_THUMBNAIL_URL));
                    mag.isPublished = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_IS_PUBLISHED)) == 1) ? true : false;
                    mag.removeFromSale = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_REMOVE_FROM_SALE)) == 1) ? true : false;
                    mag.ageRestriction = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_AGE_RESTRICTION));
                    mag.exclude_from_subscription = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_EXCLUDE_FROM_SUBSCRIPTION));
                    mag.thumbnailDownloadedInternalPath = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_INTERNAL_SAVED_URL));
                    mag.isThumbnailDownloaded = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_IS_THUMBNAIL_DOWNLOADED)) == 1) ? true : false;
                    mag.isIssueOwnedByUser = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_IS_ISSUE_OWNED)) == 1) ? true : false;
                    mag.mediaFormat = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_MEDIA_FORMAT));
                    mag.paymentProvider = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_PAYMENT_PROVIDER));

                    magazinesArray.add(mag);

                }

                queryCursor.close();

            }else{

                System.out.println("NULL result in getSingleIssue()");
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return magazinesArray;
    }

    public Magazine getSingleIssue(SQLiteDatabase db, String IssueID){

        Magazine mMagazine = null;

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    AllIssuesEntry.COLUMN_ID,
                    AllIssuesEntry.COLUMN_TITLE,
                    AllIssuesEntry.COLUMN_SYNOPSIS,
                    AllIssuesEntry.COLUMN_ANDROID_STORE_SKU,
                    AllIssuesEntry.COLUMN_PRICE,
                    AllIssuesEntry.COLUMN_TYPE,
                    AllIssuesEntry.COLUMN_MANIFEST,
                    AllIssuesEntry.COLUMN_THUMBNAIL_URL,
                    AllIssuesEntry.COLUMN_IS_PUBLISHED,
                    AllIssuesEntry.COLUMN_REMOVE_FROM_SALE,
                    AllIssuesEntry.COLUMN_AGE_RESTRICTION,
                    AllIssuesEntry.COLUMN_EXCLUDE_FROM_SUBSCRIPTION,
                    AllIssuesEntry.COLUMN_INTERNAL_SAVED_URL,
                    AllIssuesEntry.COLUMN_IS_THUMBNAIL_DOWNLOADED,
                    AllIssuesEntry.COLUMN_IS_ISSUE_OWNED,
                    AllIssuesEntry.COLUMN_MEDIA_FORMAT,
                    AllIssuesEntry.COLUMN_PAYMENT_PROVIDER
            };

            // Specify the sort order
            String sortOrder = AllIssuesEntry.COLUMN_ID + " DESC";

            String whereClause = AllIssuesEntry.COLUMN_ID+"=?";
            String [] whereArgs = {IssueID};

            Cursor queryCursor = db.query(
                    AllIssuesEntry.ALL_ISSUES_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    whereClause,                                           // The columns for the WHERE clause
                    whereArgs,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if(queryCursor != null ){

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {

                    mMagazine = new Magazine();

                    mMagazine.id = queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_ID));
                    mMagazine.title = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_TITLE));
                    mMagazine.synopsis = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_SYNOPSIS));
                    mMagazine.android_store_sku = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_ANDROID_STORE_SKU));
                    mMagazine.price = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_PRICE));
                    mMagazine.type = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_TYPE));
                    mMagazine.manifest = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_MANIFEST));
                    mMagazine.thumbnailURL = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_THUMBNAIL_URL));
                    mMagazine.isPublished = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_IS_PUBLISHED)) == 1) ? true : false;
                    mMagazine.removeFromSale = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_REMOVE_FROM_SALE)) == 1) ? true : false;
                    mMagazine.ageRestriction = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_AGE_RESTRICTION));
                    mMagazine.exclude_from_subscription = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_EXCLUDE_FROM_SUBSCRIPTION));
                    mMagazine.thumbnailDownloadedInternalPath = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_INTERNAL_SAVED_URL));
                    mMagazine.isThumbnailDownloaded = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_IS_THUMBNAIL_DOWNLOADED)) == 1) ? true : false;
                    mMagazine.isIssueOwnedByUser = (queryCursor.getInt(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_IS_ISSUE_OWNED)) == 1) ? true : false;
                    mMagazine.mediaFormat = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_MEDIA_FORMAT));
                    mMagazine.paymentProvider = queryCursor.getString(queryCursor.getColumnIndex(AllIssuesEntry.COLUMN_PAYMENT_PROVIDER));

                }

                queryCursor.close();

            }else{
                System.out.println("NULL result in getSingleIssue()");
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return mMagazine;
    }

    /* Inner class that defines the table contents */
    public static class AllIssuesEntry {

        public static final String ALL_ISSUES_TABLE_NAME = BrandedSQLiteHelper.TABLE_ALL_ISSUES;
        public static final String COLUMN_ID = "magazine_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_ANDROID_STORE_SKU = "android_store_sku";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MANIFEST = "manifest";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_IS_PUBLISHED = "is_published";
        public static final String COLUMN_REMOVE_FROM_SALE = "remove_from_sale";
        public static final String COLUMN_AGE_RESTRICTION = "age_restriction";
        public static final String COLUMN_EXCLUDE_FROM_SUBSCRIPTION = "exclude_from_subscription";
        public static final String COLUMN_INTERNAL_SAVED_URL = "internal_saved_url";
        public static final String COLUMN_IS_THUMBNAIL_DOWNLOADED = "is_thumbnail_downloaded";
        public static final String COLUMN_MEDIA_FORMAT = "media_format";
        public static final String COLUMN_IS_ISSUE_OWNED="is_issue_owned";
        public static final String COLUMN_PAYMENT_PROVIDER="payment";

        public static final String CREATE_ALL_ISSUES_TABLE = "CREATE TABLE "
                + ALL_ISSUES_TABLE_NAME
                + "("
                + COLUMN_ID + " INTEGER,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_SYNOPSIS + " TEXT,"
                + COLUMN_ANDROID_STORE_SKU + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_MANIFEST + " TEXT,"
                + COLUMN_THUMBNAIL_URL + " TEXT,"
                + COLUMN_IS_PUBLISHED + " INTEGER,"
                + COLUMN_REMOVE_FROM_SALE + " INTEGER,"
                + COLUMN_AGE_RESTRICTION + " TEXT,"
                + COLUMN_EXCLUDE_FROM_SUBSCRIPTION + " TEXT,"
                + COLUMN_INTERNAL_SAVED_URL + " TEXT,"
                + COLUMN_IS_THUMBNAIL_DOWNLOADED + " INTEGER,"
                + COLUMN_IS_ISSUE_OWNED + " INTEGER,"
                + COLUMN_MEDIA_FORMAT + " TEXT,"
                + COLUMN_PAYMENT_PROVIDER+ " TEXT"
                + ")";

        public static final String DROP_ALL_ISSUES_TABLE = "DROP TABLE IF EXISTS " + ALL_ISSUES_TABLE_NAME;

    }


}
