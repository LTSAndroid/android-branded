package com.pixelmags.android.api;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.datamodels.MySubscription;
import com.pixelmags.android.datamodels.Subscription;
import com.pixelmags.android.json.GetMySubscriptionsParser;
import com.pixelmags.android.json.GetSubscriptionsParser;
import com.pixelmags.android.storage.SubscriptionsDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.BaseApp;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 30/10/15.
 */
public class GetMySubscriptions extends WebRequest {

    private static final String API_NAME="getMySubscriptions";

    public GetMySubscriptionsParser mySubsParser;


    public GetMySubscriptions(){
        super(API_NAME);
    }

    public void init() {

        setApiNameValuePairs();
        doPostRequest();

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

        baseApiNameValuePairs = new ArrayList<NameValuePair>(6);
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", UserPrefs.getUserEmail()));
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", UserPrefs.getUserPassword()));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", UserPrefs.getDeviceID()));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", Config.Magazine_Number));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", Config.Bundle_ID));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", Config.api_mode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));

    }

    public void saveMySubscriptionsDataToApp(){


        for(int i=0; i< mySubsParser.mySubscriptionsList.size();i++) {

            MySubscription sub = mySubsParser.mySubscriptionsList.get(i);

            System.out.println(" MySubscription CREDITS ::" + sub.creditsAvailable);
        }


        // Save the MySubscription Objects into the SQlite DB


        /*
        SubscriptionsDataSet mDbHelper = new SubscriptionsDataSet(BaseApp.getContext());
        mDbHelper.insert_all_subscriptions(mDbHelper.getWritableDatabase(), subsParser.subscriptionsList);
        mDbHelper.close();
        */
    }


}

