package com.creativetrends.app.simplicity.webview;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import java.io.InputStream;


@SuppressWarnings (value="unused")
public class CSSInjection {

    static void injectInstaTheme(Context context, WebView view) {
        instagramLight(context, view);
        Log.d("Plugin enabled", "on " + view.getUrl());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void instagramLight(Context context, WebView view) {
        try {
            InputStream inputStream = context.getAssets().open("instagram.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void injectPhotos(WebView webView){
        Log.d("Plugin enabled", "on " + webView.getUrl());
    }




  @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void injectDarkMode(Context context, WebView view) {
        try {
            InputStream inputStream = context.getAssets().open("darkmode.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
