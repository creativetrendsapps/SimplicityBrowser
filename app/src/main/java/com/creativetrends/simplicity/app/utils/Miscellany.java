package com.creativetrends.simplicity.app.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class Miscellany {

    // get some information about the device (needed for e-mail signature)
    public static String getDeviceInfo(Activity activity) {
        StringBuilder sb = new StringBuilder();

        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), PackageManager.GET_META_DATA);
            String pname = activity.getPackageName();
            PackageManager pm = activity.getPackageManager();
            String installPM = pm.getInstallerPackageName(pname);
            sb.append("\nApp Package Name: ").append(activity.getPackageName());
            sb.append("\nApp Version Name: ").append(pInfo.versionName);
            sb.append("\nPackage Manager: ").append(installPM);
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e("Misc: getDeviceInfo", ex.getMessage());
        }

        sb.append("\nOS Version: ").append(System.getProperty("os.version")).append(" (")
                .append(android.os.Build.VERSION.RELEASE).append(")");
        sb.append("\nOS API Level: ").append(android.os.Build.VERSION.SDK_INT);
        sb.append("\nModel: ").append(android.os.Build.MODEL);
        sb.append("\nManufacturer: ").append(android.os.Build.MANUFACTURER);

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        sb.append("\nScreen: ").append(metrics.heightPixels).append(" x ").append(metrics.widthPixels);
        sb.append("\nLocale: ").append(Locale.getDefault().toString());

        return sb.toString();
    }

}
