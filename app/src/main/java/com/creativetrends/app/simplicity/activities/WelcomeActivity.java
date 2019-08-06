package com.creativetrends.app.simplicity.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.PrefManager;
import com.creativetrends.simplicity.app.R;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private PrefManager prefManager;

    private static final int REQUEST_STORAGE = 300;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        prefManager = new PrefManager(this);
        MaterialButton appCompatButton = findViewById(R.id.wel_button);
        AppCompatTextView termsTextView = findViewById(R.id.wel_term);
        AppCompatTextView policyTextView = findViewById(R.id.wel_pri);
        appCompatButton.setOnClickListener(this);
        termsTextView.setOnClickListener(this);
        policyTextView.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }else{
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wel_button:
                if(!hasStoragePermission(this)){
                    requestStoragePermission(this);
                }else{
                    launchHomeScreen();
                }
                break;

            case R.id.wel_term:
                showTerms();
                break;

            case R.id.wel_pri:
                showPolicy();
                break;

        }
    }


    @Override
    protected void onResume() {
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public static void requestStoragePermission(Activity activity) {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission(activity)) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_STORAGE);
        }
    }

    public static boolean hasStoragePermission(Activity activity) {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(activity, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void launchHomeScreen() {
        startActivity(new Intent(WelcomeActivity.this, WelcomeActivitySign.class));
        finish();
        if(hasStoragePermission(this)) {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getResources().getString(R.string.app_name ) + File.separator + "Simplicity Downloads");
            if (!imageStorageDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                imageStorageDir.mkdirs();
                Log.i("created dir", imageStorageDir.getPath());
            }else{
                Log.i("", "");
            }
        }
    }


    private void showTerms(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        AlertDialog.Builder terms = new AlertDialog.Builder(this);
        terms.setTitle(getResources().getString(R.string.terms));
        terms.setMessage(getResources().getString(R.string.eula_string, year));
        terms.setPositiveButton(R.string.ok, (arg0, arg1) -> {
        });
        terms.show();

    }
    private void showPolicy(){
        AlertDialog.Builder policy = new AlertDialog.Builder(this);
        policy.setTitle(getResources().getString(R.string.privacy_policy));
        policy.setMessage(getResources().getString(R.string.policy_about));
        policy.setPositiveButton(R.string.ok, (arg0, arg1) -> {
        });
        policy.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchHomeScreen();
            } else {
                Cardbar.snackBar(getApplicationContext(), "Permission denied.", true).show();
                Cardbar.snackBar(getApplicationContext(), "You will be prompted for permission again, before downloading content.", true).show();
                launchHomeScreen();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
