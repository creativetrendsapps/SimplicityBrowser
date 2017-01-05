package com.creativetrends.simplicity.app.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.webkit.WebView;

public class AppTheme {

    /**Created by Creative Trends Apps on 8/29/2016.*/

    //this will get the themes color primary color
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

    //this will get the themes color primary dark color
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

    //this will get the themes accent color
    public static int fetchColorAccent(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {

            colorAttr = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    //this will allow the use of setting the webview font size
    public static void fontSize(WebView view, Activity activity) {
        boolean xxsmallfont = PreferencesUtility.getInstance(activity).getFont().equals("50");
        boolean xsmallfont = PreferencesUtility.getInstance(activity).getFont().equals("75");
        boolean smallerfont = PreferencesUtility.getInstance(activity).getFont().equals("85");
        boolean smallfont = PreferencesUtility.getInstance(activity).getFont().equals("90");
        boolean defaultfont = PreferencesUtility.getInstance(activity).getFont().equals("100");
        boolean mediumfont = PreferencesUtility.getInstance(activity).getFont().equals("105");
        boolean largefont = PreferencesUtility.getInstance(activity).getFont().equals("115");
        boolean xlfont = PreferencesUtility.getInstance(activity).getFont().equals("125");
        boolean xxlfont = PreferencesUtility.getInstance(activity).getFont().equals("175");

        if (xxsmallfont) {
            view.getSettings().setTextZoom(50);
        }

        if (xsmallfont) {
            view.getSettings().setTextZoom(75);
        }

        if (smallerfont) {
            view.getSettings().setTextZoom(85);
        }

        if (smallfont) {
            view.getSettings().setTextZoom(90);
        }

        if (defaultfont) {
            view.getSettings().setTextZoom(100);
        }

        if (mediumfont) {
            view.getSettings().setTextZoom(105);
        }
        if (largefont) {
            view.getSettings().setTextZoom(110);
        }
        if (xlfont) {
            view.getSettings().setTextZoom(120);
        }
        if (xxlfont) {
            view.getSettings().setTextZoom(150);
        }
    }



}
