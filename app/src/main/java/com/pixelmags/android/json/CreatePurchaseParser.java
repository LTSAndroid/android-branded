package com.pixelmags.android.json;

/**
 * Created by Annie on 09/10/15.
 */
public class CreatePurchaseParser extends JSONParser {

    public String mDocumentKey;


    public CreatePurchaseParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            mDocumentKey = baseJSON.getString("public_key");

        }catch(Exception e){}


        return true;
    }

}
