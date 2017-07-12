package com.pixelmags.android.json;

/**
 * Created by bujji on 09/10/15.
 */
public class RequestDeregisterParser extends JSONParser {

    public String mSuccess;

    public RequestDeregisterParser(String Data){
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
