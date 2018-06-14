package com.creativetrends.app.simplicity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.creativetrends.simplicity.app.R;

/**
 * Created by Creative Trends Apps.
 */
public class SimplicityApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;

    public static Context getContextOfApplication() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        PreferenceManager.setDefaultValues(mContext, R.xml.settings, true);
        PreferenceManager.setDefaultValues(mContext, R.xml.flags, true);
        checkAppReplacingState();
    }

    private void checkAppReplacingState(){
        if (getResources() == null){
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}