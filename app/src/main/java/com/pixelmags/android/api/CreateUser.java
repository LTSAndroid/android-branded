package com.pixelmags.android.api;

import android.util.Log;

import com.pixelmags.android.comms.ErrorMessage;
import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.CreateUserParser;
import com.pixelmags.android.storage.UserPrefs;

import okhttp3.FormBody;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

/**
 * Created by austincoutinho on 07/10/15.
 */

public class CreateUser extends WebRequest {

    private static final String API_NAME="CreateUser";
    CreateUserParser cParser;
    private String mEmail;
    private String mPassword;
    private String mFirstName;
    private String mLastName;
    private String mDOB;
    private String TAG = "CreateUser";


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
                    Log.d(TAG,"Get API Error Message "+cParser.getErrorMessage());
                    ErrorMessage.hasError = true;
                    ErrorMessage.errorCode = cParser.getErrorCode();
                    ErrorMessage.errorMessage = cParser.getErrorMessage();
                }

            }
        }else{
            cParser = new CreateUserParser(getAPIResultData());
            if(cParser.initJSONParse()){

                    // Add error handling code here
                Log.d(TAG,"Get API Error Message "+cParser.getErrorMessage());
                ErrorMessage.hasError = true;
                ErrorMessage.errorCode = cParser.getErrorCode();
                ErrorMessage.errorMessage = cParser.getErrorMessage();

            }
        }


    }

    private void setApiNameValuePairs(){

        requestBody = new FormBody.Builder()
                .add("email", mEmail)
                .add("password", mPassword)
                .add("date_of_birth", mDOB)
                .add("first_name", mFirstName)
                .add("last_name", mLastName)
                .add("device_id", baseDeviceId)
                .add("magazine_id", baseMagazineId)
                .add("api_mode", baseApiMode)
                .add("api_version", baseApiVersion)
                .build();


//        baseApiNameValuePairs = new ArrayList<NameValuePair>(9);
//        baseApiNameValuePairs.add(new BasicNameValuePair("email", mEmail));
//        baseApiNameValuePairs.add(new BasicNameValuePair("password", mPassword));
//        baseApiNameValuePairs.add(new BasicNameValuePair("date_of_birth", mDOB));
//        baseApiNameValuePairs.add(new BasicNameValuePair("first_name", mFirstName));
//        baseApiNameValuePairs.add(new BasicNameValuePair("last_name", mLastName));
//        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", baseDeviceId));
//        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", baseMagazineId));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode",baseApiMode));
//        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

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
