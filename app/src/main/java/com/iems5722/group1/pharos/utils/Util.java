package com.iems5722.group1.pharos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.iems5722.group1.pharos.Constants;

/**
 * Created by Sora on 7/4/17.
 */

public class Util {
    private static String username = null;
    private static SharedPreferences sharedPreferences;
    public static String getUsername(Context context) {
        sharedPreferences= context.getSharedPreferences(Constants.PREFS_USERNAME_SAVE,
                Context.MODE_PRIVATE);
        if (username != null)
            return username;
        username = sharedPreferences.getString(Constants.PREFS_USERNAME_KEY, "null");
        return username;
    }

    public static void setUserName(final String name,Context context) {
        sharedPreferences= context.getSharedPreferences(Constants.PREFS_USERNAME_SAVE,
                Context.MODE_PRIVATE);
        username = name;
        // Save the text in SharedPreference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREFS_USERNAME_KEY, name);
        editor.apply();
    }

    public static void delUserName(String name,Context context){
        sharedPreferences= context.getSharedPreferences(Constants.PREFS_USERNAME_SAVE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.PREFS_USERNAME_KEY);
        editor.apply();
       // editor.remove(Constants.PREFS_USERNAME_KEY).apply();
        editor.clear().commit();
//        editor.commit();
//        editor.apply();
    }
}
