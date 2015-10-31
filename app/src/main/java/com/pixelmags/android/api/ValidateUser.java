package com.pixelmags.android.api;

import com.pixelmags.android.comms.WebRequest;
import com.pixelmags.android.json.GetSubscriptionsParser;
import com.pixelmags.android.json.ValidateUserParser;
import com.pixelmags.android.storage.UserPrefs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Annie on 07/10/15.
 */
public class ValidateUser extends WebRequest
{
    private static final String API_NAME="validateUser";

    private String mEmail;
    private String mPassword;
    private ValidateUserParser validateParser;

    public ValidateUser(String email, String password){
        super(API_NAME);
        this.mEmail = email;
        this.mPassword = password;
    }

    public void init() {

        setApiNameValuePairs();
        doPostRequest();

        if(responseCode==200){

            validateParser = new ValidateUserParser(getAPIResultData());

            //System.out.println("Log in Data ::"+getAPIResultData());

            if(validateParser.initJSONParse()){

                if(validateParser.isSuccess()){
                    validateParser.parse();
                    saveValidatedUserDataToApp();
                } else{

                    UserPrefs.setUserLoggedIn(validateParser.isSuccess());

                    // Add error handling code here

                }

            }
        }

    }


    private void setApiNameValuePairs(){

        baseApiNameValuePairs = new ArrayList<NameValuePair>(6);
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_email_address", mEmail));
        baseApiNameValuePairs.add(new BasicNameValuePair("auth_password", mPassword));
        baseApiNameValuePairs.add(new BasicNameValuePair("device_id", baseDeviceId));
        baseApiNameValuePairs.add(new BasicNameValuePair("magazine_id", baseMagazineId));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_mode", baseApiMode));
        baseApiNameValuePairs.add(new BasicNameValuePair("api_version", baseApiVersion));

    }

    private void saveValidatedUserDataToApp(){

        if(validateParser.userDetails != null){

            UserPrefs.setUserEmail(validateParser.userDetails.email);
            UserPrefs.setUserPassword(mPassword); // ValidateUser Parser does not contain password
            UserPrefs.setUserFirstName(validateParser.userDetails.firstName);
            UserPrefs.setUserLastName(validateParser.userDetails.lastName);
            UserPrefs.setUserPixelmagsId(validateParser.userDetails.account_id);
            UserPrefs.setUserLoggedIn(validateParser.isSuccess());

        }else{
            UserPrefs.setUserLoggedIn(false);
        }
    }

    public boolean isSuccess() {

        if(validateParser != null){
            return validateParser.isSuccess();
        }

        return false;
    }

}

