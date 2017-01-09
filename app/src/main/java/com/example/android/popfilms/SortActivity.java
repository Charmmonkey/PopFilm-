package com.example.android.popfilms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by slyut on 12/16/2016.
 * Preference class. Uses deprecated methods, might need to change in future
 */

public class SortActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String LOG_TAG = SortActivity.class.getSimpleName();
    public static boolean preferenceChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    // Update summary on preference change
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(Utility.getSortPreferenceKey(this))){
            Preference sortPref = findPreference(key);
            ListPreference listPref = (ListPreference) sortPref;
            sortPref.setSummary(listPref.getEntry());
            preferenceChanged = true;
        }



    }

}
