package com.creativetrends.app.simplicity.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.creativetrends.app.simplicity.fragments.SettingsFragment;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.simplicity.app.R;

/**
 * Created by Creative Trends Apps.
 */

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    Toolbar toolbar;
    //AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Drawable drawable = toolbar.getNavigationIcon();
            if (drawable != null) {
                drawable.setColorFilter(ContextCompat.getColor(this, R.color.grey_color), PorterDuff.Mode.SRC_ATOP);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), null, StaticUtils.fetchColorPrimary(this)));
        }
        getFragmentManager().beginTransaction().replace(R.id.settings_frame, new SettingsFragment()).commit();
    }


    @Override
    protected void onStart() {
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
        if (preferences.getString("needs_change", "").equals("false")) {
            finish();
        } else if (preferences.getString("needs_change", "").equals("true")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }


}
