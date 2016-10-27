package com.creativetrends.simplicity.app.utils;// Created by Creative Trends Apps on 8/23/2016.

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class OnlineStatus {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static OnlineStatus instance = new OnlineStatus();
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    private boolean connected = false;

    public static OnlineStatus getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
}