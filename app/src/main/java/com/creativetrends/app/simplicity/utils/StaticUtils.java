package com.creativetrends.app.simplicity.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;

/**
 * Created by Creative Trends Apps.
 */

public class StaticUtils {
    private static final int TITLE_COLOR_FALLBACK = Color.parseColor("#212121");
    private static final int TITLE_COLOR_DARK = Color.parseColor("#212121");
    private static final int TITLE_COLOR_LIGHT = Color.parseColor("#ffffff");


    public static int darkColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f;
        return Color.HSVToColor(hsv);
    }

    private static boolean isColorDark(int color){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness < 0.2) {
            return false; // It's a light color
        } else {
            return true; // It's a dark color
        }
    }

    public static int getTitleColor(Palette.Swatch swatch) {
        if (swatch == null) {
            return TITLE_COLOR_FALLBACK;
        }
        if (isColorDark(swatch.getRgb())) {
            return TITLE_COLOR_LIGHT;
        } else {
            return TITLE_COLOR_DARK;
        }
    }

    public static Palette.Swatch getColorSwatch(@Nullable Palette palette) {
        if (palette != null) {
            if (palette.getMutedSwatch() != null) {
                return palette.getMutedSwatch();
            } else if (palette.getLightMutedSwatch() != null) {
                return palette.getLightMutedSwatch();
            } else if (palette.getDarkMutedSwatch() != null) {
                return palette.getDarkMutedSwatch();
            }
        }
        return null;
    }

    public static boolean isColorLight(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        float hsl[] = new float[3];
        ColorUtils.RGBToHSL(red, green, blue, hsl);
        return hsl[2] > 0.5f;
    }

    public static int getColor(Bitmap bitmap, boolean incognito) {
        Palette palette = Palette.from(bitmap).generate();
        final int fallback = Color.TRANSPARENT;
        return incognito ? palette.getMutedColor(fallback) : palette.getVibrantColor(fallback);
    }


    public static int getPositionInTime(long timeMilliSec) {
        long diff = System.currentTimeMillis() - timeMilliSec;

        long hour = 1000 * 60 * 60;
        long day = hour * 24;
        long week = day * 7;
        long month = day * 30;

        return hour > diff ? 0 : day > diff ? 1 : week > diff ? 2 : month > diff ? 3 : 4;
    }

    public static float dpToPx(Resources res, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }


    public static void showKeyboard(View view) {
        InputMethodManager imm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            imm = view.getContext().getSystemService(InputMethodManager.class);
        }
        if (imm != null) {
            imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, 0);
        }
    }


    public static void hideKeyboard(View view) {
        InputMethodManager imm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            imm = view.getContext().getSystemService(InputMethodManager.class);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public static boolean isOreo() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.O;
    }


    public static int fetchColorPrimary(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimary;
        } else {

            colorAttr = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    public static int fetchColorPrimaryDark(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimaryDark;
        } else {

            colorAttr = context.getResources().getIdentifier("colorPrimaryDark", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }


    public static String getAppVersionName(Context context) {
        String res = "0.0.0.0";
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    // method for clearing cache
    public static void deleteCache(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib") && !s.equals("shared_prefs") && !s.equals("databases")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    // helper method for clearing cache
    private static boolean deleteDir(File dir) {
        if (dir == null)
            return false;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success)
                    return false;
            }
        }
        return dir.delete();
    }


    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap out = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        float radius = bitmap.getWidth() / 2;
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return Bitmap.createScaledBitmap(out, 192, 192, true);
    }


}