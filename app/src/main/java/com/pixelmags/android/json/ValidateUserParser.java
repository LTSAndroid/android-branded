package com.pixelmags.android.json;

import com.pixelmags.android.datamodels.PreviewImage;
import com.pixelmags.android.datamodels.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class ValidateUserParser extends JSONParser {

    public String loginSuccess;
    public User userDetails;

    public ValidateUserParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            userDetails = new User();
            userDetails.account_id = baseJSON.getString("user_id");
            userDetails.firstName = baseJSON.getString("first_name");
            userDetails.lastName = baseJSON.getString("last_name");
            userDetails.email = baseJSON.getString("email");
            userDetails.deviceId = baseJSON.getString("device_id");

        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

}
