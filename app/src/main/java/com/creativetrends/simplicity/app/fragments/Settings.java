package com.creativetrends.simplicity.app.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.creativetrends.simplicity.app.R;
import com.creativetrends.simplicity.app.activities.DonationActivity;
import com.creativetrends.simplicity.app.activities.MainActivity;
import com.creativetrends.simplicity.app.activities.NewWindow;
import com.creativetrends.simplicity.app.activities.SimplicityApplication;
import com.creativetrends.simplicity.app.utils.PreferencesUtility;

public class Settings extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private SharedPreferences.OnSharedPreferenceChangeListener preferencesListener;
    private SharedPreferences savedPreferences;
    Context context;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = Settings.class.getSimpleName();
    public static final String RESTART_CODE = "changed_setting";
    public static final String RESTART_RESULTS = "needs_restart";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        context = SimplicityApplication.getContextOfApplication();


        final Preference homepagePreference = findPreference("homepage");

        savedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        homepagePreference.setSummary(savedPreferences.getString("homepage", ""));



        preferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                PreferencesUtility.putString(RESTART_CODE, RESTART_RESULTS);
                Log.i("Changes made", "App needs refresh");

                 switch (key) {

                    case "javascript_enabled":
                        MainActivity.javaScriptEnabled = sharedPreferences.getBoolean("javascript_enabled", false);
                        MainActivity.webView.getSettings().setJavaScriptEnabled(MainActivity.javaScriptEnabled);
                        MainActivity.webView.reload();
                        break;

                    case "first_party_cookies_enabled":
                        MainActivity.firstPartyCookiesEnabled = sharedPreferences.getBoolean("first_party_cookies_enabled", false);
                        MainActivity.cookieManager.setAcceptCookie(MainActivity.firstPartyCookiesEnabled);
                        MainActivity.webView.reload();
                        break;

                    case "third_party_cookies_enabled":
                        MainActivity.thirdPartyCookiesEnabled = sharedPreferences.getBoolean("third_party_cookies_enabled", false);
                        if (Build.VERSION.SDK_INT >= 21) {
                            MainActivity.cookieManager.setAcceptThirdPartyCookies(MainActivity.webView, MainActivity.thirdPartyCookiesEnabled);
                            MainActivity.webView.reload();
                        }
                        break;

                    case "search_engine":
                        String newJavaScriptEnabledSearchString = sharedPreferences.getString("search_engine", "https://duckduckgo.com/?q=");
                        MainActivity.defaultSearch = newJavaScriptEnabledSearchString;
                        NewWindow.defaultSearch = newJavaScriptEnabledSearchString;
                        break;


                    case "homepage":
                        MainActivity.homepage = sharedPreferences.getString("homepage", "https://www.google.com");
                        NewWindow.homepage = sharedPreferences.getString("homepage", "https://www.google.com");
                        break;


                    case "enable_location":
                        //noinspection StatementWithEmptyBody
                        if (sharedPreferences.getBoolean("enable_location", false)) {
                            requestLocationPermission();
                        } else {

                        }
                        break;

                    default:
                        break;
                }
                Log.v("Preference Change", key + " changed in Settings");
            }
        };


    Preference donate = findPreference("donate_app");
    donate.setOnPreferenceClickListener(this);
    Preference about = findPreference("about_app");
    about.setOnPreferenceClickListener(this);

    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case "about_app":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new About()).commit();
                break;



            case "donate_app":
                Intent donateIntent = new Intent(getActivity(), DonationActivity.class);
                startActivity(donateIntent);
                break;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.settings);
       
    }

    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public void onPause() {
        super.onPause();
        savedPreferences.unregisterOnSharedPreferenceChangeListener(preferencesListener);
    }

    private void requestLocationPermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = ContextCompat.checkSelfPermission(context, locationPermission);
        String[] permissions = new String[] { locationPermission };
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION);
        } else
            Log.e(TAG, "We already have storage permission.");
    }
}
