package com.creativetrends.app.simplicity.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ListView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.activities.AboutActivity;
import com.creativetrends.app.simplicity.utils.ExportUtils;
import com.creativetrends.simplicity.app.R;

import java.io.File;

/**
 * Created by Creative Trends Apps.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    public boolean mListStyled;
    Context context;
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private SharedPreferences preferences;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_STORAGE = 2;
    private static final String TAG = SettingsFragment.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SimplicityApplication.getContextOfApplication();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        addPreferencesFromResource(R.xml.settings);
        Preference colored_nav = findPreference("nav_color");
        EditTextPreference pref = (EditTextPreference) findPreference("homepage");
        if (pref.getText().contains("http://") || pref.getText().contains("https://")) {
            pref.setSummary(pref.getText());
        } else if (!pref.getText().contains("http://") || !pref.getText().contains("https://")) {
            pref.setSummary("http://" + pref.getText());
        }

        try {
            if (hasSoftKeys() && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colored_nav.setEnabled(true);
                colored_nav.setSelectable(true);
                colored_nav.setSummary("Enable to color the navigation bar.");
            } else if (!hasSoftKeys() && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colored_nav.setEnabled(false);
                colored_nav.setSelectable(false);
                colored_nav.setSummary("Your device does not support this feature.");
            }
        } catch (Exception ignored) {
        }



        myPrefListner = (prefs, key) -> {
            switch (key) {

                case "homepage":
                    EditTextPreference pref1 = (EditTextPreference) findPreference("homepage");

                    if (pref1.getText().contains("http://") || pref1.getText().contains("https://")) {
                        pref1.setSummary(pref1.getText());
                        preferences.edit().putString("homepage", pref1.getText()).apply();
                    } else if (!pref1.getText().contains("http://") || !pref1.getText().contains("https://")) {
                        pref1.setSummary("http://" + pref1.getText());
                        preferences.edit().putString("homepage", "http://" + pref1.getText()).apply();
                    }
                    break;
                case "enable_location":
                    if (preferences.getBoolean("enable_location", false)) {
                        requestLocationPermission();
                    } else {
                        Log.i("Settings", "Location enabled");
                    }
                    break;

                case "address_bar":
                    preferences.edit().putString("needs_change", "true").apply();
                    break;

                default:
                    break;
            }

        };

        Preference overflow = findPreference("about_app");
        Preference terms = findPreference("terms_set");
        //Preference sync = findPreference("sim_cloud");
        Preference backup_restore = findPreference("back_restore");
        Preference policy = findPreference("privacy_policy_set");
        overflow.setOnPreferenceClickListener(this);
        terms.setOnPreferenceClickListener(this);
        policy.setOnPreferenceClickListener(this);
        //sync.setOnPreferenceClickListener(this);
        backup_restore.setOnPreferenceClickListener(this);


    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {

           /* case "sim_cloud":
                Intent intent = new Intent(getActivity(), SimplicitySignIn.class);
                startActivity(intent);
                break;*/

            case "back_restore":
                if (!hasStoragePermission()) {
                    requestStoragePermission();
                } else {
                    AlertDialog.Builder bnr = new AlertDialog.Builder(getActivity());
                    bnr.setTitle(getResources().getString(R.string.backup_restore));
                    bnr.setMessage(getResources().getString(R.string.backup_restore_message));
                    bnr.setPositiveButton(R.string.backup, (arg0, arg1) -> backupSettings());
                    bnr.setNegativeButton(R.string.restore, (arg0, arg1) -> restoreSettings());
                    bnr.setNeutralButton(R.string.cancel, null);
                    bnr.show();

                }
                return true;

            
            case "about_app":
                Intent Intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(Intent);
                break;


            case "terms_set":
                buildTerms();
                break;

            case "privacy_policy_set":
                buildPrivacy();
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

    public boolean hasSoftKeys(){
        boolean hasSoftwareKeys;
        //c = context; use getContext(); in fragments, and in activities you can
        //directly access the windowManager();

        Display d = getActivity().getWindowManager().getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        hasSoftwareKeys =  (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;

        return hasSoftwareKeys;
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


    private void buildPrivacy() {

        AlertDialog.Builder policy = new AlertDialog.Builder(getActivity());
        policy.setTitle(getResources().getString(R.string.privacy_policy));
        //noinspection deprecation
        policy.setMessage(Html.fromHtml(getString(R.string.policy_about)));
        policy.setPositiveButton(R.string.ok, (arg0, arg1) -> {

        });
        try {
            AlertDialog dialog = policy.create();
            //noinspection ConstantConditions
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.show();
        } catch (NullPointerException ignored) {
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

    private void buildTerms() {
        AlertDialog.Builder terms = new AlertDialog.Builder(getActivity());
        terms.setTitle(getResources().getString(R.string.terms));
        terms.setMessage(getResources().getString(R.string.eula_string));
        terms.setPositiveButton(R.string.ok, (arg0, arg1) -> {

        });
        terms.show();
    }

    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_STORAGE);
        } else {
            hasStoragePermission();
        }
    }


    public boolean hasStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(getActivity(), storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void backupSettings(){
        StorageChooser chooser = new StorageChooser.Builder()
                .setTheme(myTheme(context))
                .withActivity(getActivity())
                .allowAddFolder(true)
                .allowCustomPath(true)
                .withFragmentManager(getFragmentManager())
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .showFoldersInGrid(true)
                .withMemoryBar(true)
                .build();
        chooser.setOnSelectListener(path -> {
            File fil = new File(path, "simplicity.sbh");
            ExportUtils.writeToFile(fil, context);
        });
        chooser.show();
    }

    private void restoreSettings(){
        StorageChooser chooser = new StorageChooser.Builder()
                .setTheme(myTheme(context))
                .setDialogTitle(getString(R.string.restore))
                .withActivity(getActivity())
                .allowAddFolder(false)
                .allowCustomPath(true)
                .withFragmentManager(getFragmentManager())
                .setType(StorageChooser.FILE_PICKER)
                .withMemoryBar(true)
                .build();
        chooser.setOnSelectListener(path -> {
            File fil = new File(path);
            ExportUtils.readFromFile(fil, context);

        });

        chooser.show();
    }


    private StorageChooser.Theme myTheme(Context context) {
        StorageChooser.Theme theme = new StorageChooser.Theme(context);
        int[] myScheme;
        myScheme = theme.getDefaultScheme();
        myScheme[StorageChooser.Theme.OVERVIEW_HEADER_INDEX] = ContextCompat.getColor(context, R.color.md_blue_600);
        myScheme[StorageChooser.Theme.OVERVIEW_MEMORYBAR_INDEX] = ContextCompat.getColor(context, R.color.md_blue_600);
        myScheme[StorageChooser.Theme.OVERVIEW_INDICATOR_INDEX] = ContextCompat.getColor(context, R.color.md_blue_600);
        myScheme[StorageChooser.Theme.SEC_FOLDER_CREATION_BG_INDEX] = ContextCompat.getColor(context, R.color.md_blue_600);
        myScheme[StorageChooser.Theme.SEC_ADDRESS_BAR_BG] = ContextCompat.getColor(context, R.color.md_blue_600);
        myScheme[StorageChooser.Theme.SEC_SELECT_LABEL_INDEX] = ContextCompat.getColor(context, R.color.black);
        myScheme[StorageChooser.Theme.SEC_FOLDER_TINT_INDEX] = ContextCompat.getColor(context, R.color.md_blue_600);
        myScheme[StorageChooser.Theme.SEC_TEXT_INDEX] = ContextCompat.getColor(context, R.color.black);
        theme.setScheme(myScheme);
        return theme;
            }
        }
