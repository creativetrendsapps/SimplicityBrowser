package com.creativetrends.simplicity.app.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**Created by Creative Trends on 9/7/2016.*/
public class EasterEggTask extends AsyncTask<Void, Void, String> {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private static final String TAG = "EasterEggTask";

    private final WeakReference<Activity> activityWeakReference;

    public EasterEggTask(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override protected String doInBackground(Void... params) {
        Request request = new Request.Builder().url("http://api.icndb.com/jokes/random?limitTo=[nerdy]").build();
        try {
            Response response = CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                return new JSONObject(response.body().string()).getJSONObject("value").getString("joke");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting a random nerdy joke", e);
        }
        return null;
    }

    @Override protected void onPostExecute(String joke) {
        Activity activity = activityWeakReference.get();
        if (activity != null && !activity.isFinishing() && !TextUtils.isEmpty(joke)) {
            Toast.makeText(activity.getApplicationContext(), Html.fromHtml(joke), Toast.LENGTH_LONG).show();
        }
    }

}