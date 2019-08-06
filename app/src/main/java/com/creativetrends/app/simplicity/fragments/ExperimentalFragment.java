package com.creativetrends.app.simplicity.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.simplicity.app.R;

/**
 * Created by Creative Trends Apps (Jorell Rutledge) 5/23/2018.
 */
@SuppressWarnings("deprecation")
public class ExperimentalFragment extends PreferenceFragment {
    public boolean mListStyled;
    Context context;
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SimplicityApplication.getContextOfApplication();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        addPreferencesFromResource(R.xml.flags_settings);
        myPrefListner = (prefs, key) -> {
            switch (key) {
                default:
            }

        };
    }


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();
        preferences.registerOnSharedPreferenceChangeListener(myPrefListner);
    }


    @Override
    public void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(myPrefListner);
    }

}
