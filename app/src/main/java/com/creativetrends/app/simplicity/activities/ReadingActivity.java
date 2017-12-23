package com.creativetrends.app.simplicity.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.simplicity.app.R;

/**
 * Created by Creative Trends Apps.
 */

public class ReadingActivity extends AppCompatActivity {
    TextView textTitle;
    TextView textBody;
    boolean inverted = true;
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textTitle = findViewById(R.id.textViewTitle);
        try {
            //noinspection ConstantConditions
            textTitle.setText(getIntent().getExtras().getString("title"));
        }catch(NullPointerException ignored){
        }catch(Exception i){
            i.printStackTrace();
        }
        textBody = findViewById(R.id.textViewBody);
        textBody.setText(getIntent().getExtras().getString("text"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reader, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        switch(id){
            case R.id.action_invert:
                inverted = !inverted;
                ScrollView view = findViewById(R.id.scroll);
                if(!inverted) {
                    view.setBackgroundColor(Color.BLACK);
                    textTitle.setTextColor(Color.WHITE);
                    textBody.setTextColor(Color.WHITE);
                    mToolbar = findViewById(R.id.toolbar);
                    mToolbar.setBackgroundColor(Color.BLACK);
                    getWindow().setStatusBarColor(Color.BLACK);
                    Drawable drawable = mToolbar.getNavigationIcon();
                    if (drawable != null) {
                        drawable.setColorFilter(ContextCompat.getColor(this, R.color.grey_color), PorterDuff.Mode.SRC_ATOP);
                    }
                    mToolbar.setTitleTextColor(Color.WHITE);

                } else{
                    view.setBackgroundColor(Color.WHITE);
                    textTitle.setTextColor(Color.BLACK);
                    textBody.setTextColor(Color.BLACK);
                    mToolbar = findViewById(R.id.toolbar);
                    mToolbar.setBackgroundColor(Color.WHITE);
                    getWindow().setStatusBarColor(StaticUtils.fetchColorPrimaryDark(this));
                    Drawable drawable = mToolbar.getNavigationIcon();
                    if (drawable != null) {
                        drawable.setColorFilter(ContextCompat.getColor(this, R.color.grey_color), PorterDuff.Mode.SRC_ATOP);
                    }
                    mToolbar.setTitleTextColor(Color.BLACK);
                }

                break;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}
