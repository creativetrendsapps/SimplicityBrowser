package com.creativetrends.simplicity.app.utils;

import android.graphics.Bitmap;
import android.webkit.WebView;


import com.creativetrends.simplicity.app.activities.NewWindow;
import com.creativetrends.simplicity.app.ui.WebViewScroll;


public class NewListener implements WebViewScroll.Listener {
    private final WebViewScroll webView;
    private final NewWindow mActivity;
    public NewListener(NewWindow activity, WebView view) {
        mActivity = activity;
        webView = (WebViewScroll) view;

    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {
    }


    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
    }

    @Override
    public void onExternalPageRequest(String url) {


    }




    @Override
    public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
    }





}


