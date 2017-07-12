package com.pixelmags.android.util;

import android.os.StrictMode;

/**
 * Created by austincoutinho on 23/10/15.
 */
public class PMStrictMode {


    public static void setStrictMode(boolean developerMode){

        if(developerMode){
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }


}
