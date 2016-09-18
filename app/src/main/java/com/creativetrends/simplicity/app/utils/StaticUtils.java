package com.creativetrends.simplicity.app.utils;// Created by Creative Trends Apps on 8/23/2016.

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;

public class StaticUtils {

    @Nullable
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) return ((BitmapDrawable) drawable).getBitmap();
        if (drawable instanceof VectorDrawableCompat)
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        int width = drawable.getBounds().isEmpty() ? drawable.getIntrinsicWidth() : drawable.getBounds().width();
        int height = drawable.getBounds().isEmpty() ? drawable.getIntrinsicHeight() : drawable.getBounds().height();

        Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Drawable getVectorDrawable(Context context, int resId) {
        VectorDrawableCompat drawable;
        try {
            drawable = VectorDrawableCompat.create(context.getResources(), resId, context.getTheme());
        } catch (Exception e) {
            e.printStackTrace();
            return new ColorDrawable(Color.TRANSPARENT);
        }

        if (drawable != null)
            return drawable.getCurrent();
        else
            return new ColorDrawable(Color.TRANSPARENT);
    }

    public static int darkColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f;
        return Color.HSVToColor(hsv);
    }

    public static boolean isColorDark(int color) {
        double darkness = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    public static void restart(Context context) {
        try {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            int mPendingIntentId = 223344;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, i, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}