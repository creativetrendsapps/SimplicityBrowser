package com.creativetrends.simplicity.app.utils;// Created by Creative Trends Apps on 8/29/2016.

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.TypedValue;
import android.webkit.WebView;

import java.util.Locale;

public class AppTheme {
    public WebView webview;


    public static void setLanguage(Activity activity) {
        boolean English = PreferencesUtility.getInstance(activity).getLang().equals("en");
        boolean Catalan = PreferencesUtility.getInstance(activity).getLang().equals("ca");
        boolean Spanish = PreferencesUtility.getInstance(activity).getLang().equals("es");
        boolean French = PreferencesUtility.getInstance(activity).getLang().equals("fr");
        boolean Croatian = PreferencesUtility.getInstance(activity).getLang().equals("hr");
        boolean Italian = PreferencesUtility.getInstance(activity).getLang().equals("it");
        boolean Dutch = PreferencesUtility.getInstance(activity).getLang().equals("nl");
        boolean Polish = PreferencesUtility.getInstance(activity).getLang().equals("pl");
        boolean Brazilian = PreferencesUtility.getInstance(activity).getLang().equals("pt-rBR");
        boolean Romanian = PreferencesUtility.getInstance(activity).getLang().equals("ro");
        boolean Russian = PreferencesUtility.getInstance(activity).getLang().equals("ru");
        boolean Turkish = PreferencesUtility.getInstance(activity).getLang().equals("tr");
        boolean Simplified = PreferencesUtility.getInstance(activity).getLang().equals("zh-rCN");
        boolean Traditional = PreferencesUtility.getInstance(activity).getLang().equals("zh-rTW");

        if (English) {
            Locale en = new Locale("en");
            Locale.setDefault(en);
            Configuration config = new Configuration();
            //noinspection deprecation
            config.locale = en;
            activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
        }
        if (Croatian) {
            Locale hr = new Locale("hr");
            Locale.setDefault(hr);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = hr;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Spanish) {
            Locale es = new Locale("es");
            Locale.setDefault(es);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = es;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }
        if (French) {
            Locale fr = new Locale("fr");
            Locale.setDefault(fr);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = fr;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Italian) {
            Locale it = new Locale("it");
            Locale.setDefault(it);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = it;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Dutch) {
            Locale nl = new Locale("nl");
            Locale.setDefault(nl);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = nl;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Polish) {
            Locale pl = new Locale("pl");
            Locale.setDefault(pl);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = pl;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Brazilian) {
            Locale br = new Locale("pt", "BR");
            Locale.setDefault(br);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = br;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Romanian) {
            Locale ro = new Locale("ro");
            Locale.setDefault(ro);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = ro;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Russian) {
            Locale ru = new Locale("ru");
            Locale.setDefault(ru);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = ru;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Turkish) {
            Locale tr = new Locale("tr");
            Locale.setDefault(tr);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = tr;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Simplified) {
            Locale s = new Locale("zh", "CN");
            Locale.setDefault(s);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = s;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Traditional) {
            Locale t = new Locale("zh", "TW");
            Locale.setDefault(t);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = t;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

        if (Catalan) {
            Locale ca = new Locale("ca");
            Locale.setDefault(ca);
            Configuration config3 = new Configuration();
            //noinspection deprecation
            config3.locale = ca;
            activity.getBaseContext().getResources().updateConfiguration(config3, activity.getBaseContext().getResources().getDisplayMetrics());
        }

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
