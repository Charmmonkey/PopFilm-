package com.example.android.popfilms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by slyut on 12/16/2016.
 */

public class SortActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String LOG_TAG = SortActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Log.v(LOG_TAG, " Sort Activity triggered");
        if(key.equals(Utility.getSortPreferenceKey(this))){
            Log.v(LOG_TAG,Utility.getSortPreferenceKey(this).toString());
            Preference sortPref = findPreference(key);
            ListPreference listPref = (ListPreference) sortPref;
            sortPref.setSummary(listPref.getEntry());
        }


    }

}
