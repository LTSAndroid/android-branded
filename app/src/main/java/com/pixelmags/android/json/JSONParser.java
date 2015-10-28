package com.pixelmags.android.json;

import org.json.JSONObject;

/**
 * Created by austincoutinho on 08/10/15.
 */
public class JSONParser {

    private String JSONRawData;
    public JSONObject baseJSON;
    private boolean success;
    private int error_code;
    private String error_message;


    JSONParser(){}

    public JSONParser(String Data){

        baseJSON = null;
        JSONRawData = Data;
        success = false;

    }

    public boolean initJSONParse(){

        try{

            baseJSON = new JSONObject(JSONRawData);
            success = baseJSON.getBoolean("success");
            error_code = baseJSON.getInt("error_code");
            error_message = baseJSON.getString("error_message");

            return true; // true if the JSON object gets sucesfully created and the basic response parameters are extracted.

        }catch(Exception e){}

        return false;
    }


    public boolean isSuccess(){
        return success;
    }

    public int getErrorCode(){
        return error_code;
    }

    public String getErrorMessage(){
        return error_message;
    }



}
