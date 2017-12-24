package com.creativetrends.app.simplicity.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.simplicity.app.R;

/**
 * Created by Creative Trends Apps.
 */

public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    public boolean mListStyled;
    Context context;
    SharedPreferences preferences;
    TextView etUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SimplicityApplication.getContextOfApplication();
        addPreferencesFromResource(R.xml.about);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Preference versionnumber = findPreference("version_simple");
        versionnumber.setSummary(getResources().getString(R.string.app_name) + " " + StaticUtils.getAppVersionName(context));
        Preference changeLog = findPreference("change");
        changeLog.setOnPreferenceClickListener(this);

    }


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.about_app);
        if (!mListStyled) {
            View rootView = getView();
            if (rootView != null) {
                ListView list = rootView.findViewById(android.R.id.list);
                list.setPadding(0, 0, 0, 0);
                list.setDivider(null);
                mListStyled = true;
            }
        }

    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            //top preferences
            case "change":
                changeDialog();
                break;

            default:
                break;


        }
        return false;


    }

    private void changeDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.changelog_layout, null);
        etUsername = alertLayout.findViewById(R.id.change_text);
        etUsername.setText("Bug fixes");
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.whats_new));
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
        });
        alert.create();
        if (!getActivity().isFinishing()) {
            alert.show();
        }

    }

}
