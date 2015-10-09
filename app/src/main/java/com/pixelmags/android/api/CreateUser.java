package com.pixelmags.android.api;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.CreateUserParser;
import com.pixelmags.android.storage.UserPrefs;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by austincoutinho on 07/10/15.
 */

public class CreateUser extends WebRequest {

    private static final String API_NAME="CreateUser";

    private String mEmail;
    private String mPassword;
    private String mFirstName;
    private String mLastName;
    private String mDOB;
    CreateUserParser cParser;


    public CreateUser(){
        super(API_NAME);
    }

    public void init(String email, String password,String firstName , String lastName, String DOB) {

        mEmail = email;
        mPassword = password;
        mFirstName = firstName;
        mLastName = lastName;
        mDOB = DOB;

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){
            cParser = new CreateUserParser(getAPIResultData());
            if(cParser.initJSONParse()){

                if(cParser.isSuccess()){
                    cParser.parse();
                    saveCreateUserDataToApp();
                } else{

                    // Add error handling code here

                }

            }
        }

    }

    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(9);
        baseApiNameValuePairs.add(new BasicNameValuePair("email", mEmail));
        baseApiNameValuePairs.add(new BasicNameValuePair("password", mPassword));
        baseApiNameValuePairs.add(new BasicNameValuePair("date_of_birth", mDOB));
        baseApiNameValuePairs.add(new BasicNameValuePair("first_name", mFirstName));
        baseApiNameValuePairs.add(new BasicNameValuePair("last_name", mLastName));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", baseDeviceId));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", baseMagazineId));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode",baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }

    public void saveCreateUserDataToApp(){

        UserPrefs.setUserEmail(mEmail);
        UserPrefs.setUserPassword(mPassword);
        UserPrefs.setUserDob(mDOB);
        UserPrefs.setUserFirstName(mFirstName);
        UserPrefs.setUserLastName(mLastName);
        UserPrefs.setUserPixelmagsId(cParser.mUserID);

    }


}