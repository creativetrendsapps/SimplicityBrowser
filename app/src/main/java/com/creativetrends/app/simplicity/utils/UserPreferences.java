package com.creativetrends.app.simplicity.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.adapters.BookmarkItems;
import com.creativetrends.app.simplicity.adapters.HistoryItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Creative Trends Apps.
 */

public final class UserPreferences {
    private static final String FONT_SIZE = "font_pref";
    private static final String TOOLBAR_STYLE = "address_bar";
    private static final String SIMPLICITY_BOOKMARKS = "simplicity_bookmarks";
    private static final String SIMPLICITY_HISTORY = "simplicity_history";
    private static final String CUSTOM_FONT_PREF = "custom_font";
    private static SharedPreferences mPreferences;
    @SuppressLint("StaticFieldLeak")
    private static UserPreferences sInstance;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;


    public static ArrayList<String> list = new ArrayList<>();
    UserPreferences(final Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static UserPreferences getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new UserPreferences(context.getApplicationContext());
        }
        return sInstance;
    }



    public static boolean getBoolean(String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).getBoolean(key, defValue);
    }

    @SuppressLint("ApplySharedPref")
    public static boolean putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).edit();
        editor.putBoolean(key, value);
        editor.commit();
        return value;
    }

    public static String getString(String key) {
        return PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).getString(key, "[]");
    }

    @SuppressWarnings (value="unused")
    public static int getInt(String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).getInt(key, defValue);
    }


    public static String getString(String key, String value) {
        return PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).getString(key, value);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void clearPrefrence(String key) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).edit();
        editor.remove(key);
        editor.apply();
    }

    @SuppressWarnings (value="unused")
    public static int putInt(String key, int defValue) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SimplicityApplication.getContextOfApplication()).edit();
        editor.putInt(key, defValue);
        editor.apply();
        return defValue;
    }


    public String getTabs() {
        return mPreferences.getString(TOOLBAR_STYLE, "top");
    }

    @SuppressWarnings (value="unused")
    public String getCustomFontPref() {
        return mPreferences.getString(CUSTOM_FONT_PREF, "");
    }

    public String getFont() {
        return mPreferences.getString(FONT_SIZE, "default_font");
    }


    public static ArrayList<String> getSimpleDownloads() {
        String bookmarks = PreferenceManager.getDefaultSharedPreferences(mContext).getString("simple_downloads", "[]");
        ArrayList<String> listFilters = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(bookmarks);
            for (int i = 0; i < array.length(); i++) {
                String filter = (String) array.get(i);
                listFilters.add(filter);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listFilters;
    }


    public static void saveSimpleDownloads(ArrayList<String> listFilters) {
        JSONArray array = new JSONArray();
        for (String string : listFilters) {
            array.put(string);
        }
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("simple_downloads", array.toString()).apply();
    }


    //bookmarks
    public static ArrayList<BookmarkItems> getBookmarks() {
        String bookmarks = getString("simplicity_bookmarks");
        ArrayList<BookmarkItems> listBookmarks = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(bookmarks);
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                BookmarkItems bookmark = new BookmarkItems();
                bookmark.setTitle(ob.getString("title"));
                bookmark.setUrl(ob.optString("url"));
                bookmark.setLetter(ob.getString("letter"));
                bookmark.setImage(ob.getInt("color"));
                listBookmarks.add(bookmark);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listBookmarks;
    }

    public static void saveBookmarks(ArrayList<BookmarkItems> listBookmarks) {
        JSONArray array = new JSONArray();
        Iterator it = listBookmarks.iterator();
        if (it.hasNext()) {
            do {
                BookmarkItems bookmark = (BookmarkItems) it.next();
                JSONObject ob = new JSONObject();
                try {
                    ob.put("title", bookmark.getTitle());
                    ob.put("url", bookmark.getUrl());
                    ob.put("letter", bookmark.getLetter());
                    ob.put("color", bookmark.getImage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(ob);
            } while (it.hasNext());
        }
        putString("simplicity_bookmarks", array.toString());
    }


    //history
    public static ArrayList<HistoryItems> getHistory() {
        String historyItem = getString("simplicity_history");
        ArrayList<HistoryItems> listBookmarks = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(historyItem);
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                HistoryItems history = new HistoryItems();
                history.setTitle(ob.getString("title"));
                history.setUrl(ob.getString("url"));
                history.setDate(ob.optString("date"));
                listBookmarks.add(history);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listBookmarks;
    }

    public static void saveHistory(ArrayList<HistoryItems> listBookmarks) {
        JSONArray array = new JSONArray();
        Iterator it = listBookmarks.iterator();
        if (it.hasNext()) {
            do {
                HistoryItems bookmark = (HistoryItems) it.next();
                JSONObject ob = new JSONObject();
                try {
                    ob.put("title", bookmark.getTitle());
                    ob.put("url", bookmark.getUrl());
                    ob.put("date", bookmark.getDate());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(ob);
            } while (it.hasNext());
        }
        putString("simplicity_history", array.toString());
    }


    String getSimplicityBookmarks() {
        return mPreferences.getString(SIMPLICITY_BOOKMARKS, "");
    }

    void setSimplicityBookmarks(String marks) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(SIMPLICITY_BOOKMARKS, marks).apply();
    }

    String getSimplicityHistory() {
        return mPreferences.getString(SIMPLICITY_HISTORY , "");
    }

    void setSimplicityHistory(String his) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(SIMPLICITY_HISTORY , his).apply();
    }


    public static boolean isStarred(String bookmark) {
        if (bookmark == null || bookmark.isEmpty()) {
            return false;
        }
        String removeStart = removeStart(bookmark);
        for (BookmarkItems url : getBookmarks()) {
            if (removeStart(url.getUrl()).equals(removeStart)) {
                return true;
            }
        }
        return false;
    }

    private static String removeStart(String url) {
        return url.replaceFirst("^(http(?>s)://\\.|http(?>s)://)", "");
    }



    public static boolean isHistory(String history) {
        if (history == null || history.isEmpty()) {
            return false;
        }
        String removeStart = getHome(history);
        for (HistoryItems url : getHistory()) {
            if (getHome(url.getUrl()).equals(removeStart)) {
                return true;
            }
        }
        return false;
    }

    private static String getHome(String url) {
        return url.replaceFirst("^(http(?>s)://\\.|http(?>s)://)", "");
    }


    public static boolean isDangerousFileExtension(String url) {
        int index = url.indexOf("?");
        if (index > -1) {
            url = url.substring(0, index);
        }
        url = url.toLowerCase();

        for (String type : DOWNLOAD_FILE_TYPES) {
            if (url.endsWith(type)) return true;
        }

        return false;
    }

    private static final String[] DOWNLOAD_FILE_TYPES = {
            ".apk", ".exe", ".jar", ".bat", ".xls",
            ".js", ".sh", ".bin", "app", "com", "scr",
            "vbs", "cmd", "xlsx", "docx", "pdf", "msi", "dmg",
            "wsh", "wsf", "ws", "vbscript", "workflow", "vbe", "vs",
            "run", "rgs", "reg", "ps1", "paf", "pif", "osx", "out",
            "lnk", "jse", "job", "ksh", "ipa", "csh", "command", "action",
            "chm",
    };

}
