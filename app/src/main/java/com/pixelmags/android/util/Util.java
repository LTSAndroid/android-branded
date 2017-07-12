package com.pixelmags.android.util;

import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;

/**
 * Created by austincoutinho on 07/10/15.
 *
 * Any misc methods can go here.
 *
 */
public class Util {

public static void doPreLaunchSteps(){

    // to be extended as per launch flow
    UserPrefs.setDeviceId();

}

    public static String getLoginOrMyAccount(){

        if(UserPrefs.getUserLoggedIn()){
            return BaseApp.getContext().getString(R.string.menu_title_my_account);
        }

        return BaseApp.getContext().getString(R.string.menu_title_login);
    }


    public static void doAllLogoutSteps() {

        UserPrefs.setUserLoggedIn(false);
        UserPrefs.setUserPixelmagsId("");
        UserPrefs.setUserFirstName("");
        UserPrefs.setUserLastName("");
        UserPrefs.setUserPassword("");
        UserPrefs.setUserEmail("");
        UserPrefs.setUserPassword("");
        UserPrefs.setUserDob("");


    }



}
