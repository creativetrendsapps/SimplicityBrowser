package com.creativetrends.app.simplicity.activities;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.creativetrends.app.simplicity.fragments.SettingsFragment;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;
import com.google.android.material.appbar.AppBarLayout;

/**
 * Created by Creative Trends Apps.
 */
public class SettingsActivity extends BaseActivity {
    SharedPreferences preferences;
    Toolbar toolbar;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(UserPreferences.getBoolean("dark_mode", false)){
            setTheme(R.style.SettingsThemeDark);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appBarLayout = findViewById(R.id.appbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getFragmentManager().beginTransaction().replace(R.id.settings_frame, new SettingsFragment()).commit();

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        try {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
            ActivityManager.TaskDescription description;
            description = new ActivityManager.TaskDescription("Simplicity", bm, 0);
            setTaskDescription(description);
        }catch (Exception i){
            i.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        preferences.edit().putString("should_sync", "false").apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            changes();
        } else
            getFragmentManager().popBackStack();

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void changes() {
        if (UserPreferences.getString("should_sync", "").equals("false")) {
            finish();
        } else if (UserPreferences.getString("should_sync", "").equals("true")) {
            shouldSync = true;
        }
    }


}