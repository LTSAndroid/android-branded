package com.pixelmags.android.json;

/**
 * Created by austincoutinho on 08/10/15.
 */
public class CreateUserParser extends JSONParser {

    public String mUserID;

//{"success":true,"user_id":121613753,"error_code":-1,
// "error_message":"No error","execution_time":0.166,"api_version":2,"api_time":"2015-10-08 00:37:04"}



    public CreateUserParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            mUserID = baseJSON.getString("user_id");

        }catch(Exception e){
            e.printStackTrace();
        }


        return true;
    }

}
