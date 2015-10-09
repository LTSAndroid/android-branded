package com.pixelmags.android.json;

/**
 * Created by Annie on 09/10/15.
 */
public class FindUserByDeviceIDParser extends JSONParser {

    public String mUserID;
    public String memail;
    public String mFirstName;


    public FindUserByDeviceIDParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            mUserID = baseJSON.getString("mUserID");
            memail = baseJSON.getString("email");
            mFirstName = baseJSON.getString("first_name");

        }catch(Exception e){}


        return true;
    }

}

