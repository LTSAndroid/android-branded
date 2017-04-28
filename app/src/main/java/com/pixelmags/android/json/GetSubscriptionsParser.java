package com.pixelmags.android.json;

import com.pixelmags.android.datamodels.Subscription;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by austincoutinho on 08/10/15.
 *
 */

public class GetSubscriptionsParser extends JSONParser {

    public ArrayList<Subscription> subscriptionsList;
    String mUserID;




    public GetSubscriptionsParser(String Data){

        super(Data);
        subscriptionsList = new ArrayList<Subscription>();
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

                JSONArray arrayData = baseJSON.getJSONArray("data");
                for(int i=0;i<arrayData.length();i++)
                {
                    Subscription sub = new Subscription();
                    JSONObject unit = arrayData.getJSONObject(i);

/*
*     {"id":51,"magazine_id":"2","synopsis":"Save with a subscription","itunes_store_sku":"ultimamedia_interior_motives.12",
    "price":19.99,"payment_provider":"itunes","parent_sku_id":null,"thumbnail_url":"http:\/\/cdn.pixel-mags.com\/prod\/ultimamedia\/
    interior_motives\/thumbnails\/51.png","credits_included":4,"description":"1 Year","remove_from_sale":true,"auto_renewable":false}

*
**/
                    sub.id = unit.getInt("id");
                    sub.magazine_id = unit.getInt("magazine_id");
                    sub.synopsis = unit.getString("synopsis");
                    sub.android_store_sku = unit.getString("itunes_store_sku");   // NEEDS TO BE CHANGED
                    sub.price = String.valueOf(unit.getDouble("price"));
                    sub.payment_provider = unit.getString("payment_provider");
                    sub.parent_sku_id = unit.getString("parent_sku_id");
                    sub.thumbnail_url = unit.getString("thumbnail_url");
                    sub.credits_included = unit.getInt("credits_included");
                    sub.description = unit.getString("description");
                    sub.remove_from_sale = unit.getBoolean("remove_from_sale");
                    sub.auto_renewable = unit.getBoolean("auto_renewable");

                    subscriptionsList.add(sub);
                }

        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

}
