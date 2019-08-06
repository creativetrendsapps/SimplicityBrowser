package com.creativetrends.app.simplicity.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DonateActivity extends BaseActivity implements View.OnClickListener, AppCompatSeekBar.OnSeekBarChangeListener , BillingProcessor.IBillingHandler {
    Toolbar mToolbar;
    AppCompatTextView amount, description;
    CardView pay;
    AppCompatSeekBar seekBar;
    int stepSize = 1;
    BillingProcessor bp;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(UserPreferences.getBoolean("dark_mode", false)){
            setTheme(R.style.SettingsThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        bp = BillingProcessor.newBillingProcessor(this, "", this);
        bp.initialize();
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
        }
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

    @Override
    protected void onStart() {
        super.onStart();
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
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //noinspection IntegerDivisionInFloatingPointContext
        progress = Math.round(progress / stepSize) * stepSize;
        seekBar.setProgress(progress);
        switch (progress) {
            case 0:
                
                break;

            case 1:
                break;

            case 2:
                
                break;

            case 3:
               
                break;
            case 4:
                break;
            case 5:
                
                break;

            default:
                
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
        switch (seekBar.getProgress()) {
            case 0:
                
                break;

            case 1:
                
                break;

            case 2:
                
                break;

            case 3:
                
                break;
            case 4:
                
                break;
            case 5:
                
                break;

            default:
                
                break;

        }

    }

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        Toast.makeText(this, DXDecryptorxWPYWsyI.decode("LDxEFcSiTykFpTcUcR9JDh4L3wMtz6AD22qEow==")/*"Thank you for your donation!"*/, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        Log.d(DXDecryptorxWPYWsyI.decode("Kz1IC8PrVS8E/A==")/*"Simplicity"*/, DXDecryptorxWPYWsyI.decode("Gj1JF8bsUWYZ6zgPal5cCBEcmw==")/*"billing initialized"*/);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

class DXDecryptorxWPYWsyI {

    static String decode(String s) {
        String str;
        String key = "Y35Iz7UjKpLDA6r+q3TYGQ==";
        try {
            String algo = "ARCFOUR";
            Cipher rc4 = Cipher.getInstance(algo);
            String kp = "7hZIAuFU4kD5kLrc";
            Key kpk = new SecretKeySpec(kp.getBytes(), algo);
            rc4.init(Cipher.DECRYPT_MODE, kpk);
            byte[] bck = Base64.decode(key, Base64.DEFAULT);
            byte[] bdk = rc4.doFinal(bck);
            Key dk = new SecretKeySpec(bdk, algo);
            rc4.init(Cipher.DECRYPT_MODE, dk);
            byte[] bcs = Base64.decode(s, Base64.DEFAULT);
            byte[] byteDecryptedString = rc4.doFinal(bcs);
            str = new String(byteDecryptedString);
        } catch (Exception e) {
            str = "";
        }
        return str;
    }

}
