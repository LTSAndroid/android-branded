package com.pixelmags.android.json;

/**
 * Created by Annie on 09/10/15.
 */
public class FindMemberByEmailParser extends JSONParser {

    public String mUserID;


    public FindMemberByEmailParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            mUserID = baseJSON.getString("mUserID");

        }catch(Exception e){}


        return true;
    }

}
