package com.pixelmags.android.json;


import android.util.Log;

import com.pixelmags.android.datamodels.Magazine;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Annie on 09/10/15.
 */
public class GetIssuesParser extends JSONParser {

    public Object mData;
    public ArrayList<Magazine> allIssuesList;
    private String TAG = "GetIssuesParser";



    public GetIssuesParser(String Data){
        super(Data);
        allIssuesList = new ArrayList<Magazine>();
    }

    public boolean parse()
    {

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{



            JSONArray arrayData = baseJSON.getJSONArray("data");
            for(int i=0;i< arrayData.length();i++)
            {
                Magazine magazine = new Magazine();
                JSONObject unit = arrayData.getJSONObject(i);

                magazine.id = unit.getInt("ID");

                Log.d(TAG,"Magazine Id is : "+magazine.id);

            //    magazine.magazineId = unit.getInt("ID"); // Is this different from ID field ??
                magazine.synopsis = unit.getString("synopsis");
                magazine.type = unit.getString("type");
                magazine.title = unit.getString("title");
                Log.d(TAG,"Issue title is : "+magazine.title);
                magazine.mediaFormat = unit.getString("media_format");
                magazine.manifest = unit.getString("manifest");
                magazine.issueDate =  unit.getString("issueDate");
                Log.d(TAG,"Issue date parsed is : "+magazine.issueDate);
                // magazine.lastModified = unit.getString("lastModified"); // how to get date?
                magazine.android_store_sku = unit.getString("iTunesStoreSKU");
                magazine.price = unit.getString("price");
                magazine.thumbnailURL = unit.getString("thumbnailURL");
                magazine.ageRestriction = unit.getString("ageRestriction");
                magazine.removeFromSale = unit.getBoolean("remove_from_sale");
                magazine.isPublished = unit.getBoolean("isPublished");
                magazine.exclude_from_subscription = unit.getString("exclude_from_subscription");
                magazine.paymentProvider = unit.getString("paymentProvider");
                Log.d(TAG,"Payment Provider is : " +magazine.paymentProvider);

                allIssuesList.add(magazine);

            }
// IAB is fully set up. Now, let's get an inventory of stuff we own.
        //    LaunchActivity.mHelper.queryInventoryAsync(true,skuList,iabInventoryListener());

            Collections.sort(allIssuesList, new Comparator<Magazine>(){
                public int compare(Magazine magazine1, Magazine magazine2) {
                    // ## Ascending order
                    return magazine1.issueDate.compareToIgnoreCase(magazine2.issueDate); // To compare string values
                    // return Integer.valueOf(emp1.getId()).compareTo(emp2.getId()); // To compare integer values

                    // ## Descending order
                    // return emp2.getFirstName().compareToIgnoreCase(emp1.getFirstName()); // To compare string values
                    // return Integer.valueOf(emp2.getId()).compareTo(emp1.getId()); // To compare integer values
                }
            });

            for(int i=0; i<allIssuesList.size(); i++){
                Log.d(TAG,"Array List after sorting  is : "+ allIssuesList.get(i).issueDate);
            }



        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }


}
