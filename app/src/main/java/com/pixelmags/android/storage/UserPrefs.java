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

    public static final String PREFS_NAME = "USER_DATA_PREFERENCES";

    public static final String DEVICE_ID = "device_id";


    public static void storeDeviceID() {

        String deviceId = null;

        final TelephonyManager tm = (TelephonyManager) BaseApp.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm.getDeviceId();


        System.out.println("DEVICE ID === "+deviceId);

        SharedPreferences settings = BaseApp.getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(DEVICE_ID, deviceId);
        editor.commit();


        System.out.println("DEVICE ID STORED === "+getDeviceID());

    }

    public static String getDeviceID() {

        SharedPreferences settings = BaseApp.getContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        String deviceid = settings.getString(DEVICE_ID,"UnknownDeviceId");

        return deviceid;
    }



}
