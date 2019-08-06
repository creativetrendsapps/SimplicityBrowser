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
        webView.loadUrl("javascript:var targetNode = document.querySelector('body');\nvar config = { attributes: true, childList: true, subtree: true };\nvar callback = function(mutationsList, observer) {\nvar classname_home = document.querySelectorAll(\"._7ab_\");\nfor (var i = 0; i < classname_home.length; i++) {\nif(classname_home[i].parentNode.hasAttribute(\"href\") || classname_home[i].parentNode.parentNode.hasAttribute(\"href\")) {\nclassname_home[i].parentNode.removeAttribute(\"href\") || classname_home[i].parentNode.parentNode.removeAttribute(\"href\");\nclassname_home[i].addEventListener('click', function(event) {\nvar targetElement = event.target || event.srcElement;\nvar hr = targetElement.querySelector('div i');\nvar style = hr.currentStyle || window.getComputedStyle(hr, false);\nvar bi = style.backgroundImage.slice(4, -1).replace(/\"/g, \"\");\nPhotos.getImage(bi);})}}\nvar classname = document.getElementsByClassName(\"_i81\");\nfor (var i = 0; i < classname.length; i++) {\nvar dd = classname[i].getAttribute(\"data-sheets\");\nif (!dd) {\nclassname[i].setAttribute(\"data-sheets\", \"simple\");\nclassname[i].addEventListener('click', function(event) {\nvar get = this.querySelector('img');\nvar base = get.src;\nPhotos.getImage(base);});}}};\nvar observer = new MutationObserver(callback);\nobserver.observe(targetNode, config);\n");
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
