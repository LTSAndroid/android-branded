package com.pixelmags.android.json;

import org.json.JSONObject;

/**
 * Created by austincoutinho on 08/10/15.
 */
public class JSONParser {

    public JSONObject baseJSON;
    private String JSONRawData;
    private boolean success;
    private int error_code;
    private String error_message;
    private String TAG = "JSONParser";


    JSONParser(){}

    public JSONParser(String Data){

        baseJSON = null;
        JSONRawData = Data;
        success = false;

    }

    public boolean initJSONParse(){


//        ErrorMessage.jsonTestResult = JSONRawData;

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
