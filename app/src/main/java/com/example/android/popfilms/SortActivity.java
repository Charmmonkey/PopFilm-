package com.example.android.popfilms;

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
    public static final String KEY_PREF_SORT = "sort_setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(KEY_PREF_SORT)){
            Preference sortPref = findPreference(key);
            ListPreference listPref = (ListPreference) sortPref;
            sortPref.setSummary(listPref.getEntry());
        }


    }

}
