package com.creativetrends.simplicity.app.utils;

/**Created by Creative Trends Apps on 8/23/2016.*/

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.creativetrends.simplicity.app.activities.SimplicityApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class PreferencesUtility {
    private static final String FONT_SIZE = "font_pref";
    private static final String DEFAULT_LANGUAGE = "folio_language";
    private static SharedPreferences mPreferences;
    private static PreferencesUtility sInstance;


    private PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    static PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }



    public static String getString(String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).getString(key, defValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).edit();
        editor.putString(key, value);
        editor.apply();
    }



    String getLang() {
        return mPreferences.getString(DEFAULT_LANGUAGE, "");
    }


    String getFont() {
        return mPreferences.getString(FONT_SIZE, "default_font");
    }

    public static ArrayList<Bookmark> getBookmarks() {
        String bookmarks = getString("simplicity_bookmarks", "[]");
        ArrayList<Bookmark> listBookmarks = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(bookmarks);
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(ob.getString("title"));
                bookmark.setUrl(ob.getString("url"));
                listBookmarks.add(bookmark);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listBookmarks;
    }

    public static void saveBookmarks(ArrayList<Bookmark> listBookmarks) {
        JSONArray array = new JSONArray();
        Iterator it = listBookmarks.iterator();
        if (it.hasNext()) {
            do {
                Bookmark bookmark = (Bookmark) it.next();
                JSONObject ob = new JSONObject();
                try {
                    ob.put("title", bookmark.getTitle());
                    ob.put("url", bookmark.getUrl());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(ob);
            } while (it.hasNext());
        }
        putString("simplicity_bookmarks", array.toString());
    }


}