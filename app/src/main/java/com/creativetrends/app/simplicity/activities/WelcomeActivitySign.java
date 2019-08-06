package com.creativetrends.app.simplicity.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.PrefManager;
import com.creativetrends.simplicity.app.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.button.MaterialButton;

public class WelcomeActivitySign extends AppCompatActivity implements View.OnClickListener {
    private PrefManager prefManager;
    MaterialButton no;
    MaterialButton sign;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_sign);
        prefManager = new PrefManager(this);

        no = findViewById(R.id.welcome_no);
        sign = findViewById(R.id.welcome_sign);
        no.setOnClickListener(this);
        sign.setOnClickListener(this);
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

        switch(v.getId()){
            case R.id.welcome_no:
                launchHomeScreen();
                break;

            case R.id.welcome_sign:
                launchSimpleScreen();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivitySign.this, MainActivity.class));
        finish();
    }

    private void launchSimpleScreen() {
        prefManager.setFirstTimeLaunch(false);
        if (checkPlayServices()) {
            Intent account = new Intent(WelcomeActivitySign.this, SimplicityAccount.class);
            startActivity(account);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                noPlayServices();
            } else {
                Cardbar.snackBar(this, "This device is not supported.", true).show();
            }
            return false;
        }
        return true;
    }

    private void noPlayServices(){
        AlertDialog.Builder policy = new AlertDialog.Builder(this);
        policy.setTitle("Cannot Create Account");
        policy.setMessage("Play Services was not found on your device. To create a Simplicity Account, Play Services needs to be enabled, or installed.");
        policy.setPositiveButton(R.string.ok, (arg0, arg1) -> launchHomeScreen());
        policy.show();
    }
}
