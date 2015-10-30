package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.datamodels.Subscription;
import com.pixelmags.android.json.GetMySubscriptionsParser;
import com.pixelmags.android.json.GetSubscriptionsParser;
import com.pixelmags.android.storage.SubscriptionsDataSet;
import com.pixelmags.android.util.BaseApp;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 30/10/15.
 */
public class GetMySubscriptions extends WebRequest {

    private static final String API_NAME="getMySubscriptions";

    GetMySubscriptionsParser mySubsParser;


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

        baseApiNameValuePairs = new ArrayList<NameValuePair>(4);
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", baseMagazineId));
        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", baseAppBundleId));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode",baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }

    public void saveMySubscriptionsDataToApp(){

        // Save the Subscription Objects into the SQlite DB
        /*
        SubscriptionsDataSet mDbHelper = new SubscriptionsDataSet(BaseApp.getContext());
        mDbHelper.insert_all_subscriptions(mDbHelper.getWritableDatabase(), subsParser.subscriptionsList);
        mDbHelper.close();
        */
    }


}

