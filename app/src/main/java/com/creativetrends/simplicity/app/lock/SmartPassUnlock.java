package com.creativetrends.simplicity.app.lock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.creativetrends.simplicity.app.R;

public class SmartPassUnlock extends AppCompatActivity {
    SharedPreferences preferences;

    public static final String TAG = "PinLockView";

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            //View parentLayout = findViewById(android.R.id.content);
            if (pin.equals( preferences.getString("smart_lock", ""))){
                finish();
                preferences.edit().putString("needs_lock", "false").apply();
            }else{
                recreate();
                //Snackbar.make(parentLayout, R.string.lock_wrong, Snackbar.LENGTH_LONG).show();
                Toast.makeText(getBaseContext(), R.string.lock_wrong, Toast.LENGTH_SHORT).show();
            }
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
            setContentView(R.layout.activity_unlock_tablet);
            }else {
            setContentView(R.layout.activity_unlock);
            }
            preferences = PreferenceManager.getDefaultSharedPreferences(this);


        PinLockView mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        assert mPinLockView != null;
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);

        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(SmartPassUnlock.this, R.color.white));
    }


    @Override
    public void onBackPressed(){

    }

}