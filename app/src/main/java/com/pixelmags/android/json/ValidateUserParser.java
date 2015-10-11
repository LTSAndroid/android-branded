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

    public String mSuccess;
    public String muserID;
    public ArrayList<User> userList;

    public ValidateUserParser(String Data){
        super(Data);
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            mSuccess = baseJSON.getString("success");
            muserID = baseJSON.getString("user_id");


                User user = new User();

            user.id = baseJSON.getInt("user_id");
            user.firstName = baseJSON.getString("first_name");
            user.lastName = baseJSON.getString("last_name");
            user.email = baseJSON.getString("email");
            user.deviceId = baseJSON.getString("device_id");



        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

}
