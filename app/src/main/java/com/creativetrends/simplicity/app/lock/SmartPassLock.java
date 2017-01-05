package com.creativetrends.simplicity.app.lock;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.creativetrends.simplicity.app.R;


public class SmartPassLock extends AppCompatActivity {

    SharedPreferences preferences;

    public static final String TAG = "PinLockView";

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            preferences.edit().putString("smart_lock", pin).apply();
            AlertDialog.Builder close = new AlertDialog.Builder(SmartPassLock.this);
            close.setCancelable(false);
            close.setTitle(getResources().getString(R.string.your_pin) +" "+ pin);
            close.setMessage((getResources().getString(R.string.saved_pin_message)));
            close.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });
            close.setNeutralButton(null, null);
            close.show();
        }


        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            setContentView(R.layout.activity_lock_tablet);
        }else {
            setContentView(R.layout.activity_lock);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        PinLockView mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        assert mPinLockView != null;
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(SmartPassLock.this, R.color.white));
    }


    @Override
    public void onBackPressed(){

    }

}