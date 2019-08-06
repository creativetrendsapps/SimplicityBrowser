package com.creativetrends.app.simplicity.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.simplicity.app.R;

/**
 * Created by Creative Trends Apps.
 */

@SuppressWarnings("deprecation")
public class PluginsFragment extends PreferenceFragment {
    public boolean mListStyled;
    Context context;
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SimplicityApplication.getContextOfApplication();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        addPreferencesFromResource(R.xml.plugin_settings);


        myPrefListner = (prefs, key) -> {
            switch (key) {
                default:
                    break;
            }

        };

    }


    @Override
    public void onStart() {
        super.onStart();
        View rootView = getView();
        if (rootView != null) {
            ListView list = rootView.findViewById(android.R.id.list);
            list.setPadding(0, 0, 0, 0);
            list.setDivider(null);
            mListStyled = true;
        }

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
