package com.pixelmags.android.util;

import android.content.Context;
import android.telephony.TelephonyManager;

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



}
