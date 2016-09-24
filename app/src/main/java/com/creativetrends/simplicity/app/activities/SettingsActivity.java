package com.creativetrends.simplicity.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.creativetrends.simplicity.app.R;
import com.creativetrends.simplicity.app.fragments.Settings;
import com.creativetrends.simplicity.app.utils.PreferencesUtility;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new Settings()).commit();
    }



    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else
            getFragmentManager().popBackStack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();

                return true;


            case R.id.simplicity_help:
                Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "contact@creativetrendsapps.com", null));
                feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Feedback");
                feedbackIntent.putExtra(Intent.EXTRA_TEXT, "Here is some awesome feedback for " + getString(R.string.app_name) + "\n\n");
                startActivity(Intent.createChooser(feedbackIntent, "Send Feedback"));
                return true;




            default:
                return super.onOptionsItemSelected(item);


        }
    }
    private void changes(String key) {
        if (key.equals(PreferencesUtility.getString("apply_changes", ""))) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            finish();
        }
    }
}
