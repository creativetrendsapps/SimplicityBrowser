package com.creativetrends.app.simplicity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.creativetrends.simplicity.app.BuildConfig;
import com.creativetrends.simplicity.app.R;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;

import androidx.multidex.MultiDex;

/**
 * Created by Creative Trends Apps.
 *
 */
@AcraCore(buildConfigClass = BuildConfig.class)
@AcraMailSender(mailTo = "mail@host.com")
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        ACRA.init(this);
    }
}