/*
 * *
 *  * Created by Jorell Rutledge on 5/13/19 9:34 AM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 5/13/19 9:34 AM
 *
 */

package com.creativetrends.app.simplicity.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.creativetrends.simplicity.app.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.lang.ref.WeakReference;

import static com.creativetrends.app.simplicity.activities.MainActivity.currentVersion;
import static com.creativetrends.app.simplicity.activities.MainActivity.latestVersion;
import static com.creativetrends.app.simplicity.activities.MainActivity.mOverflow;
import static com.creativetrends.app.simplicity.activities.MainActivity.update_root;

public class AppUpdater extends AsyncTask<Void, Void, String> {
    private WeakReference<Context> mContext;
    public AppUpdater(Context context) {
        mContext = new WeakReference<>(context);

    }

    @Override
    protected String doInBackground(Void[] params) {
        try {
            Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.creativetrends.simplicity.app").get();
            Element es = doc.select("div:matchesOwn(^Current Version$)").first().parent().select("span").first();
            latestVersion = es.text();
        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        try {
            if (currentVersion != null && latestVersion != null && currentVersion.compareTo(latestVersion) < 0) {
                showUpdateDialog();
            } else {
                Log.e("Installed version", "is latest");
            }
        }catch (NullPointerException | IllegalArgumentException i){
            i.printStackTrace();
        }catch (Exception z){
            z.printStackTrace();
        }
        super.onPostExecute(string);
    }


    private void showUpdateDialog() {
        Context context = mContext.get();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                update_root.setVisibility(View.VISIBLE);
                mOverflow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_update));
            }
        }, 2500);
    }
}