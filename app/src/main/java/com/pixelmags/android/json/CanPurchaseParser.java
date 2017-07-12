package com.pixelmags.android.json;

/**
 * Created by Annie on 09/10/15.
 */
public class CanPurchaseParser extends JSONParser {

    public String mSuccess;
    

    public CanPurchaseParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            mSuccess = baseJSON.getString("success");

        }catch(Exception e){}


        return true;
    }

}
