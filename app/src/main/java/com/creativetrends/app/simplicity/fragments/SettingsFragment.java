package com.creativetrends.app.simplicity.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.activities.AboutActivity;
import com.creativetrends.app.simplicity.preferences.MaterialEditText;
import com.creativetrends.simplicity.app.R;

/**
 * Created by Creative Trends Apps.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    public boolean mListStyled;
    Context context;
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private SharedPreferences preferences;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SimplicityApplication.getContextOfApplication();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        addPreferencesFromResource(R.xml.settings);
        Preference colored_nav = findPreference("nav_color");
        MaterialEditText pref = (MaterialEditText) findPreference("homepage");
        if (pref.getText().contains("http://") || pref.getText().contains("https://")) {
            pref.setSummary(pref.getText());
        } else if (!pref.getText().contains("http://") || !pref.getText().contains("https://")) {
            pref.setSummary("http://"+pref.getText());
        }

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


        myPrefListner = (prefs, key) -> {
            switch (key) {

                case "homepage":
                    MaterialEditText pref1 = (MaterialEditText) findPreference("homepage");

                    if (pref1.getText().contains("http://") || pref1.getText().contains("https://")) {
                        pref1.setSummary(pref1.getText());
                        preferences.edit().putString("homepage", pref1.getText()).apply();
                    } else if (!pref1.getText().contains("http://") || !pref1.getText().contains("https://")) {
                        pref1.setSummary("http://"+ pref1.getText());
                        preferences.edit().putString("homepage", "http://"+ pref1.getText()).apply();
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

        };

        Preference overflow = findPreference("about_app");
        Preference terms = findPreference("terms_set");
        Preference policy = findPreference("privacy_policy_set");
        overflow.setOnPreferenceClickListener(this);
        terms.setOnPreferenceClickListener(this);
        policy.setOnPreferenceClickListener(this);

    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {

            case "about_app":
                Intent Intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(Intent);
                break;


            case "terms_set":
                buildTerms(R.style.DialogAnimation);
                break;

            case "privacy_policy_set":
                buildPrivacy(R.style.DialogAnimation);
                break;


            default:
                break;


        }

        return false;


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

    public boolean isNavigationBarAvailable() {
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        boolean hasMenuKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_MENU);
        return (!(hasBackKey && hasHomeKey && !hasMenuKey));
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


    private void buildPrivacy(int animationSource) {

        AlertDialog.Builder policy = new AlertDialog.Builder(getActivity());
        policy.setTitle(getResources().getString(R.string.privacy_policy));
        //noinspection deprecation
        policy.setMessage(Html.fromHtml(getString(R.string.policy_about)));
        policy.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        try{
        AlertDialog dialog = policy.create();
        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
        }catch(NullPointerException ignored){
        }catch (Exception i){
            i.printStackTrace();
        }
    }

    private void buildTerms(int animationSource) {
            AlertDialog.Builder terms = new AlertDialog.Builder(getActivity());
            terms.setTitle(getResources().getString(R.string.terms));
            terms.setMessage(getResources().getString(R.string.eula_string));
            terms.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
        try {
            AlertDialog dialog = terms.create();
            dialog.getWindow().getAttributes().windowAnimations = animationSource;
            dialog.show();
        }catch(NullPointerException ignored){
        }catch (Exception i){
            i.printStackTrace();
        }
    }

}