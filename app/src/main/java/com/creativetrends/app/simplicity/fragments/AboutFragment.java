package com.creativetrends.app.simplicity.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.simplicity.app.R;

/**
 * Created by Creative Trends Apps.
 */

@SuppressWarnings("ALL")
public class AboutFragment extends PreferenceFragment  {
    public boolean mListStyled;
    Context context;
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SimplicityApplication.getContextOfApplication();
        addPreferencesFromResource(R.xml.about_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Preference version = findPreference("version_simple");
        version.setSummary(getResources().getString(R.string.app_name) + " " + StaticUtils.getAppVersionName(context));


    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.about_app);

    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
