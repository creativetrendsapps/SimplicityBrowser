package com.creativetrends.app.simplicity.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.creativetrends.app.simplicity.fragments.ExperimentalFragment;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;

/**
 * Created by Creative Trends Apps (Jorell Rutledge) 5/23/2018.
 */
@SuppressWarnings(value="deprecation")
public class ExperimentalActivity extends BaseActivity {
    SharedPreferences preferences;
    Toolbar toolbar;
    //AdView adView;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        try {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
            ActivityManager.TaskDescription description;
            description = new ActivityManager.TaskDescription("Simplicity", bm, 0);
            setTaskDescription(description);
        }catch (Exception i){
            i.printStackTrace();
        }
        getFragmentManager().beginTransaction().replace(R.id.settings_frame, new ExperimentalFragment()).commit();

    }


    @Override
    public void onStart() {
        super.onStart();
        preferences.edit().putString("needs_change", "false").apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //BannerAd.resumeAd(adView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //BannerAd.pauseAd(adView);

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
        if (UserPreferences.getString("needs_change", "").equals("false")) {
            finish();
        } else if (UserPreferences.getString("needs_change", "").equals("true")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }


}