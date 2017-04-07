package com.iems5722.group1.pharos.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.iems5722.group1.pharos.Constants;

/**
 * Created by Sora on 7/4/17.
 */

public class Util {
    private static String username = null;

    public static String getUsername(Context context) {
        if (username != null)
            return username;
        SharedPreferences sharedPreferences= context.getSharedPreferences(Constants.PREFS_USERNAME_SAVE,
                Context.MODE_PRIVATE);
        username = sharedPreferences.getString(Constants.PREFS_USERNAME_KEY, null);
        return username;
    }

    public static void setUserName(final String name,Context context) {
        username = name;
        // Save the text in SharedPreference
        SharedPreferences sharedPreferences =context.getSharedPreferences(Constants.PREFS_USERNAME_SAVE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREFS_USERNAME_KEY, name);
        editor.apply();
    }

    public static void delUserName(String name,Context context){
        SharedPreferences sharedPreferences =context.getSharedPreferences(Constants.PREFS_USERNAME_SAVE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }
}
