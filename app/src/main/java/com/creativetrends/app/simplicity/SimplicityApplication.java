package com.creativetrends.app.simplicity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;

import com.creativetrends.simplicity.app.R;


/**
 * Created by Creative Trends Apps.
 *
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
        PreferenceManager.setDefaultValues(mContext, R.xml.flags_settings, true);
        PreferenceManager.setDefaultValues(mContext, R.xml.plugin_settings, true);
        createNotificationChannel();
       

    }

    //notification channel for Simplicity's downloader
    private void createNotificationChannel() {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence widget_name = getString(R.string.notifications_widget);
                String widget_description = getString(R.string.notifications_widget_description);
                int wimp = NotificationManager.IMPORTANCE_LOW;

                NotificationChannel channel5 = new NotificationChannel(getString(R.string.notification_widget_channel), widget_name, wimp);
                channel5.setDescription(widget_description);
                channel5.setShowBadge(true);
                channel5.enableVibration(false);
                channel5.enableLights(true);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                assert notificationManager != null;
                if (notificationManager.getNotificationChannel(getString(R.string.notification_widget_channel)) == null) {
                    notificationManager.createNotificationChannel(channel5);

                }

            }
    }

}
