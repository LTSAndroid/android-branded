package com.pixelmags.android.util;

/**
 * Created by austincoutinho on 27/10/15.
 */
public class AccountUtil {

    public static boolean isEmailValid(String email) {

        if(email.contains(".") && email.contains("@")){
            return  true;
        }

        return false;
    }

    public static boolean isPasswordValid(String password) {

        return (password.length() > 5) ? true : false;
    }

}
