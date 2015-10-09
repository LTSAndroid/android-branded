package com.pixelmags.android.json;

/**
 * Created by Annie on 09/10/15.
 */
public class ValidateUserParser extends JSONParser {

    public String mSuccess;
    public String muserID;


    public ValidateUserParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            mSuccess = baseJSON.getString("success");
            muserID = baseJSON.getString("user_id");

        }catch(Exception e){}


        return true;
    }

}
