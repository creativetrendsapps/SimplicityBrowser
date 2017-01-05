package com.creativetrends.simplicity.app.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.creativetrends.simplicity.app.R;
import com.creativetrends.simplicity.app.fragments.Settings;
import com.creativetrends.simplicity.app.utils.Miscellany;

public class SettingsActivity extends AppCompatActivity {
SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new Settings()).commit();
    }



    @Override
    protected void onResume(){
        super.onResume();
        preferences.edit().putString("changed", "false").apply();{

        }
    }

    @Override
    public void onBackPressed() {
        if (preferences.getString("changed", "").equals("true")) {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
                super.onBackPressed();
         }
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


            case R.id.rate_folio:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                return true;


            case R.id.settings_feedback:
                AlertDialog.Builder terms = new AlertDialog.Builder(SettingsActivity.this);
                terms.setTitle(getResources().getString(R.string.help));
                terms.setMessage("Get help and support by choosing one of the options below.");
                terms.setPositiveButton(R.string.helpfeed, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "contact@creativetrendsapps.com", null));
                        feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Feedback");
                        feedbackIntent.putExtra(Intent.EXTRA_TEXT, "Here is some awesome feedback for " + getString(R.string.app_name) + "\n\n" + Miscellany.getDeviceInfo(SettingsActivity.this) + "\n\n");
                        startActivity(Intent.createChooser(feedbackIntent, getString(R.string.choose_email_client)));

                    }
                });
                terms.setNegativeButton(R.string.bugs, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent bugIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "bugs@creativetrendsapps.com", null));
                        bugIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Bug");
                        bugIntent.putExtra(Intent.EXTRA_TEXT, "I found a bug in" + " " + getString(R.string.app_name) + "\n\n" + Miscellany.getDeviceInfo(SettingsActivity.this) + "\n\n");
                        startActivity(Intent.createChooser(bugIntent, getString(R.string.choose_email_client)));
                    }

                });
                terms.setNeutralButton(R.string.cancel, null);
                terms.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }
}
