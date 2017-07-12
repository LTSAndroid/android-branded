package com.pixelmags.android.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by austincoutinho on 07/10/15.
 *
 *  Used to instantiate the Application context and pass it everywhere (e.g. SQLiteOpenHelper) where app context is required
 *
 *  Use BaseApp.getContext(); anywhere in the app to access App context.
 *
 */
public class BaseApp extends Application {

    private static BaseApp instance;

    public static BaseApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
