package com.pixelmags.android.json;


import android.os.Bundle;

import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.Subscription;
import com.pixelmags.android.pixelmagsapp.LaunchActivity;
import com.pixelmags.android.pixelmagsapp.MainActivity;
import com.pixelmags.android.util.IabHelper;
import com.pixelmags.android.util.IabResult;
import com.pixelmags.android.util.Inventory;
import com.pixelmags.android.util.Purchase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetIssuesParser extends JSONParser {

    public Object mData;
    public ArrayList<Magazine> allIssuesList;

    public ArrayList<String> skuList;

    public GetIssuesParser(String Data){
        super(Data);
        allIssuesList = new ArrayList<Magazine>();
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            skuList = new ArrayList<String>();

            JSONArray arrayData = baseJSON.getJSONArray("data");
            for(int i=0;i<arrayData.length();i++)
            {
                Magazine magazine = new Magazine();
                JSONObject unit = arrayData.getJSONObject(i);

                magazine.id = unit.getInt("ID");
            //    magazine.magazineId = unit.getInt("ID"); // Is this different from ID field ??
                magazine.synopsis = unit.getString("synopsis");
                magazine.type = unit.getString("type");
                magazine.title = unit.getString("title");
                magazine.mediaFormat = unit.getString("media_format");
                magazine.manifest = unit.getString("manifest");
                // magazine.lastModified = unit.getString("lastModified"); // how to get date?
                magazine.android_store_sku = unit.getString("iTunesStoreSKU");
                magazine.price = unit.getDouble("price");
                magazine.thumbnailURL = unit.getString("thumbnailURL");
                magazine.ageRestriction = unit.getString("ageRestriction");
                magazine.removeFromSale = unit.getBoolean("remove_from_sale");
                magazine.isPublished = unit.getBoolean("isPublished");
                magazine.exclude_from_subscription = unit.getString("exclude_from_subscription");

                allIssuesList.add(magazine);
                skuList.add(magazine.android_store_sku);
            }
// IAB is fully set up. Now, let's get an inventory of stuff we own.
            //LaunchActivity.mHelper.queryInventoryAsync(true,skuList,iabInventoryListener());


         /*   int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList
                        = skuDetails.getStringArrayList("DETAILS_LIST");

                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    String sku = object.getString("productId");
                    String price = object.getString("price");
                    *//*if (sku.equals("premiumUpgrade")) mPremiumUpgradePrice = price;
                    else if (sku.equals("gas")) mGasPrice = price;*//*
                }
            }*/

        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }
/**
 * Listener that's called when we finish querying the items and subscriptions we own
 *//*
    private IabHelper.QueryInventoryFinishedListener iabInventoryListener() {
        return new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            // Have we been disposed of in the meantime? If so, quit.
                if (LaunchActivity.mHelper == null) {
                    return;
                }
            // Something went wrong
                if (!result.isSuccess()) {
                    return;
                }
               // Purchase purchasePro = inventory.getPurchase(G.SKU_PRO); // Where G.SKU_PRO is your product ID (eg. permanent.ad_removal)
            }
        };
    }*/
}
