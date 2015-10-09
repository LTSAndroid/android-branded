package com.pixelmags.android.json;

/**
 * Created by Annie on 09/10/15.
 */
public class ForgotPasswordParser extends JSONParser {

    public String msuccess;


    public ForgotPasswordParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            msuccess = baseJSON.getString("success");

        }catch(Exception e){}


        return true;
    }

}

