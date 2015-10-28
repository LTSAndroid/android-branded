package com.pixelmags.android.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.pixelmags.android.util.BaseApp;

/**
 * Created by austincoutinho on 07/10/15.
 *
 * Store user prefs
 *
 */
public class UserPrefs {

    private static final String PREFS_NAME = "USER_DATA_PREFERENCES";

    private static final String USER_EMAIL = "user_email";
    private static final String USER_PASSWORD = "user_password";
    private static final String USER_DOB = "user_dob"; // date of birth
    private static final String USER_FIRST_NAME = "user_first_name";
    private static final String USER_LAST_NAME = "user_last_name";
    private static final String USER_PIXELMAGS_ID = "user_pixelmags_id";

    private static final String DEVICE_ID = "device_id";

    private static final String USER_LOGIN_STATUS = "user_login_status";


    private static void setStringPref(String PrefID, String value){

        SharedPreferences settings = BaseApp.getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PrefID, value);
        editor.commit();

    }

    private static void setBooleanPref(String PrefID, boolean value){

        SharedPreferences settings = BaseApp.getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PrefID, value);
        editor.commit();

    }

    private static void setIntPref(String PrefID, int value){

        SharedPreferences settings = BaseApp.getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PrefID, value);
        editor.commit();

    }

    private static String getStringPrefs(String PrefID, String defaultValue){

        SharedPreferences settings = BaseApp.getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        return settings.getString(PrefID, defaultValue);

    }


    // Note : if the preference values are set before u call them, for boolean, a true is returned,
    // so make sure that when app launches values are set before access
    private static boolean getBooleanPrefs(String PrefID, boolean defaultValue){

        SharedPreferences settings = BaseApp.getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        return settings.getBoolean(PrefID, defaultValue);

    }


    private static int getIntPrefs(String PrefID, int defaultValue){

        SharedPreferences settings = BaseApp.getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        return settings.getInt(PrefID, defaultValue);

    }


    // User Email
    public static void setUserEmail(String email){
        setStringPref(USER_EMAIL, email);
    }

    public static String getUserEmail(){
        return getStringPrefs(USER_EMAIL, "");
    }

    // User Password
    public static void setUserPassword(String password){
        setStringPref(USER_PASSWORD, password);
    }

    public static String getUserPassword(){
        return getStringPrefs(USER_PASSWORD,"");
    }


    // User Date of Birth
    public static void setUserDob(String dob){
        setStringPref(USER_DOB, dob);
    }

    public static String getUserDob(){
        return getStringPrefs(USER_DOB,"");
    }

    // User First Name
    public static void setUserFirstName(String firstName){
        setStringPref(USER_FIRST_NAME, firstName);
    }

    public static String getUserFirstName(){
        return getStringPrefs(USER_FIRST_NAME,"");
    }

    // User Last Name
    public static void setUserLastName(String lastName){
        setStringPref(USER_LAST_NAME, lastName);
    }

    public static String getUserLastName(){
        return getStringPrefs(USER_LAST_NAME,"");
    }


    // User Last Name
    public static void setUserPixelmagsId(String userPixelmagsId){
        setStringPref(USER_PIXELMAGS_ID, userPixelmagsId);
    }

    public static String getUserPixelmagsId(){
        return getStringPrefs(USER_PIXELMAGS_ID,"");
    }


    // Device ID

    public static void setDeviceId() {

        String deviceId = null;
        final TelephonyManager tm = (TelephonyManager) BaseApp.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm.getDeviceId();

        setStringPref(DEVICE_ID, deviceId);

    }

    public static String getDeviceID() {

        return getStringPrefs(DEVICE_ID, "UnknownDeviceId");

    }

    // User Log in status

    public static void setUserLoggedIn(boolean loggedInStatus) {

        setBooleanPref(USER_LOGIN_STATUS, loggedInStatus);

    }

    public static boolean getUserLoggedIn() {

        return getBooleanPrefs(USER_LOGIN_STATUS, false);

    }


}
