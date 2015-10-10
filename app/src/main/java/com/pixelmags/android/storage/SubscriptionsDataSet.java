package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pixelmags.android.datamodels.Subscription;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 10/10/15.
 *
 * All accesors and functions to create/insert/update/delete Subscriptions in db.
 *
 */
public class SubscriptionsDataSet extends BrandedSQLiteHelper{


    public SubscriptionsDataSet(Context context) {
        super(context);
    }

    public void createTableSubscriptions(SQLiteDatabase db){
        db.execSQL(SubscriptionsEntry.CREATE_SUBSCRIPTIONS_TABLE);
    }

    public void dropSubscriptionsTable(SQLiteDatabase db){
        db.execSQL(SubscriptionsEntry.DROP_SUBSCRIPTIONS_TABLE);
    }

    public void insert_all_subscriptions(SQLiteDatabase db, ArrayList<Subscription> subscriptionsArray){

        // Do batch inserts using Transcations. This is to vastly increase the speed of DB writes

        try{
            // Start the transaction
            db.beginTransaction();


            // clear out any previous values by rebuilding table
            dropSubscriptionsTable(db);
            createTableSubscriptions(db);


            for(int i=0; i< subscriptionsArray.size();i++){

                Subscription sub = subscriptionsArray.get(i);

                ContentValues insertValues = new ContentValues();
                insertValues.put(SubscriptionsEntry.COLUMN_ID, sub.id);
                insertValues.put(SubscriptionsEntry.COLUMN_MAGAZINE_ID, sub.magazine_id);
                insertValues.put(SubscriptionsEntry.COLUMN_SYNOPSIS, sub.synopsis);
                insertValues.put(SubscriptionsEntry.COLUMN_ANDROID_STORE_SKU, sub.android_store_sku);
                insertValues.put(SubscriptionsEntry.COLUMN_PRICE, sub.price);
                insertValues.put(SubscriptionsEntry.COLUMN_PAYMENT_PROVIDER, sub.payment_provider);
                insertValues.put(SubscriptionsEntry.COLUMN_PARENT_SKU_ID , sub.parent_sku_id);
                insertValues.put(SubscriptionsEntry.COLUMN_THUMBNAIL_URL, sub.thumbnail_url);
                insertValues.put(SubscriptionsEntry.COLUMN_CREDITS_INCLUDED, sub.credits_included);
                insertValues.put(SubscriptionsEntry.COLUMN_DESCRIPTION, sub.description);
                insertValues.put(SubscriptionsEntry.COLUMN_REMOVE_FROM_SALE, (sub.remove_from_sale) ? 1 : 0 );  // SQLite does not support boolean so convert to int
                insertValues.put(SubscriptionsEntry.COLUMN_AUTO_RENEWABLE, (sub.auto_renewable) ? 1 : 0 );

                db.insert(SubscriptionsEntry.SUBSCRIPTIONS_TABLE_NAME, null, insertValues);

            }


            //End and close the transaction
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }


    }


    public ArrayList<Subscription> getAllSubscriptions(SQLiteDatabase db){

        ArrayList<Subscription> subscriptionsArray = null;

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                SubscriptionsEntry.COLUMN_ID,
                SubscriptionsEntry.COLUMN_MAGAZINE_ID,
                SubscriptionsEntry.COLUMN_SYNOPSIS,
                SubscriptionsEntry.COLUMN_ANDROID_STORE_SKU,
                SubscriptionsEntry.COLUMN_PRICE,
                SubscriptionsEntry.COLUMN_PAYMENT_PROVIDER,
                SubscriptionsEntry.COLUMN_PARENT_SKU_ID,
                SubscriptionsEntry.COLUMN_THUMBNAIL_URL,
                SubscriptionsEntry.COLUMN_CREDITS_INCLUDED,
                SubscriptionsEntry.COLUMN_DESCRIPTION,
                SubscriptionsEntry.COLUMN_REMOVE_FROM_SALE,
                SubscriptionsEntry.COLUMN_AUTO_RENEWABLE
            };

            // Specify the sort order
            String sortOrder = SubscriptionsEntry.COLUMN_PRICE + " DESC";

            Cursor queryCursor = db.query(
                    SubscriptionsEntry.SUBSCRIPTIONS_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    null,                                           // The columns for the WHERE clause
                    null,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if(queryCursor != null ){

                subscriptionsArray = new ArrayList<Subscription>();

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {
                    // Extract data.
                    Subscription sub = new Subscription();

                    sub.id = queryCursor.getInt(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_ID));
                    sub.magazine_id = queryCursor.getInt(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_MAGAZINE_ID));
                    sub.synopsis = queryCursor.getString(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_SYNOPSIS));
                    sub.android_store_sku = queryCursor.getString(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_ANDROID_STORE_SKU));
                    sub.price = queryCursor.getDouble(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_PRICE));
                    sub.payment_provider = queryCursor.getString(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_PAYMENT_PROVIDER));
                    sub.parent_sku_id = queryCursor.getString(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_PARENT_SKU_ID));
                    sub.thumbnail_url = queryCursor.getString(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_THUMBNAIL_URL));
                    sub.credits_included = queryCursor.getInt(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_CREDITS_INCLUDED));
                    sub.description = queryCursor.getString(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_DESCRIPTION));
                    sub.remove_from_sale = (queryCursor.getInt(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_REMOVE_FROM_SALE)) == 1) ? true : false;
                    sub.auto_renewable = (queryCursor.getInt(queryCursor.getColumnIndex(SubscriptionsEntry.COLUMN_AUTO_RENEWABLE)) == 1) ? true : false;

                    subscriptionsArray.add(sub);

                }
            }


        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return subscriptionsArray;
    }


    /* Inner class that defines the table contents */
    public static class SubscriptionsEntry {

        public static final String SUBSCRIPTIONS_TABLE_NAME = BrandedSQLiteHelper.TABLE_SUBSCRIPTIONS;
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MAGAZINE_ID = "magazine_id";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_ANDROID_STORE_SKU = "android_store_sku";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_PAYMENT_PROVIDER = "payment_provider";
        public static final String COLUMN_PARENT_SKU_ID = "parent_sku_id";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_CREDITS_INCLUDED = "credits_included";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_REMOVE_FROM_SALE = "remove_from_sale";
        public static final String COLUMN_AUTO_RENEWABLE = "auto_renewable";


        public static final String CREATE_SUBSCRIPTIONS_TABLE = "CREATE TABLE "
                + SUBSCRIPTIONS_TABLE_NAME
                + "("
                + COLUMN_ID + " INTEGER,"
                + COLUMN_MAGAZINE_ID + " INTEGER,"
                + COLUMN_SYNOPSIS + " TEXT,"
                + COLUMN_ANDROID_STORE_SKU + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_PAYMENT_PROVIDER + " TEXT,"
                + COLUMN_PARENT_SKU_ID + " TEXT,"
                + COLUMN_THUMBNAIL_URL + " TEXT,"
                + COLUMN_CREDITS_INCLUDED + " INTEGER,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_REMOVE_FROM_SALE + " INTEGER,"
                + COLUMN_AUTO_RENEWABLE + " INTEGER"
                + ")"; ;

        public static final String DROP_SUBSCRIPTIONS_TABLE = "DROP TABLE IF EXISTS " + SUBSCRIPTIONS_TABLE_NAME;

    }




}
