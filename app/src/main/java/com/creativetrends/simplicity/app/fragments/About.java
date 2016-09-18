package com.creativetrends.simplicity.app.fragments;

// Created by Creative Trends Apps on 8/27/2016.

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.creativetrends.simplicity.app.R;
import com.creativetrends.simplicity.app.activities.EasterEggTask;

public class About extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final int MAX_CLICKS_TO_UNLOCK_EGG = 4;
    private int numTimesVersionClicked;
    private Preference prefVersion;
    public boolean mListStyled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preferences);
        prefVersion = findPreference("version_number");
        PackageManager pm = getActivity().getPackageManager();
        try {
            prefVersion.setSummary(pm.getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf("", "Error getting our own package name");
        }

        Preference rate_app = findPreference("rate_simplicity");
        Preference libraries = findPreference("libraries");
        Preference google_plus = findPreference("google_plus");
        Preference on_twitter = findPreference("on_twitter");
        Preference more_apps = findPreference("more_apps");
        rate_app.setOnPreferenceClickListener(this);
        libraries.setOnPreferenceClickListener(this);
        google_plus.setOnPreferenceClickListener(this);
        on_twitter.setOnPreferenceClickListener(this);
        more_apps.setOnPreferenceClickListener(this);
        prefVersion.setOnPreferenceClickListener(this);

    }
    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case "rate_simplicity":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getApplicationContext().getPackageName())));
                break;

            case "libraries":
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle("Libraries & Software Used");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    builder.setMessage(Html.fromHtml(getResources().getString(R.string.about_text), Html.FROM_HTML_MODE_LEGACY));
                }else{
                    //noinspection deprecation
                    builder.setMessage(Html.fromHtml(getResources().getString(R.string.about_text)));
                }
                builder.setPositiveButton(getResources().getString(R.string.ok), null);
                builder.setNegativeButton(null, null);
                builder.show();
                break;

            case "google_plus":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/116238843585710616103")));
                break;


            case "on_twitter":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/creativetrendsa")));
                break;

            case "more_apps":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5017142426512120457")));
                break;

            case "version_number":
                if (preference == prefVersion) {
                    if (++numTimesVersionClicked == MAX_CLICKS_TO_UNLOCK_EGG) {
                        numTimesVersionClicked = 0;
                        new EasterEggTask(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Ouch...", Toast.LENGTH_LONG).show();
                }
                return true;
        }

        return false;
    }

    @Override
    public void onStart(){
        super.onStart();
        getActivity().setTitle("About");
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!mListStyled) {
            View rootView = getView();
            if (rootView != null) {
                ListView list = (ListView) rootView.findViewById(android.R.id.list);
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

}