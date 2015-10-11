package com.pixelmags.android.datamodels;

/**
 * Created by Annie on 11/10/15.
 */
public class User
{

    /**
     * One of the main reason for Uer Model is to enable the app to go through auto login procress. i.e: once user logged In
     * we never show login screen untill he log out
     */
    public int id;

    public String email;

    public String password;

    public String firstName;

    public String lastName;

    public String deviceId;

}
