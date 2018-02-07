package com.creativetrends.app.simplicity.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.creativetrends.app.simplicity.SimplicityApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Creative Trends Apps.
 */

public class UserPreferences {
    private static final String FONT_SIZE = "font_pref";
    private static SharedPreferences mPreferences;
    private static UserPreferences sInstance;


    private UserPreferences(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static UserPreferences getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new UserPreferences(context.getApplicationContext());
        }
        return sInstance;
    }


    private static String getString(String key) {
        return PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).getString(key, "[]");
    }

    private static void putString(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).edit();
        editor.putString(key, value);
        editor.apply();
    }



    public String getFont() {
        return mPreferences.getString(FONT_SIZE, "default_font");
    }


    //bookmarks
    public static ArrayList<Bookmark> getBookmarks() {
        String bookmarks = getString("simplicity_bookmarks");
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


    //history
    public static ArrayList<History> getHistory() {
        String historyItem = getString("simplicity_history");
        ArrayList<History> listBookmarks = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(historyItem);
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                History history = new History();
                history.setTitle(ob.getString("title"));
                history.setUrl(ob.getString("url"));
                listBookmarks.add(history);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listBookmarks;
    }

    public static void saveHistory(ArrayList<History> listBookmarks) {
        JSONArray array = new JSONArray();
        Iterator it = listBookmarks.iterator();
        if (it.hasNext()) {
            do {
                History bookmark = (History) it.next();
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
        putString("simplicity_history", array.toString());
    }
    
    public static boolean isStarred(String bookmark) {
        if (bookmark == null || bookmark.isEmpty()) {
            return false;
        }
        String removeStart = removeStart(bookmark);
        for (Bookmark url : getBookmarks()) {
            if (removeStart(url.getUrl()).equals(removeStart)) {
                return true;
            }
        }
        return false;
    }

    private static String removeStart(String url) {
        return url.replaceFirst("^(http(?>s)://\\.|http(?>s)://)", "");
    }

}
