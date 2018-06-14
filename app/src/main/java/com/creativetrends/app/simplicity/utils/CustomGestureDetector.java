package com.creativetrends.app.simplicity.utils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.creativetrends.app.simplicity.webview.NestedWebView;

import java.util.HashMap;

/**
 * Created by Creative Trends Apps.
 */

public class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private NestedWebView webview;
    private Context context;

    public CustomGestureDetector(NestedWebView webview, Context context) {
        this.webview = webview;
        this.context = context;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            Values values = new Values(context);
            HashMap<String, Integer> valuesForSwipe = values.getValuesForSwipe();
            if (Math.abs(e1.getY() - e2.getY()) > valuesForSwipe.get("swipeMaxOffPath"))
                return false;
            if (e1.getX() - e2.getX() > valuesForSwipe.get("swipeMinDistance")
                    && Math.abs(velocityX) > valuesForSwipe.get("swipeThresholdVelocity")) {
                if (webview.canGoForward() && webview != null) {
                    webview.goForward();
                }
                return true;
            } else if (e2.getX() - e1.getX() > valuesForSwipe.get("swipeMinDistance")
                    && Math.abs(velocityX) > valuesForSwipe.get("swipeThresholdVelocity")) {
                if (webview.canGoBack() && webview != null) {
                    webview.goBack();
                }
                return true;
            }
        } catch (Exception e) {
            Log.e("gestureExceptio", e.toString());
        }
        return false;
    }
}