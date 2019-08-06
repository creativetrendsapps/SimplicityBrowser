package com.creativetrends.app.simplicity.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.creativetrends.app.simplicity.activities.ReadingActivity;

public class ReaderHandler {
    private Context context;
    private Activity activity;

    public ReaderHandler(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @JavascriptInterface
    public void processReaderMode(String text, String title) {
        Intent intent = new Intent(activity, ReadingActivity.class);
        intent.putExtra("text", text);
        intent.putExtra("title",title);
        activity.startActivity(intent);
    }
}
