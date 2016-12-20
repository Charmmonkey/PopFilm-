package com.example.android.popfilms;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by slyut on 12/17/2016.
 */

public class Utility {


    public static String getSortingPreference(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(getSortPreferenceKey(context),context.getString(R.string.Pref_values_popular));
    }

    public static String getSortPreferenceKey(Context context){
        return context.getString(R.string.Pref_sort_key);
    }

}
