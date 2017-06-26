package com.pixelmags.android.api;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.GetSubscriptionsParser;
import com.pixelmags.android.storage.SubscriptionsDataSet;
import com.pixelmags.android.util.BaseApp;

import okhttp3.FormBody;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

/**
 * Created by austincoutinho on 07/10/15.
 */

public class GetSubscriptions extends WebRequest {

    private static final String API_NAME="getSubscriptions";

    GetSubscriptionsParser subsParser;
    private String TAG = "GetSubscription";


    public GetSubscriptions(){
        super(API_NAME);
    }

    public void init() {

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            subsParser = new GetSubscriptionsParser(getAPIResultData());
            if(subsParser.initJSONParse()){

                if(subsParser.isSuccess()){
                    subsParser.parse();
                    saveSubscriptionsDataToApp();
                } else{

                    // Add error handling code here

                }

            }
        }

    }

    private void setApiNameValuePairs(){

        requestBody = new FormBody.Builder()
                .add("magazine_id", baseMagazineId)
                .add("app_bundle_id", Config.Bundle_ID)
                .add("api_mode", Config.api_mode)
                .add("api_version", Config.api_version)

                .build();

//        baseApiNameValuePairs = new ArrayList<NameValuePair>(4);
//        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", baseMagazineId));
//        baseApiNameValuePairs.add(new BasicNameValuePair("app_bundle_id", baseAppBundleId));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode",baseApiMode));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }

    public void saveSubscriptionsDataToApp(){

        // printing the values of the Subscriptions objects
      /*  for(int i=0; i<subsParser.subscriptionsList.size();i++){
            Subscription sub = subsParser.subscriptionsList.get(i);

            System.out.println("SUBSCRIPTION Synopsis === " + sub.synopsis);
            System.out.println("SUBSCRIPTION Price === " + sub.price);
            System.out.println("SUBSCRIPTION Description === " + sub.description);

        }
        */

        // Save the Subscription Objects into the SQlite DB
        SubscriptionsDataSet mDbHelper = new SubscriptionsDataSet(BaseApp.getContext());
        mDbHelper.insert_all_subscriptions(mDbHelper.getWritableDatabase(), subsParser.subscriptionsList);
        mDbHelper.close();

        //Enable these only for testing purpose. Else not required

//        SubscriptionsDataSet mDbReader = new SubscriptionsDataSet(BaseApp.getContext());
//        ArrayList<Subscription> mySubsArray = mDbReader.getAllSubscriptions(mDbReader.getReadableDatabase());
//        mDbReader.close();
//
//        for(int i=0; i< mySubsArray.size();i++) {
//            Subscription sub = mySubsArray.get(i);
//            Log.d(TAG,"Complete Subscription is : "+sub.payment_provider);
//            Log.d(TAG,"Subscription Description is ::" + sub.description);
//            Log.d(TAG,"Subscription price is "+sub.price);
//        }
    }


}
