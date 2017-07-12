package com.pixelmags.android.json;

import android.util.Log;

/**
 * Created by Annie on 09/10/15.
 */
public class CreatePurchaseParser extends JSONParser {

    public String mDocumentKey;
    public String transaction_Id;
    private String TAG = "CreatePurchaseParser";


    public CreatePurchaseParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            Log.d(TAG,"JSON Object received after issue purchase is : "+baseJSON);

            mDocumentKey = baseJSON.getString("public_key");
            if(!baseJSON.isNull("transaction_id")){
                transaction_Id = baseJSON.getString("transaction_id");
            }

        }catch(Exception e){}


        return true;
    }

}
