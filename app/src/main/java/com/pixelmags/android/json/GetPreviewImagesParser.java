package com.pixelmags.android.json;

/**
 * Created by Annie on 09/10/15.
 */
public class GetPreviewImagesParser extends JSONParser {

    public Object mData;


    public GetPreviewImagesParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            mData = baseJSON.getJSONArray("data");

        }catch(Exception e){}


        return true;
    }

}


