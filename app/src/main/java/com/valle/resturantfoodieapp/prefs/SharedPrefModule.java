package com.valle.resturantfoodieapp.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefModule {

    private SharedPreferences prefs;

    private Context context;


    public SharedPrefModule(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUserLoginResponse(String response) {
        prefs.edit().putString("UserLoginResponse", response).commit();
    }


    public String getUserId() {
        return prefs.getString("userID", "");
    }

    public void setUserId(String userID) {
        prefs.edit().putString("userID", userID).commit();
    }

    public String getUserRememberData() {
        return prefs.getString("setUserRemember", "");
    }

    public void setUserPresence(String data) {
        prefs.edit().putString("UserPresence", data).commit();
    }

    public String getUserPresence() {
        return prefs.getString("UserPresence", "");
    }

    public void setUserRemember(String response) {
        prefs.edit().putString("setUserRemember", response).commit();
    }

    public void setAppNotificationPrefrence(String notificaton){
        prefs.edit().putString("notificaton", notificaton).commit();
    }
    public String getnotification() {
        return prefs.getString("notificaton", "");
    }

    public String getUserLoginResponseData() {
        return prefs.getString("UserLoginResponse", "");
    }

}