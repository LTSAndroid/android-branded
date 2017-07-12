package com.pixelmags.android.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pixelmags.android.datamodels.MySubscription;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 01/11/15.
 */
public class MySubscriptionsDataSet extends BrandedSQLiteHelper {


    public MySubscriptionsDataSet(Context context) {
        super(context);
    }

    public void createTableMySubscriptions(SQLiteDatabase db){
        db.execSQL(MySubscriptionsEntry.CREATE_MY_SUBSCRIPTIONS_TABLE);
    }

    public void dropMySubscriptionsTable(SQLiteDatabase db){
        db.execSQL(MySubscriptionsEntry.DROP_MY_SUBSCRIPTIONS_TABLE);
    }

    public void insert_my_subscriptions(SQLiteDatabase db, ArrayList<MySubscription> mySubscriptionsArray){

        // Do batch inserts using Transcations. This is to vastly increase the speed of DB writes

        try{
            // Start the transaction
            db.beginTransaction();


            // clear out any previous values by rebuilding table
            dropMySubscriptionsTable(db);
            createTableMySubscriptions(db);



            for(int i=0; i< mySubscriptionsArray.size();i++){

                MySubscription sub = mySubscriptionsArray.get(i);

                ContentValues insertValues = new ContentValues();
                insertValues.put(MySubscriptionsEntry.COLUMN_MAGAZINE_ID, sub.magazineID);
                insertValues.put(MySubscriptionsEntry.COLUMN_CREDITS_AVAILABLE, sub.creditsAvailable);
                insertValues.put(MySubscriptionsEntry.COLUMN_PURCHASE_DATE, sub.purchaseDate);
                insertValues.put(MySubscriptionsEntry.COLUMN_EXPIRES_DATE, sub.expiresDate);
                insertValues.put(MySubscriptionsEntry.COLUMN_SUBSCRIPTION_PRODUCT_ID, sub.subscriptionProductId);

                db.insert(MySubscriptionsEntry.MY_SUBSCRIPTIONS_TABLE_NAME, null, insertValues);

            }

            //End and close the transaction
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

    }


    public ArrayList<MySubscription> getMySubscriptions(SQLiteDatabase db){

        ArrayList<MySubscription> mySubscriptionsArray = null;

        try{

            // Define a projection i.e specify columns to retrieve
            String[] projection = {
                    MySubscriptionsEntry.COLUMN_MAGAZINE_ID,
                    MySubscriptionsEntry.COLUMN_CREDITS_AVAILABLE,
                    MySubscriptionsEntry.COLUMN_PURCHASE_DATE,
                    MySubscriptionsEntry.COLUMN_EXPIRES_DATE,
                    MySubscriptionsEntry.COLUMN_SUBSCRIPTION_PRODUCT_ID
            };

            // Specify the sort order
            String sortOrder = MySubscriptionsEntry.COLUMN_PURCHASE_DATE + " DESC";

            Cursor queryCursor = db.query(
                    MySubscriptionsEntry.MY_SUBSCRIPTIONS_TABLE_NAME,    // The table to query
                    projection,                                     // The columns to return
                    null,                                           // The columns for the WHERE clause
                    null,                                           // The values for the WHERE clause
                    null,
                    null,
                    sortOrder                                       // The sort order
            );

            if(queryCursor != null ){

                mySubscriptionsArray = new ArrayList<MySubscription>();

                //queryCursor.getCount();
                while (queryCursor.moveToNext()) {
                    // Extract data.
                    MySubscription sub = new MySubscription();

                    sub.magazineID = queryCursor.getString(queryCursor.getColumnIndex(MySubscriptionsEntry.COLUMN_MAGAZINE_ID));
                    sub.creditsAvailable = queryCursor.getInt(queryCursor.getColumnIndex(MySubscriptionsEntry.COLUMN_CREDITS_AVAILABLE));
                    sub.purchaseDate = queryCursor.getString(queryCursor.getColumnIndex(MySubscriptionsEntry.COLUMN_PURCHASE_DATE));
                    sub.expiresDate = queryCursor.getString(queryCursor.getColumnIndex(MySubscriptionsEntry.COLUMN_EXPIRES_DATE));
                    sub.subscriptionProductId = queryCursor.getString(queryCursor.getColumnIndex(MySubscriptionsEntry.COLUMN_SUBSCRIPTION_PRODUCT_ID));

                    mySubscriptionsArray.add(sub);

                }

                queryCursor.close();
            }


        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return mySubscriptionsArray;
    }




    /* Inner class that defines the table contents */
    public static class MySubscriptionsEntry {

        public static final String MY_SUBSCRIPTIONS_TABLE_NAME = BrandedSQLiteHelper.TABLE_MY_SUBSCRIPTIONS;
        public static final String COLUMN_MAGAZINE_ID = "magazine_id";
        public static final String COLUMN_CREDITS_AVAILABLE = "credits_available";
        public static final String COLUMN_PURCHASE_DATE = "purchase_date";
        public static final String COLUMN_EXPIRES_DATE = "expires_date";
        public static final String COLUMN_SUBSCRIPTION_PRODUCT_ID = "subscription_product_id";

        public static final String CREATE_MY_SUBSCRIPTIONS_TABLE = "CREATE TABLE "
                + MY_SUBSCRIPTIONS_TABLE_NAME
                + "("
                + COLUMN_MAGAZINE_ID + " TEXT,"
                + COLUMN_CREDITS_AVAILABLE + " INTEGER,"
                + COLUMN_PURCHASE_DATE + " TEXT,"
                + COLUMN_EXPIRES_DATE + " TEXT,"
                + COLUMN_SUBSCRIPTION_PRODUCT_ID + " TEXT"
                + ")"; ;

        public static final String DROP_MY_SUBSCRIPTIONS_TABLE = "DROP TABLE IF EXISTS " + MY_SUBSCRIPTIONS_TABLE_NAME;

    }

}
