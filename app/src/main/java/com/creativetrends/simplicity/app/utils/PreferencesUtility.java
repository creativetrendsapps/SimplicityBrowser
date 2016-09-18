package com.creativetrends.simplicity.app.utils;

// Created by Creative Trends Apps on 8/23/2016.

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.creativetrends.simplicity.app.activities.SimplicityApplication;

public class PreferencesUtility {


    private static PreferencesUtility sInstance;

    public static SharedPreferences mPreferences;
    private static final String USER_BOOKMARKS = "userBookmarks";


    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    public static SharedPreferences getUserPreferences(Context context) {
        return context.getSharedPreferences("com.creativetrends.simplicity.app", 0);
    }

    public static SharedPreferences.Editor getUserPreferencesEditor(Context context) {
        return context.getSharedPreferences("com.creativetrends.simplicity.app", 0).edit();
    }


    public static boolean getBoolean(String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).getBoolean(key, defValue);
    }

    public static String getString(String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).getString(key, defValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).edit();
        editor.remove(key);
        editor.apply();
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
}
    /**public static ArrayList<Bookmarks> getBookmarks(Context context) {
        String bookmarks = getUserPreferences(context).getString(USER_BOOKMARKS, "[]");
        ArrayList<Bookmarks> listBookmarks = new ArrayList();
        try {
            JSONArray array = new JSONArray(bookmarks);
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                Bookmarks bookmark = new Bookmarks();
                bookmark.setTitle(ob.getString("title"));
                bookmark.setUrl(ob.getString("url"));
                listBookmarks.add(bookmark);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listBookmarks;
    }

    public static void saveBookmarks(Context context, ArrayList<Bookmarks> listBookmarks) {
        JSONArray array = new JSONArray();
        Iterator it = listBookmarks.iterator();
        while (it.hasNext()) {
            Bookmarks bookmark = (Bookmarks) it.next();
            JSONObject ob = new JSONObject();
            try {
                ob.put("title", bookmark.getTitle());
                ob.put("url", bookmark.getUrl());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(ob);
        }
        getUserPreferencesEditor(context).putString(USER_BOOKMARKS, array.toString()).apply();
     }

     */
