package com.creativetrends.app.simplicity.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.creativetrends.simplicity.app.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class DonateActivity extends AppCompatActivity implements View.OnClickListener, AppCompatSeekBar.OnSeekBarChangeListener {
    Toolbar mToolbar;
    AppCompatTextView amount, description;
    CardView pay;
    AppCompatSeekBar seekBar;
    int stepSize = 1;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        mToolbar = findViewById(R.id.toolbar);
        amount = findViewById(R.id.amount);
        description = findViewById(R.id.description);
        pay = findViewById(R.id.paypal);
        seekBar = findViewById(R.id.seek);
        seekBar.setOnSeekBarChangeListener(this);
        pay.setOnClickListener(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Drawable drawable = mToolbar.getNavigationIcon();
            if (drawable != null) {
                drawable.setColorFilter(ContextCompat.getColor(this, R.color.grey_color), PorterDuff.Mode.SRC_ATOP);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

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
        super.onBackPressed();
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress = Math.round(progress/stepSize) *stepSize;
        seekBar.setProgress(progress);
        switch (progress){
            case 0:
                amount.setText("$1.85");
                description.setText("Buys me a small coffee.");
                break;

            case 1:
                amount.setText("$2.37");
                description.setText("Buys me a large coffee.");
                break;

            case 2:
                amount.setText("$4.43");
                description.setText("Buys me an extra large coffee.");
                break;

            case 3:
                amount.setText("$6.49");
                description.setText("Buys me a small lunch.");
                break;
            case 4:
                amount.setText("$15.76");
                description.setText("Wow! Are you sure?");
                break;
            case 5:
                amount.setText("$20.91");
                description.setText("That's a lot!! Are you sure?");
                break;

            default:
                amount.setText("$1.85");
                description.setText("Buys me a small coffee.");
                break;

        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (seekBar.getProgress()){
            case 0:
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.paypal.me/creativetrends/1.85"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case 1:
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.paypal.me/creativetrends/2.37"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.paypal.me/creativetrends/4.43"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case 3:
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.paypal.me/creativetrends/6.49"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.paypal.me/creativetrends/15.76"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.paypal.me/creativetrends/20.91"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            default:
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.paypal.me/creativetrends/1.85"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;

        }

    }
}
