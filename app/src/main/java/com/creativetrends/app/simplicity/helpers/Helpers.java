package com.creativetrends.app.simplicity.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;

import com.creativetrends.app.simplicity.activities.MainActivity;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by Creative Trends Apps.
 */

public class Helpers {
    private static final String GET_RESULTS = "Unverified";
    private static final String GET_RESULTS_OTHER = "Verified";

    public static String getCookie() {
        try {
            final CookieManager cookieManager = CookieManager.getInstance();
            final String cookies = cookieManager.getCookie("https://m.facebook.com/");
            if (cookies != null) {
                String[] temp = cookies.split(";");
                for (String ar1 : temp) {
                    if (ar1.contains("c_user")) {
                        final String[] temp1 = ar1.split("=");
                        return temp1[1];
                    }
                }
            }
            // Return null as we found no cookie
        }catch (Exception i){
            i.printStackTrace();
        }
        return null;
    }



    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }






    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_STORAGE = 1;
    public static final int REQUEST_LOCATION = 2;







    // request storage permission
    public static void requestStoragePermission(Activity activity) {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission(activity)) {
            Log.e(TAG, "No storage permission at the moment. Requesting...");
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_STORAGE);
        }
    }

    // check is storage permission granted
    public static boolean hasStoragePermission(Activity activity) {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(activity, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }




    public static String decodeImg(String img_url) {
        return img_url.replace("\\3a ", ":").replace("efg\\3d ", "oh=").replace("\\3d ", "=").replace("\\26 ", "&").replace("\\", "").replace("&amp;", "&");
    }

    public static Bitmap getImage(URL url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            //Log.e(LOG_TAG, convertStreamToString(connection.getInputStream()));
            if (responseCode == 200) {
                return BitmapFactory.decodeStream(connection.getInputStream());
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static Bitmap getImage(String urlString) {
        try {
            URL url = new URL(urlString);
            return getImage(url);
        } catch (Exception e) {
            return null;
        }
    }

    /*public static String decodeCover(String img_url) {
        return img_url.replace("\\3a ", ":").replace("efg\\3d ", "oh=").replace("\\3d ", "=").replace("\\26 ", "&").replace("\\", "").replace("&amp;", "&");
    }*/





    public static String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return null;
    }


    public static boolean isEmpty(String string){
        return string == null || string.length() < 1;
    }



}
