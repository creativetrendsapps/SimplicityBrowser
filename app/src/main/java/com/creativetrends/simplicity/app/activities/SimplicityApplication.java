package com.creativetrends.simplicity.app.activities;// Created by Creative Trends Apps on 8/23/2016.

import android.app.Application;
import android.content.Context;

import com.creativetrends.simplicity.app.utils.AdBlock;

public class SimplicityApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
        AdBlock.init(this);
    }

    public static Context getContextOfApplication() {
        return mContext;
    }
}