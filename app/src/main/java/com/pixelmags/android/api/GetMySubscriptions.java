package com.pixelmags.android.api;

import android.util.Log;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.GetMySubscriptionsParser;
import com.pixelmags.android.storage.MySubscriptionsDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.BaseApp;

import okhttp3.FormBody;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

/**
 * Created by austincoutinho on 30/10/15.
 */
public class GetMySubscriptions extends WebRequest {

    private static final String API_NAME="getMySubscriptions";

    public GetMySubscriptionsParser mySubsParser;
    private String TAG = "GetMySubscription";


    public GetMySubscriptions(){
        super(API_NAME);
    }

    public void init() {

        setApiNameValuePairs();
        doPostRequest();

        Log.d(TAG,"Response Code for subscription is : "+responseCode);

        if(responseCode==200){
            mySubsParser = new GetMySubscriptionsParser(getAPIResultData());
            if(mySubsParser.initJSONParse()){

                if(mySubsParser.isSuccess()){
                    mySubsParser.parse();
                    saveMySubscriptionsDataToApp();
                } else{

                    // Add error handling code here

                }

            }
        }

    }

    private void setApiNameValuePairs(){

        requestBody = new FormBody.Builder()
                .add("auth_email_address", UserPrefs.getUserEmail())
                .add("auth_password", UserPrefs.getUserPassword())
                .add("device_id", UserPrefs.getDeviceID())
                .add("magazine_id", Config.Magazine_Number)
                .add("app_bundle_id", Config.Bundle_ID)
                .add("api_mode", Config.api_mode)
                .add("api_version", Config.api_version)

                .build();

//        baseApiNameValuePairs = new ArrayList<NameValuePair>(6);
//        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", UserPrefs.getUserEmail()));
//        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", UserPrefs.getUserPassword()));
//        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", UserPrefs.getDeviceID()));
//        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", Config.Magazine_Number));
//        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", Config.Bundle_ID));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));

    }

    public void saveMySubscriptionsDataToApp(){

        MySubscriptionsDataSet mDbHelper = new MySubscriptionsDataSet(BaseApp.getContext());
        mDbHelper.insert_my_subscriptions(mDbHelper.getWritableDatabase(), mySubsParser.mySubscriptionsList);
        mDbHelper.close();


        //Enable these only for testing purpose. Else not required

//        MySubscriptionsDataSet mDbReader = new MySubscriptionsDataSet(BaseApp.getContext());
//        ArrayList<MySubscription> mySubsArray = mDbReader.getMySubscriptions(mDbReader.getReadableDatabase());
//        mDbReader.close();
//
//        Log.d(TAG,"My Subscription Array size is : "+mySubsArray.size());
//
//        for(int i=0; i< mySubsArray.size();i++) {
//            MySubscription sub = mySubsArray.get(i);
//            Log.d(TAG,"MySubscription CREDITS : "+sub.creditsAvailable);
//            Log.d(TAG,"MySybscription price is "+sub.expiresDate);
//        }
        // Save the MySubscription Objects into the SQlite DB

    }


}

