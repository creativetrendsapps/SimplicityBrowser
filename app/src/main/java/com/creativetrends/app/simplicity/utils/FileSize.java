package com.creativetrends.app.simplicity.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.creativetrends.app.simplicity.activities.MainActivity;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

/**
 * Created by Creative Trends Apps.
 */

public class FileSize extends AsyncTask<Void, Void, Void> {

    private String mUrl;
    private int fileLength;
    private static final String TAG;
    static {
        TAG = FileSize.class.getSimpleName();
    }
    public FileSize(String url) {
        mUrl = url;

    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(mUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            fileLength = url.openConnection().getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.file_size_main = getFileSize((long) fileLength);
        Log.d(TAG, getFileSize((long) fileLength));
    }

    private static String getFileSize(long size){
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}