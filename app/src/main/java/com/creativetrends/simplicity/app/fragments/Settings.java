package com.creativetrends.simplicity.app.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.creativetrends.simplicity.app.R;
import com.creativetrends.simplicity.app.activities.DonationActivity;
import com.creativetrends.simplicity.app.activities.MainActivity;
import com.creativetrends.simplicity.app.activities.NewWindow;
import com.creativetrends.simplicity.app.activities.SimplicityApplication;
import com.creativetrends.simplicity.app.preferences.MaterialEditText;
import com.creativetrends.simplicity.app.lock.SmartPassLock;

public class Settings extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;
    private SharedPreferences preferences;

    public boolean mListStyled;
    Context context;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = Settings.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        context = SimplicityApplication.getContextOfApplication();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Preference colored_nav = findPreference("nav_color");
        Preference donate = findPreference("donate_app");
        Preference about = findPreference("about_app");
        Preference whatsnew = findPreference("whats_new");
        donate.setOnPreferenceClickListener(this);
        about.setOnPreferenceClickListener(this);
        whatsnew.setOnPreferenceClickListener(this);

        if(!isNavigationBarAvailable() && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colored_nav.setEnabled(false);
            colored_nav.setSelectable(false);
            colored_nav.setSummary("Your device does not support this feature.");
            Log.i("Hardware buttons", "disable this preference");
        }else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isNavigationBarAvailable()) {
                colored_nav.setEnabled(true);
                colored_nav.setSelectable(true);
                colored_nav.setSummary("Enable to color the navigation bar.");
            }
         }


        prefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                preferences.edit().putString("changed", "true").apply();
                Log.i("Something changed", "should restart");
                switch (key) {
                    case "smart_pass":
                        if (preferences.getBoolean("smart_pass", false)) {
                            Intent intent = new Intent(getActivity(), SmartPassLock.class);
                            startActivity(intent);
                        } else {
                            Log.i("Settings", "Smart Pass disabled");
                        }
                        break;

                    case "no_ads":
                        if (preferences.getBoolean("no_ads", false)) {
                            checkAdAway();
                        } else {
                            Log.i("Settings", "AdAway disabled");
                        }
                        break;

                    case "javascript_enabled":
                        if (preferences.getBoolean("javascript_enabled", false)) {
                            MainActivity.javaScriptEnabled = preferences.getBoolean("javascript_enabled", false);
                            MainActivity.webView.getSettings().setJavaScriptEnabled(MainActivity.javaScriptEnabled);
                            MainActivity.webView.reload();
                        } else {
                            Log.i("Settings", "Smart Pass disabled");
                        }
                        break;

                    case "first_party_cookies_enabled":
                        MainActivity.firstPartyCookiesEnabled = preferences.getBoolean("first_party_cookies_enabled", false);
                        MainActivity.cookieManager.setAcceptCookie(MainActivity.firstPartyCookiesEnabled);
                        MainActivity.webView.reload();
                        preferences.edit().putString("apply_changes", "true").apply();
                        break;

                    case "third_party_cookies_enabled":
                        MainActivity.thirdPartyCookiesEnabled = preferences.getBoolean("third_party_cookies_enabled", false);
                        if (Build.VERSION.SDK_INT >= 21) {
                            MainActivity.cookieManager.setAcceptThirdPartyCookies(MainActivity.webView, MainActivity.thirdPartyCookiesEnabled);
                            MainActivity.webView.reload();
                            preferences.edit().putString("apply_changes", "true").apply();
                        }
                        break;

                    case "search_engine":
                        String newJavaScriptEnabledSearchString = preferences.getString("search_engine", "https://duckduckgo.com/?q=");
                        MainActivity.defaultSearch = newJavaScriptEnabledSearchString;
                        NewWindow.defaultSearch = newJavaScriptEnabledSearchString;
                        break;

                    case "homepage":
                        MainActivity.homepage = preferences.getString("home_page", "");
                        NewWindow.homepage = preferences.getString("home_page", "");
                        MaterialEditText pref = (MaterialEditText) findPreference("homepage");
                        pref.setSummary(pref.getText());
                        if (pref.getText().contains("http://") || pref.getText().contains("https://")) {
                            preferences.edit().putString("home_page", pref.getText()).apply();
                        } else {
                            preferences.edit().putString("home_page", "").apply();
                            Toast.makeText(getActivity(), "Please include https: or http: in the address", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case "enable_location":
                        if (preferences.getBoolean("enable_location", false)) {
                            requestLocationPermission();
                        } else {
                            Log.i("Settings", "Location enabled");
                        }
                        break;

                    default:
                        break;
                }

                // what's going on, dude?
                Log.v("SharedPreferenceChange", key + " changed in SettingsFragment");
            }
        };
    }


        @Override
        public boolean onPreferenceClick (Preference preference){
            String key = preference.getKey();
            Log.v("OnPreferenceClick", key + " clicked in SettingsFragment");
            switch (key) {
                case "donate_app":
                    Intent donateIntent = new Intent(getActivity(), DonationActivity.class);
                    startActivity(donateIntent);
                    return true;
                case "about_app":
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out)
                            .addToBackStack(null).replace(R.id.content_frame,
                            new About()).commit();
                    return true;
                case "whats_new":
                    newDialog();
                    return true;
                default:
                    return false;
            }


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

        try{
            MaterialEditText start = (MaterialEditText) findPreference("homepage");
            if(preferences.getString("home_page", "").equals("")) {
            start.setSummary("Current:" + " " +"https://www.google.com");
            } else {
             start.setSummary("Current:" + " " + preferences.getString("home_page", ""));
            }
        }catch(Exception ignored){

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        preferences.registerOnSharedPreferenceChangeListener(prefChangeListener);
    }


    @Override
    public void onPause() {
        super.onPause();

        preferences.unregisterOnSharedPreferenceChangeListener(prefChangeListener);
    }

    private void requestLocationPermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = ContextCompat.checkSelfPermission(context, locationPermission);
        String[] permissions = new String[]{locationPermission};
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION);
        } else
            Log.e(TAG, "We already have storage permission.");
    }

    private boolean checkAdAway() {
        if (packageExists("org.adaway")) {
            Toast.makeText(getActivity(), "AdAway may interfere with Simplicity's AdBlocker", Toast.LENGTH_LONG).show();
            return true;
        }
        if (packageExists("org.adaway_60")) {
            Toast.makeText(getActivity(), "AdAway may interfere with Simplicity's AdBlocker", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean packageExists(final String packageName) {
        try {
            ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo(packageName, 0);
            if (info == null) {
                Log.i("Settings", "Smart Pass disabled");
                return false;
            }
            return true;
        } catch (Exception ignored) {

        }
        return false;
    }

    private void newDialog() {
        AlertDialog.Builder whats_new = new AlertDialog.Builder(getActivity());
        whats_new.setTitle("What's New");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            whats_new.setMessage(Html.fromHtml(getResources().getString(R.string.about_new), Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            whats_new.setMessage(Html.fromHtml(getResources().getString(R.string.about_new)));
        }
        whats_new.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        whats_new.show();

    }


    public boolean isNavigationBarAvailable(){
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        return (!(hasBackKey && hasHomeKey));
    }
}
