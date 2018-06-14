package com.creativetrends.app.simplicity.utils;

import android.content.Context;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by Creative Trends Apps.
 */

public class ExportUtils {
    //Pins Settings
    private static String BOOKMARK_KEY = "simplicity_bookmarks";
    private static String HISTORY_KEY = "simplicity_history";


    public static void writeToFile(File file, Context context) {
        try {
            UserPreferences utils = new UserPreferences(context);
            HashMap<String, Object> map = new HashMap<>();
            map.put(BOOKMARK_KEY, utils.getSimplicityBookmarks());
            map.put(HISTORY_KEY, utils.getSimplicityHistory());

            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(map);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }

    }

    @SuppressWarnings (value="unchecked")
    public static void readFromFile(File file, Context context) {
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(file));
            Object obj = inp.readObject();
            inp.close();
            HashMap<String,Object> map = (HashMap<String, Object>) obj;
            UserPreferences utils = new UserPreferences(context);
            utils.setSimplicityBookmarks(map.get(BOOKMARK_KEY).toString());
            utils.setSimplicityHistory(map.get(HISTORY_KEY).toString());
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}