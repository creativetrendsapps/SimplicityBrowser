package com.creativetrends.app.simplicity.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codekidlabs.storagechooser.StorageChooser;
import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.activities.AboutActivity;
import com.creativetrends.app.simplicity.activities.DonateActivity;
import com.creativetrends.app.simplicity.activities.SimplicityAccount;
import com.creativetrends.app.simplicity.activities.SimplicityProfile;
import com.creativetrends.app.simplicity.activities.WelcomeActivity;
import com.creativetrends.app.simplicity.adapters.BookmarkItems;
import com.creativetrends.app.simplicity.adapters.HistoryItems;
import com.creativetrends.app.simplicity.utils.ExportUtils;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Creative Trends Apps.
 */

@SuppressWarnings(value="deprecation")
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    Context context;
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private SharedPreferences preferences;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_STORAGE = 2;
    private static final String TAG = SettingsFragment.class.getSimpleName();
    Preference sync;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    Preference cache;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SimplicityApplication.getContextOfApplication();
        mAuth = FirebaseAuth.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        addPreferencesFromResource(R.xml.settings);
        sync = findPreference("sync_click");
        EditTextPreference pref = (EditTextPreference) findPreference("homepage");
        pref.setSummary(UserPreferences.getString("homepage",""));


        myPrefListner = (prefs, key) -> {
            UserPreferences.putString("should_sync", "true");
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

                case "dark_mode":

                    setRelaunch();
                    break;


                default:
                    break;
            }

        };

        Preference overflow = findPreference("about_app");
        Preference terms = findPreference("terms_set");
        Preference donate = findPreference("sim_donate");
        Preference backup_restore = findPreference("back_restore");
        Preference policy = findPreference("privacy_policy_set");
        Preference plugins = findPreference("plugins");
        overflow.setOnPreferenceClickListener(this);
        donate.setOnPreferenceClickListener(this);
        terms.setOnPreferenceClickListener(this);
        policy.setOnPreferenceClickListener(this);
        backup_restore.setOnPreferenceClickListener(this);
        plugins.setOnPreferenceClickListener(this);
        sync.setOnPreferenceClickListener(this);
        cache = findPreference("delete_cache");
        initializeCache();
        cache.setOnPreferenceClickListener(this);


    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        UserPreferences.putString("should_sync", "true");
        switch (key) {
            case "sync_click":
                if (currentUser != null) {
                    AlertDialog.Builder bnr = new AlertDialog.Builder(getActivity());
                    bnr.setTitle(getResources().getString(R.string.simplicity_account));
                    bnr.setMessage(getResources().getString(R.string.simplicity_account_manage));
                    bnr.setPositiveButton(R.string.update_profile, (dialog, which) -> {
                        Intent intent = new Intent(getActivity(), SimplicityProfile.class);
                        startActivity(intent);
                    });
                    bnr.setNegativeButton(R.string.logout, (dialog, which) -> {
                        resetUserInfo();
                        deleteItems();
                        new Handler().postDelayed(() -> {
                            try {
                                mAuth.signOut();
                                currentUser = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 900);

                    });
                    bnr.show();
                }else {
                    if (checkPlayServices()) {
                        Intent account = new Intent(getActivity(), SimplicityAccount.class);
                        startActivity(account);
                    }
                }
                break;

           case "sim_donate":
                Intent intent = new Intent(getActivity(), DonateActivity.class);
                startActivity(intent);
                break;

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

            case "plugins":
                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.settings_frame, new PluginsFragment())
                        .commit();
                break;

            case "delete_cache":
                AlertDialog.Builder cache_dialog = new AlertDialog.Builder(getActivity());
                cache_dialog.setTitle(getResources().getString(R.string.cache_dialog));
                cache_dialog.setMessage(getResources().getString(R.string.cache_dialog_summary, getResources().getString(R.string.app_name)));
                cache_dialog.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                    try {
                        deleteCache();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                cache_dialog.setNegativeButton(getResources().getString(R.string.cancel), null);
                cache_dialog.show();
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


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();
        preferences.registerOnSharedPreferenceChangeListener(myPrefListner);
        currentUser = mAuth.getCurrentUser();
        getUserInfo();
    }


    @Override
    public void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(myPrefListner);
    }


    private void buildPrivacy() {
        AlertDialog.Builder policy = new AlertDialog.Builder(getActivity());
        policy.setTitle(getResources().getString(R.string.privacy_policy));
        policy.setMessage(getResources().getString(R.string.policy_about));
        policy.setPositiveButton(R.string.ok, (arg0, arg1) -> {
        });
        policy.show();
    }

    private void buildTerms() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        AlertDialog.Builder terms = new AlertDialog.Builder(getActivity());
        terms.setTitle(getResources().getString(R.string.terms));
        terms.setMessage(getResources().getString(R.string.eula_string, year));
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


    public void getUserInfo() {
        try {
            if (currentUser != null) {
                sync.setTitle(currentUser.getDisplayName());
                sync.setSummary("Syncing to" + " "+ currentUser.getEmail());
                if (currentUser.getPhotoUrl() != null) {
                    Glide.with(this).load(currentUser.getPhotoUrl().toString()).apply(new RequestOptions().circleCrop())
                            .into(new SimpleTarget<Drawable>(78, 78) {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    sync.setIcon(resource);
                                }
                            });
                }
            }else{
                resetUserInfo();
            }
        } catch (NullPointerException z){
            z.printStackTrace();
        } catch (Exception p) {
            p.printStackTrace();
        }
    }

    private void resetUserInfo(){
        sync.setTitle(getString(R.string.app_name));
        sync.setSummary("Tap here to signin to your Simplicity account.");
        new Handler().postDelayed(() -> {
            try {
                Glide.with(this).load("")
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                sync.setIcon(resource);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 100);

    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                noPlayServices();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private void noPlayServices(){
        AlertDialog.Builder policy = new AlertDialog.Builder(getActivity());
        policy.setTitle("Cannot Create Account");
        policy.setMessage("Play Services was not found on your device. To create a Simplicity Account, Play Services needs to be enabled, or installed.");
        policy.setPositiveButton(R.string.ok, (arg0, arg1) -> {
        });
        policy.show();
    }



    private void deleteItems(){
        try {
            ArrayList<HistoryItems> listBookmarks = UserPreferences.getHistory();
            listBookmarks.clear();
            UserPreferences.saveHistory(listBookmarks);
            ArrayList<BookmarkItems> simpleBookmarks = UserPreferences.getBookmarks();
            simpleBookmarks.clear();
            UserPreferences.saveBookmarks(simpleBookmarks);
        }catch (Exception i){
            i.printStackTrace();
        }
    }

    private void setRelaunch(){
        View rootView = getView();
        if (rootView != null) {
            Snackbar snackbar =  Snackbar.make(rootView.findViewById(android.R.id.list), "Your changes will take effect when you restart Simplicity.", Snackbar.LENGTH_INDEFINITE);
            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setSingleLine(false);
                    snackbar.setAction("Restart?", v -> {
                        Intent mStartActivity = new Intent(getActivity(), WelcomeActivity.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }).show();
        }
    }


    private void deleteCache(){
        try{
            FileUtils.deleteQuietly(context.getCacheDir());
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.trimming));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                long size = getDirSize(context.getCacheDir());
                try {
                    if (cache != null) {
                        cache.setSummary(getResources().getString(R.string.current_cache_size) + ": " + readableFileSize(size));
                        //if (deleted) {
                        Toast.makeText(context, context.getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    }
                } catch (IllegalStateException e) {
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }, 5000);
        }catch (Exception z){
            z.printStackTrace();
        }
    }



    private void initializeCache() {
        long size = 0;
        size += getDirSize(context.getCacheDir());
        size += getDirSize(context.getExternalCacheDir());
        cache.setSummary(getResources().getString(R.string.current_cache_size) + ": " + readableFileSize(size));
    }

    public long getDirSize(File dir){
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
