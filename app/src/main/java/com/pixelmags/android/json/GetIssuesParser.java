package com.pixelmags.android.json;


import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.Subscription;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetIssuesParser extends JSONParser {

    public Object mData;
    public ArrayList<Magazine> allIssuesList;



    public GetIssuesParser(String Data){
        super(Data);
        allIssuesList = new ArrayList<Magazine>();
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            JSONArray arrayData = baseJSON.getJSONArray("data");
            for(int i=0;i<arrayData.length();i++)
            {
                Magazine magazine = new Magazine();
                JSONObject unit = arrayData.getJSONObject(i);

                magazine.id = unit.getInt("ID");
                magazine.magazineId = unit.getInt("ID");
                magazine.synopsis = unit.getString("synopsis");
                magazine.type = unit.getString("type");
                magazine.magazine_title = unit.getString("title");
                magazine.mediaFormat = unit.getString("media_format");
                magazine.manifest = unit.getString("manifest");
                // magazine.lastModified = unit.getString("lastModified"); // how to get date?
                magazine.android_issue_sku = unit.getString("iTunesStoreSKU");
                magazine.price = unit.getDouble("price");
                magazine.thumbnailURL = unit.getString("thumbnailURL");
                magazine.ageRestriction = unit.getString("ageRestriction");
                magazine.removeFromSale = unit.getBoolean("remove_from_sale");
                magazine.isPublished = unit.getBoolean("isPublished");
                magazine.exclude_from_subscription = unit.getString("exclude_from_subscription");

                allIssuesList.add(magazine);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

}
