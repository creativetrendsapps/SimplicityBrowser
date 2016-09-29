

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
import android.support.design.widget.Snackbar;
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

public class Settings extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private SharedPreferences.OnSharedPreferenceChangeListener preferencesListener;
    private SharedPreferences savedPreferences;
    private static Context context;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = Settings.class.getSimpleName();
    public boolean mListStyled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        context = SimplicityApplication.getContextOfApplication();
        final Preference homepagePreference = findPreference("homepage");
        savedPreferences = getPreferenceScreen().getSharedPreferences();
        homepagePreference.setSummary(savedPreferences.getString("homepage", ""));
        final Preference defaultFontSizePreference = findPreference("default_font_size");
        String defaultFontSizeString = savedPreferences.getString("default_font_size", "100");
        defaultFontSizePreference.setSummary(defaultFontSizeString + "%%");
        final Preference search_engine_choice = findPreference("search_engine");
        String search_engine = savedPreferences.getString("search_engine", "https://www.google.com/search?q=");
        search_engine_choice.setSummary(search_engine);
        preferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                @SuppressWarnings("ConstantConditions") Snackbar snackbar = Snackbar.make(getView(), R.string.app_restart, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                });
                snackbar.show();

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
                        search_engine_choice.setSummary(newJavaScriptEnabledSearchString);
                        MainActivity.defaultSearch = newJavaScriptEnabledSearchString;
                        NewWindow.defaultSearch = newJavaScriptEnabledSearchString;
                        break;


                    case "homepage":
                        homepagePreference.setSummary(sharedPreferences.getString("homepage", "https://www.google.com"));
                        MainActivity.homepage = sharedPreferences.getString("homepage", "https://www.google.com");
                        NewWindow.homepage = sharedPreferences.getString("homepage", "https://www.google.com");
                        return;

                    case "default_font_size":
                        String newDefaultFontSizeString = sharedPreferences.getString("default_font_size", "100");
                        MainActivity.webView.getSettings().setTextZoom(Integer.valueOf(newDefaultFontSizeString));
                        NewWindow.webView.getSettings().setTextZoom(Integer.valueOf(newDefaultFontSizeString));
                        defaultFontSizePreference.setSummary(newDefaultFontSizeString + "%%");

                    case "enable_location":
                        //noinspection StatementWithEmptyBody
                        if (sharedPreferences.getBoolean("enable_location", false)) {
                            requestLocationPermission();
                        } else {

                        }
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
                startActivity(new Intent(getActivity(), DonationActivity.class));
                break;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.settings);
        View rootView = getView();
        if (rootView != null) {
            ListView list = (ListView) rootView.findViewById(android.R.id.list);
            list.setPadding(0, 0, 0, 0);
            list.setDivider(null);
            mListStyled = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        savedPreferences.registerOnSharedPreferenceChangeListener(preferencesListener);
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
